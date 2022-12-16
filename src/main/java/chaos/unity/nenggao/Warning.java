package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Warning extends Report {
    public Warning(@NotNull AbstractPosition startPosition, @NotNull AbstractPosition endPosition, @NotNull String message) {
        super("warning", startPosition, endPosition, ReportType.WARNING, message);
    }

    public Warning(@NotNull AbstractSpan span, @NotNull String message) {
        super("warning", span, ReportType.WARNING, message);
    }
}
