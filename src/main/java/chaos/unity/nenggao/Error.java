package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class Error extends Report {
    public @NotNull String tag = "error";

    public Error(@NotNull Position startPosition, @NotNull Position endPosition, @NotNull String message) {
        super(startPosition, endPosition, ReportType.ERROR, message);
    }

    public Error(@NotNull Span span, @NotNull String message) {
        super(span, ReportType.ERROR, message);
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
