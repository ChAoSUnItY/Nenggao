package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

public class Error extends Report {
    public Error(@NotNull Position startPosition, @NotNull Position endPosition, @NotNull String message) {
        super(startPosition, endPosition, ReportType.ERROR, message);
    }

    public Error(@NotNull Span span, @NotNull String message) {
        super(span, ReportType.ERROR, message);
    }
}
