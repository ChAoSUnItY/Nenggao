package chaos.unity.nenggao;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import com.sun.jna.Function;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.*;

@SuppressWarnings("unused")
public class FileReportBuilder {
    private @Nullable String filePath;
    private @NotNull String sourceName;
    /**
     * The actual source file to retrieve, will be read by {@link Source#fromFile(File)}
     */
    private final @Nullable File sourceFile;
    private final @Nullable String source;
    private boolean enableColor = true;
    private @NotNull CharacterSet characterSet = CharacterSet.UNICODE;
    private final @NotNull List<@NotNull Report> reports = new ArrayList<>();

    private FileReportBuilder(@NotNull File sourceFile) {
        try {
            this.filePath = sourceFile.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            this.filePath = sourceFile.getPath();
        }
        this.sourceFile = sourceFile;
        this.sourceName = filePath;
        this.source = null;
    }

    private FileReportBuilder(@NotNull String source) {
        this.filePath = null;
        this.sourceFile = null;
        this.sourceName = "Unknown";
        this.source = source.endsWith("\n") ? source : source + "\n";
    }

    public static @NotNull FileReportBuilder sourceFile(@NotNull File sourceFile) {
        return new FileReportBuilder(sourceFile);
    }

    public static @NotNull FileReportBuilder source(@NotNull String source) {
        return new FileReportBuilder(source);
    }

    public @NotNull FileReportBuilder report(@NotNull Report report) {
        this.reports.add(report);
        return this;
    }

    public @NotNull ReportBuilder warning(@NotNull Span span, @NotNull String message, @Nullable Object... args) {
        return new ReportBuilder(this, new Warning(span, String.format(message, args)));
    }

    public @NotNull FileReportBuilder warning(@NotNull Warning warning) {
        this.reports.add(warning);
        return this;
    }

    public @NotNull ReportBuilder error(@NotNull Span span, @NotNull String message, @Nullable Object... args) {
        return new ReportBuilder(this, new Error(span, String.format(message, args)));
    }

    public @NotNull FileReportBuilder error(@NotNull Error error) {
        this.reports.add(error);
        return this;
    }

    public @NotNull FileReportBuilder sourceName(@NotNull String sourceName) {
        this.sourceName = sourceName;
        return this;
    }

    public @NotNull FileReportBuilder characterSet(@NotNull CharacterSet characterSet) {
        this.characterSet = characterSet;
        return this;
    }

    public @NotNull FileReportBuilder relativePath(@NotNull Path parentPath) {
        if (sourceFile == null)
            throw new IllegalStateException("Failed to relativize path of source file, source file must not be null.");

        this.filePath = parentPath.relativize(sourceFile.toPath()).toString();
        return this;
    }

    public @NotNull FileReportBuilder enableColor(boolean enable) {
        this.enableColor = enable;
        return this;
    }

