package chaos.unity.nenggao;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import com.sun.jna.Function;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class ReportBuilder {
    /**
     * Must be absolute path.
     */
    public final @NotNull String sourceFilePath;
    /**
     *
     */
    public final @NotNull File sourceFile;
    public @NotNull List<@NotNull Report> reports = new ArrayList<>();

    private ReportBuilder(@NotNull String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
        this.sourceFile = new File(sourceFilePath);
    }

    private ReportBuilder(@NotNull File sourceFile) {
        this.sourceFilePath = sourceFile.getAbsolutePath();
        this.sourceFile = sourceFile;
    }

    public static @NotNull ReportBuilder sourceFile(@NotNull String sourceFilePath) {
        return new ReportBuilder(sourceFilePath);
    }

    public static @NotNull ReportBuilder sourceFile(@NotNull File sourceFile) {
        return new ReportBuilder(sourceFile);
    }

    public @NotNull ReportBuilder warning(@NotNull Span span, @NotNull String message) {
        reports.add(new Warning(span, message));
        return this;
    }

    public @NotNull ReportBuilder error(@NotNull Span span, @NotNull String message) {
        reports.add(new Error(span, message));
        return this;
    }

    public void print(final PrintStream printStream) {
        enableWindows10AnsiSupport();

        Source source = Source.fromFile(new File(sourceFilePath));

        if (source == null)
            return;

        for (Report report : reports) {
            List<Line> segment = source.slice(report.commonSpan.startPosition.line, report.commonSpan.endPosition.line);

            switch (report.type) {
                case WARNING:
                    printStream.append(Ansi.colorize("[Warning] ", Attribute.YELLOW_TEXT()));
                    break;
                case ERROR:
                    printStream.print(Ansi.colorize("[Error] ", Attribute.RED_TEXT()));
                    break;
            }

            printStream.append(report.message);
            printStream.println();

            for (Line line : segment) {
                printStream.append(line.chars);
            }

            printStream.flush();
        }
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
}
