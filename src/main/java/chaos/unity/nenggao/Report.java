package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Report {
    public @NotNull String tag;
    public final @NotNull AbstractSpan commonSpan;
    public final @NotNull List<@NotNull AbstractLabel> labels = new ArrayList<>();
    public final @NotNull ReportType type;
    public final @NotNull String message;

    public Report(@Nullable String tag, @NotNull AbstractPosition startPosition, @NotNull AbstractPosition endPosition, @NotNull ReportType type, @NotNull String message) {
        this(tag, new Span(startPosition, endPosition), type, message);
    }

    public Report(@Nullable String tag, @NotNull AbstractSpan span, @NotNull ReportType type, @NotNull String message) {
        this.tag = tag == null ? "" : tag;
        this.commonSpan = span;
        this.type = type;
        this.message = message;
    }

    public void addLabel(@NotNull AbstractLabel label) {
        this.labels.add(label);
    }

    public void setTag(@NotNull String tag) {
        this.tag = tag;
    }

    public @NotNull String getTag() {
        return tag;
    }

    public enum ReportType {
        WARNING,
        ERROR
    }
}