    /**
     * Print out all stored reports.
     *
     * @param printStream the output stream to print reports
     */
    public void print(final @NotNull PrintStream printStream) {
        enableWindows10AnsiSupport();

        Source source = sourceFile != null ? SourceCache.INSTANCE.getOrAdd(sourceFile) :
                this.source != null ? Source.fromString(this.source) : null;

        if (source == null) {
            throw new IllegalStateException("Unable to fetch source to print");
        }

        for (Report report : reports) {
            report.labels.sort(Comparator.comparingInt(l -> l.span.startPosition.pos)); // Sort label's order so the render algorithm won't mess up
            int maxNumbersOfDigit = (int) Math.max(Math.log10(report.commonSpan.startPosition.line) + 1, Math.log10(report.commonSpan.endPosition.line) + 1);

            // Used for mapping multi-line diagnostic spacing
            // Label instance - Is Occupied
            Map<Label, Boolean> occupiedMultiLineLabels = new LinkedHashMap<>();
            List<Line> segment = source.slice(report.commonSpan.startPosition.line, report.commonSpan.endPosition.line);

            switch (report.type) {
                case WARNING:
                    writeColor(printStream, Attribute.YELLOW_TEXT());
                    break;
                case ERROR:
                    writeColor(printStream, Attribute.RED_TEXT());
                    break;
            }

            printStream.format("[%s] ", report.getTag());

            writeReset(printStream);
            printStream.append(report.message);
            printStream.append('\n');

            writeSourceLocation(printStream, maxNumbersOfDigit, report.commonSpan.startPosition);

            for (Label label : report.labels)
                if (label.isMultiLine())
                    occupiedMultiLineLabels.put(label, false);

            boolean previousLineRendered = true, renderSource;
            Label currentDominantLabel = null;
            for (Line line : segment) {
                StringBuilder lineBuilder = new StringBuilder(line.chars);
                List<Label> appliedLabels = new LinkedList<>();
                int insertedLen = 0, mostLastPosition = line.len + 1;
                renderSource = false;

                for (Label label : report.labels) {
                    if (label.isIn(line.lineNumber)) {
                        if (!label.isMultiLine()) {
                            if (label.format != null && enableColor) {
                                int originalStringPos = label.span.startPosition.pos;
                                String ansiCode = Ansi.generateCode(label.format);
                                lineBuilder.insert(insertedLen + originalStringPos, ansiCode);
                                insertedLen += ansiCode.length();
                                lineBuilder.insert(insertedLen + (originalStringPos += label.span.offset()), Ansi.RESET);
                                insertedLen += Ansi.RESET.length();

                                if (currentDominantLabel != null && currentDominantLabel.format != null) {
                                    lineBuilder.insert(insertedLen + originalStringPos, Ansi.generateCode(currentDominantLabel.format));
                                }
                            }

                            mostLastPosition = Math.max(mostLastPosition, label.span.endPosition.pos + 2);
                            appliedLabels.add(label);
                        } else {
                            if (label.format != null && enableColor) {
                                String ansiCode = Ansi.generateCode(label.format);
                                int startPos = label.span.startPosition.line == line.lineNumber ? label.span.startPosition.pos : 0;
                                lineBuilder.insert(insertedLen + startPos, ansiCode);
                                insertedLen += ansiCode.length();
                                lineBuilder.insert(insertedLen + (line.chars.length() - startPos), Ansi.RESET);
                            }

                            occupiedMultiLineLabels.computeIfPresent(label, (l, occupied) -> true);

                            currentDominantLabel = label;
                        }
                    }

                    if ((label.span.startPosition.line >= line.lineNumber - 1 && label.span.startPosition.line <= line.lineNumber + 1) ||
                            (label.span.endPosition.line >= line.lineNumber - 1 && label.span.endPosition.line <= line.lineNumber + 1)) {
                        renderSource = true;
                    }
                }

                if (!renderSource) {
                    if (previousLineRendered) {
                        writeColor(printStream, Attribute.BRIGHT_BLACK_TEXT());
                        printStream.format("%" + maxNumbersOfDigit + "s %s ", "", characterSet.verticalEllipsis);
                        writeReset(printStream);
                        writeMultiLineLabel(printStream, -1, occupiedMultiLineLabels, null, characterSet.verticalEllipsis);
                        printStream.println();
                        previousLineRendered = false;
                    }
                    continue;
                }

                previousLineRendered = true;

                writeLineNumber(printStream, line.lineNumber, maxNumbersOfDigit, false);
                Label endedLabel = writeMultiLineLabel(printStream, line.lineNumber, occupiedMultiLineLabels, null, characterSet.verticalBar);

                printStream.append(lineBuilder.toString());

                insertedLen = 0;
                if (!appliedLabels.isEmpty()) {
                    // Render Underline
                    writeLineNumber(printStream, -1, maxNumbersOfDigit, true);
                    writeMultiLineLabel(printStream, -1, occupiedMultiLineLabels, null, characterSet.verticalBar);

                    // Render under bars
                    for (Label label : appliedLabels) {
                        int spaceLen = label.span.startPosition.pos - insertedLen;
                        if (spaceLen > 0) // Prevent unnecessary padding
                            printStream.append(new String(new char[spaceLen]).replace('\0', ' '));

                        int offset = label.span.offset();
                        StringBuilder underlineBuilder = new StringBuilder(offset);
                        for (int k = 0; k < offset; k++) {
                            if (offset / 2 == k) {
                                underlineBuilder.append(characterSet.underBar);
                            } else underlineBuilder.append(characterSet.underline);
                        }

                        boolean reset = writeColor(printStream, label.format);
                        printStream.append(underlineBuilder.toString());
                        if (reset) writeReset(printStream);
                        insertedLen += spaceLen + offset;
                    }

                    printStream.append('\n');

                    // Render lines, arrows, and label message
                    for (int j = 1; j < appliedLabels.size() * 2; j++) {
                        writeLineNumber(printStream, -1, maxNumbersOfDigit, true);
                        writeMultiLineLabel(printStream, -1, occupiedMultiLineLabels, null, characterSet.verticalBar);

                        insertedLen = 0;
                        for (int k = 0; k < appliedLabels.size(); k++) {
                            Label label = appliedLabels.get(k);

                            // Check if it's null, this happens after we set label to null when it's marked printed
                            if (label == null)
                                continue;

                            int spaceLen = label.span.startPosition.pos - insertedLen, offset = label.span.offset() / 2;

                            printStream.append(new String(new char[spaceLen + offset]).replace('\0', ' '));

                            insertedLen += spaceLen + offset + 1; // 1 is vertical bar

                            if (j % 2 == 1) {
                                appliedLabels.set(k, null); // Mark printed
                                boolean reset = writeColor(printStream, label.format);
                                printStream.append(String.valueOf(characterSet.leftBottom));
                                printStream.append(new String(new char[mostLastPosition - insertedLen]).replace('\0', characterSet.horizontalBar));
                                if (reset) writeReset(printStream);
                                printStream.append(' ');
                                printStream.append(label.message);

                                if (label.hint != null) {
                                    printStream.append('\n');

                                    writeLineNumber(printStream, -1, maxNumbersOfDigit, true);
                                    writeMultiLineLabel(printStream, -1, occupiedMultiLineLabels, null, characterSet.verticalBar);

                                    printStream.append(new String(new char[spaceLen + offset]).replace('\0', ' '));
                                    printStream.append(new String(new char[mostLastPosition - insertedLen + 1]).replace('\0', ' '));
                                    boolean resetHint = writeColor(printStream, Attribute.BRIGHT_BLUE_TEXT());
                                    printStream.append("!hint: ");
                                    printStream.append(label.hint);
                                    if (resetHint) printStream.append(Ansi.RESET);
                                }

                                break;
                            }

                            boolean reset = writeColor(printStream, label.format);
                            printStream.append(characterSet.verticalBar);
                            if (reset) writeReset(printStream);
                        }

                        printStream.append('\n');
                    }
                }

                if (endedLabel != null) {
                    // Render multiline label's message
                    writeLineNumber(printStream, -1, maxNumbersOfDigit, true);
                    writeMultiLineLabel(printStream, -1, occupiedMultiLineLabels, null, characterSet.verticalBar);
                    printStream.append('\n');
                    writeLineNumber(printStream, -1, maxNumbersOfDigit, true);
                    writeMultiLineLabel(printStream, -1, occupiedMultiLineLabels, endedLabel, characterSet.verticalBar);
                    occupiedMultiLineLabels.computeIfPresent(endedLabel, (l, occupied) -> false);
                    boolean reset = writeColor(printStream, endedLabel.format);
                    printStream.append(new String(new char[mostLastPosition]).replace('\0', characterSet.horizontalBar));
                    if (reset) writeReset(printStream);
                    printStream.format(" %s", endedLabel.message);

                    if (endedLabel.hint != null) {
                        printStream.append('\n');

                        writeLineNumber(printStream, -1, maxNumbersOfDigit, true);
                        writeMultiLineLabel(printStream, -1, occupiedMultiLineLabels, null, characterSet.verticalBar);

                        printStream.append(new String(new char[mostLastPosition]).replace('\0', ' '));
                        boolean resetHint = writeColor(printStream, Attribute.BRIGHT_BLUE_TEXT());
                        printStream.append("!hint: ");
                        printStream.append(endedLabel.hint);
                        if (resetHint) printStream.append(Ansi.RESET);
                    }

                    printStream.append('\n');
                }
            }

            writeColor(printStream, Attribute.BRIGHT_BLACK_TEXT());
            printStream.append(new String(new char[maxNumbersOfDigit + 1]).replace('\0', characterSet.horizontalBar));
            printStream.append(characterSet.rightBottom);
            writeReset(printStream);
            printStream.append('\n');

            printStream.flush();
        }
    }

