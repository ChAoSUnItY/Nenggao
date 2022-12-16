package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Error extends Report {
    public Error(@NotNull AbstractPosition startPosition, @NotNull AbstractPosition endPosition, @NotNull String message) {
        super("error", startPosition, endPosition, ReportType.ERROR, message);
    }

    public Error(@NotNull AbstractSpan span, @NotNull String message) {
        super("error", span, ReportType.ERROR, message);
    }
}
