package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

public abstract class Report {
    public final @NotNull Span span;
    public final @NotNull ReportType type;
    public final @NotNull String message;

    public Report(@NotNull Position startPosition, @NotNull Position endPosition, @NotNull ReportType type, @NotNull String message) {
        this.span = new Span(startPosition, endPosition);
        this.type = type;
        this.message = message;
    }

    public Report(@NotNull Span span, @NotNull ReportType type, @NotNull String message) {
        this.span = span;
        this.type = type;
        this.message = message;
    }

    public enum ReportType {
        WARNING,
        ERROR
    }
}