    /**
     * dump prints out all built reports but also clear reports for reusing.
     *
     * @param printStream the output stream to print reports
     * @return builder with reports cleared.
     */
    public @NotNull FileReportBuilder dump(final @NotNull PrintStream printStream) {
        print(printStream);
        reports.clear();
        return this;
    }

    public boolean containsError() {
        for (Report report : reports)
            if (report.type == Report.ReportType.ERROR)
                return true;
        return false;
    }

    public boolean containsWarning() {
        for (Report report : reports)
            if (report.type == Report.ReportType.WARNING)
                return true;
        return false;
    }

    public boolean containsReport() {
        return reports.isEmpty();
    }

    private boolean writeColor(final @NotNull PrintStream printStream, @Nullable Attribute... attributes) {
        boolean reset = false;

        for (Attribute attr : attributes) {
            if (attr != null && enableColor) {
                printStream.append(Ansi.generateCode(attr));
                reset = true;
            }
        }

        return reset;
    }

    private boolean writeColor(final @NotNull PrintStream printStream, @Nullable AnsiFormat format) {
        if (format != null && enableColor) {
            printStream.append(Ansi.generateCode(format));
            return true;
        } else {
            return false;
        }
    }

    private void writeReset(final @NotNull PrintStream printStream) {
        if (enableColor) printStream.append(Ansi.RESET);
    }

