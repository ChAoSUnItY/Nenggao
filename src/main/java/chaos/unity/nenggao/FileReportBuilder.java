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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.*;

public class FileReportBuilder {
    public @NotNull String filePath;
    /**
     * The actual source file to retrieve, will be read by {@link Source#fromFile(File)}
     */
    private final @NotNull File sourceFile;
    private @NotNull CharacterSet characterSet = CharacterSet.UNICODE;
    private @NotNull List<@NotNull Report> reports = new ArrayList<>();

    private FileReportBuilder(@NotNull String sourceFilePath) {
        this.filePath = sourceFilePath;
        this.sourceFile = new File(sourceFilePath);
    }

    private FileReportBuilder(@NotNull File sourceFile) {
        try {
            this.filePath = sourceFile.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            this.filePath = sourceFile.getPath();
        }
        this.sourceFile = sourceFile;
    }

    public static @NotNull FileReportBuilder sourceFile(@NotNull String sourceFilePath) {
        return new FileReportBuilder(sourceFilePath);
    }

    public static @NotNull FileReportBuilder sourceFile(@NotNull File sourceFile) {
        return new FileReportBuilder(sourceFile);
    }

    public @NotNull ReportBuilder warning(@NotNull Span span, @NotNull String message) {
        return new ReportBuilder(this, new Warning(span, message));
    }

    public @NotNull ReportBuilder error(@NotNull Span span, @NotNull String message) {
        return new ReportBuilder(this, new Error(span, message));
    }

    public @NotNull FileReportBuilder characterSet(@NotNull CharacterSet characterSet) {
        this.characterSet = characterSet;
        return this;
    }

    public @NotNull FileReportBuilder relativePath(@NotNull Path parentPath) {
        this.filePath = parentPath.relativize(sourceFile.toPath()).toString();
        return this;
    }

