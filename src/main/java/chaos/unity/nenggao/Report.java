package chaos.unity.nenggao;

import com.diogonunes.jcolor.AnsiFormat;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Report {
    public final @NotNull Span commonSpan;
    public final @NotNull List<@NotNull Label> labels = new ArrayList<>();
    public final @NotNull ReportType type;
    public final @NotNull String message;

    public Report(@NotNull Position startPosition, @NotNull Position endPosition, @NotNull ReportType type, @NotNull String message) {
        this.commonSpan = new Span(startPosition, endPosition);
        this.type = type;
        this.message = message;
    }

    public Report(@NotNull Span span, @NotNull ReportType type, @NotNull String message) {
        this.commonSpan = span;
        this.type = type;
        this.message = message;
    }

    public void addLabel(@NotNull Label label) {
        this.labels.add(label);
    }

    public abstract void setTag(@NotNull String tag);

    public abstract @NotNull String getTag();

    public enum ReportType {
        WARNING,
        ERROR
    }
}