    private void writeSourceLocation(final @NotNull PrintStream printStream, int maxLineDigit, Position startPosition) {
        writeColor(printStream, Attribute.BRIGHT_BLACK_TEXT());
        printStream.format("%" + (maxLineDigit + 2) + "s%s[", characterSet.leftTop, characterSet.horizontalBar);
        writeReset(printStream);
        printStream.format("%s:%d:%d", sourceName, startPosition.line, startPosition.pos);
        writeColor(printStream, Attribute.BRIGHT_BLACK_TEXT());
        printStream.append(']');
        writeReset(printStream);
        printStream.append('\n');
    }

    private void writeLineNumber(final @NotNull PrintStream printStream, int lineNumber, int maxLineDigit, boolean isVirtualLine) {
        writeColor(printStream, Attribute.BRIGHT_BLACK_TEXT());
        if (isVirtualLine) printStream.format("%" + maxLineDigit + "s %s ", "", characterSet.verticalBarBreaking);
        else printStream.format("%" + maxLineDigit + "d %s ", lineNumber, characterSet.verticalBar);
        writeReset(printStream);
    }

    private @Nullable Label writeMultiLineLabel(final @NotNull PrintStream printStream, int lineNumber, Map<Label, Boolean> labelMap, @Nullable Label terminatedLabel, char verticalBarVariant) {
        List<Map.Entry<Label, Boolean>> entries = new ArrayList<>(labelMap.entrySet());
        boolean shouldPrint = true;
        int lastIndex = 0;
        Label endedLabel = null;

        for (int i = 0; i < entries.size(); i++) {
            Label label = entries.get(i).getKey();

            if (entries.get(i).getValue()) {
                // Render bars and arrow
                boolean reset = writeColor(printStream, label.format);

                if (label.span.startPosition.line == lineNumber) {
                    printStream.append(characterSet.leftTop);
                    printStream.append(new String(new char[(entries.size() - i) * 2]).replace('\0', characterSet.horizontalBar));
                    printStream.append(characterSet.rightArrow);
                    shouldPrint = false;
                    break;
                } else if (label.span.endPosition.line == lineNumber) {
                    printStream.append(characterSet.leftCross);
                    printStream.append(new String(new char[(entries.size() - i) * 2]).replace('\0', characterSet.horizontalBar));
                    printStream.append(characterSet.rightArrow);
                    shouldPrint = false;
                    endedLabel = label;
                    break;
                } else if (label == terminatedLabel) {
                    printStream.append(characterSet.leftBottom);
                    printStream.append(new String(new char[(entries.size() - i) * 2 + 2]).replace('\0', characterSet.horizontalBar));
                    return null;
                } else {
                    printStream.append(verticalBarVariant);
                    printStream.append(' ');
                }

                if (reset) writeReset(printStream);
            } else {
                printStream.append("  ");
            }

            lastIndex = i + 1;
        }

        if (shouldPrint)
            printStream.append(new String(new char[(entries.size() - lastIndex) * 2 + 3]).replace('\0', ' '));
        else printStream.append(' ');

        return endedLabel;
    }