    public void print(final @NotNull PrintStream printStream) {
        enableWindows10AnsiSupport();

        Source source = Source.fromFile(sourceFile);

        if (source == null)
            return;

        for (Report report : reports) {
            report.labels.sort((l1, l2) -> l1.span.startPosition.pos - l2.span.endPosition.pos); // Sort label's order so the render algorithm won't mess up
            int maxNumbersOfDigit = (int) Math.max(Math.log10(report.commonSpan.startPosition.line) + 1, Math.log10(report.commonSpan.endPosition.line) + 1);

            List<Line> segment = source.slice(report.commonSpan.startPosition.line, report.commonSpan.endPosition.line);

            switch (report.type) {
                case WARNING:
                    printStream.append(Ansi.colorize("[Warning] ", Attribute.YELLOW_TEXT()));
                    break;
                case ERROR:
                    printStream.append(Ansi.colorize("[Error] ", Attribute.RED_TEXT()));
                    break;
            }

            printStream.append(report.message);
            printStream.println();

            writeSourceLocation(printStream, maxNumbersOfDigit, report.commonSpan.startPosition);

            List<String> processedLines = new ArrayList<>(segment.size() + report.labels.size());
            // Used for mapping multi-line diagnostic spacing
            // Occupation Id - Is Free
            Map<Integer, Boolean> occupiedMultiLinesDiagnostic = new HashMap<>();


            for (int i = 0; i < segment.size(); i++) {
                Line line = segment.get(i);
                StringBuilder lineBuilder = new StringBuilder(line.chars);

                int insertedLen = 0;
                int mostLastPosition = 0;
                List<Label> appliedLabels = new LinkedList<>();
                for (Label label : report.labels) {
                    if (!label.isMultiLine() && label.isInSameLine(line.lineNumber)) {
                        if (label.format != null) {
                            String ansiCode = Ansi.generateCode(label.format);
                            lineBuilder.insert(insertedLen + label.span.startPosition.pos, ansiCode);
                            insertedLen += ansiCode.length();
                            lineBuilder.insert(insertedLen + label.span.startPosition.pos + label.span.offset(), Ansi.RESET);
                            insertedLen += Ansi.RESET.length();
                        }

                        mostLastPosition = Math.max(mostLastPosition, label.span.endPosition.pos) + 2;
                        appliedLabels.add(label);
                    }
                    // TODO: Handle multiline label
                }

                writeLineNumber(printStream, line.lineNumber, maxNumbersOfDigit, false);
                printStream.append(lineBuilder.toString());

                insertedLen = 0;
                if (!appliedLabels.isEmpty()) {
                    // Render Underline
                    writeLineNumber(printStream, -1, maxNumbersOfDigit, true);

                    // Render under bars
                    for (Label label : appliedLabels) {
                        int spaceLen = label.span.startPosition.pos - insertedLen;
                        if (spaceLen > 0) // Prevent unnecessary appending
                            printStream.append(new String(new char[spaceLen]).replace('\0', ' '));

                        int offset = label.span.offset();
                        StringBuilder underlineBuilder = new StringBuilder(offset);
                        for (int k = 0; k < offset; k++) {
                            if (offset / 2 == k) {
                                underlineBuilder.append(characterSet.underBar);
                            } else underlineBuilder.append(characterSet.underline);
                        }

                        printStream.append(label.format != null ? Ansi.colorize(underlineBuilder.toString(), label.format) : underlineBuilder.toString());
                        insertedLen += spaceLen + offset;
                    }

                    printStream.append('\n');

                    // Render lines, arrows, and label message
                    for (int j = 1; j < appliedLabels.size() * 2; j++) {
                        writeLineNumber(printStream, -1, maxNumbersOfDigit, true);

                        insertedLen = 0;
                        for (int k = 0; k < appliedLabels.size(); k++) {
                            Label label = appliedLabels.get(k);

                            // Check if it's null, this happens after we set label to null when it's marked printed
                            if (label == null)
                                continue;

                            int spaceLen = label.span.startPosition.pos - insertedLen;
                            int offset = label.span.offset() / 2;

                            printStream.append(new String(new char[spaceLen + offset]).replace('\0', ' '));

                            insertedLen += spaceLen + offset + 1; // 1 is vertical bar

                            if (j % 2 == 1) {
                                appliedLabels.set(k, null); // Mark printed
                                if (label.format != null)
                                    printStream.append(Ansi.generateCode(label.format));
                                printStream.append(String.valueOf(characterSet.leftBottom));
                                printStream.append(new String(new char[mostLastPosition - insertedLen]).replace('\0', characterSet.horizontalBar));
                                if (label.format != null)
                                    printStream.append(Ansi.RESET);
                                printStream.append(' ');
                                printStream.append(label.message);
                                break;
                            }

                            printStream.append(label.format != null ? Ansi.colorize(String.valueOf(characterSet.verticalBar), label.format) : String.valueOf(characterSet.verticalBar));
                        }

                        printStream.append('\n');
                    }
                }
            }

            printStream.append(Ansi.colorize(new String(new char[maxNumbersOfDigit + 1]).replace('\0', characterSet.horizontalBar) + characterSet.rightBottom, Attribute.BRIGHT_BLACK_TEXT()));
            printStream.append('\n');

            printStream.flush();
        }
    }

    private void writeSourceLocation(final @NotNull PrintStream printStream, int maxLineDigit, Position startPosition) {
        printStream.append(Ansi.generateCode(Attribute.BRIGHT_BLACK_TEXT()));
        printStream.format("%" + (maxLineDigit + 2) + "s%s", characterSet.leftTop, characterSet.horizontalBar);
        printStream.format("[%s%s:%d:%d%s]\n", Ansi.RESET, filePath, startPosition.line, startPosition.pos, Ansi.generateCode(Attribute.BRIGHT_BLACK_TEXT()));
    }

    private void writeLineNumber(final @NotNull PrintStream printStream, int lineNumber, int maxLineDigit, boolean isVirtualLine) {
        printStream.append(Ansi.generateCode(Attribute.BRIGHT_BLACK_TEXT()));
        if (isVirtualLine) printStream.format("%" + maxLineDigit + "s %s ", "", characterSet.verticalBarBreaking);
        else printStream.format("%" + maxLineDigit + "d %s ", lineNumber, characterSet.verticalBar);
        printStream.append(Ansi.RESET);
        printStream.flush();
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

        public @NotNull LabelBuilder label(@NotNull Span span, @NotNull String message) {
            return new LabelBuilder(this, new Label(span, message));
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
