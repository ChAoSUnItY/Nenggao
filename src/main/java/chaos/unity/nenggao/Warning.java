package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

public class Warning extends Report {
    public Warning(@NotNull Position startPosition, @NotNull Position endPosition, @NotNull String message) {
        super(startPosition, endPosition, ReportType.WARNING, message);
    }

    public Warning(@NotNull Span span, @NotNull String message) {
        super(span, ReportType.WARNING, message);
    }
}