    /* Windows 10 supports Ansi codes. However, it's still experimental and not enabled by default.
     * This method enables the necessary Windows 10 feature.
     *
     * More info: https://stackoverflow.com/a/51681675/675577
     * Code source: https://stackoverflow.com/a/52767586/675577
     * Reported issue: https://github.com/PowerShell/PowerShell/issues/11449#issuecomment-569531747
     */
    private void enableWindows10AnsiSupport() {
        Function GetStdHandleFunc = Function.getFunction("kernel32", "GetStdHandle");
        DWORD STD_OUTPUT_HANDLE = new DWORD(-11);
        HANDLE hOut = (HANDLE) GetStdHandleFunc.invoke(HANDLE.class, new Object[]{STD_OUTPUT_HANDLE});

        DWORDByReference p_dwMode = new DWORDByReference(new DWORD(0));
        Function GetConsoleModeFunc = Function.getFunction("kernel32", "GetConsoleMode");
        GetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, p_dwMode});

        int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
        DWORD dwMode = p_dwMode.getValue();
        dwMode.setValue(dwMode.intValue() | ENABLE_VIRTUAL_TERMINAL_PROCESSING);
        Function SetConsoleModeFunc = Function.getFunction("kernel32", "SetConsoleMode");
        SetConsoleModeFunc.invoke(BOOL.class, new Object[]{hOut, dwMode});
    }

    public static class ReportBuilder {
        private final @NotNull FileReportBuilder parentBuilder;
        private final @NotNull Report report;

        private ReportBuilder(final @NotNull FileReportBuilder parentBuilder, @NotNull Report report) {
            this.parentBuilder = parentBuilder;
            this.report = report;
        }

        public @NotNull ReportBuilder tag(@NotNull String tag) {
            report.setTag(tag);
            return this;
        }

        public @NotNull LabelBuilder label(@NotNull Span span, @NotNull String message, @Nullable Object... args) {
            return new LabelBuilder(this, new Label(span, String.format(message, args)));
        }

        public @NotNull FileReportBuilder build() {
            parentBuilder.reports.add(report);
            return parentBuilder;
        }
    }

    public static class LabelBuilder {
        private final @NotNull ReportBuilder parentBuilder;
        private final @NotNull Label label;

        private LabelBuilder(@NotNull ReportBuilder parentBuilder, @NotNull Label label) {
            this.parentBuilder = parentBuilder;
            this.label = label;
        }

        public @NotNull LabelBuilder hint(@NotNull String hint, @Nullable Object... args) {
            label.setHint(String.format(hint, args));
            return this;
        }

        public @NotNull LabelBuilder color(@NotNull AnsiFormat format) {
            label.format = format;
            return this;
        }

        public @NotNull LabelBuilder color(@NotNull Attribute... attributes) {
            label.format = new AnsiFormat(attributes);
            return this;
        }

        public @NotNull ReportBuilder build() {
            parentBuilder.report.addLabel(label);
            return parentBuilder;
        }
    }
}
