package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

public class Warning extends Report {
    public @NotNull String tag = "warning";

    public Warning(@NotNull Position startPosition, @NotNull Position endPosition, @NotNull String message) {
        super(startPosition, endPosition, ReportType.WARNING, message);
    }

    public Warning(@NotNull Span span, @NotNull String message) {
        super(span, ReportType.WARNING, message);
    }

    @Override
    public void setTag(@NotNull String tag) {
        this.tag = tag;
    }

    @Override
    public @NotNull String getTag() {
        return tag;
    }
}
