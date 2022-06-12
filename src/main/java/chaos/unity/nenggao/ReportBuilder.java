package chaos.unity.nenggao;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import com.sun.jna.Function;
import com.sun.jna.platform.win32.WinDef.BOOL;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinNT.HANDLE;

public class ReportBuilder {
    public @NotNull String sourceFilePath;
    public @NotNull List<@NotNull Report> reports = new ArrayList<>();

    private ReportBuilder(@NotNull String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public static @NotNull ReportBuilder sourceFile(@NotNull String sourceFilePath) {
        return new ReportBuilder(sourceFilePath);
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

        printStream.println(Ansi.colorize("[error]", Attribute.RED_TEXT()));
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
