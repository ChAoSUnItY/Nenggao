package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

public class Label {
    public final @NotNull Span span;
    public final @NotNull String message;

    public Label(@NotNull Span span, @NotNull String message) {
        this.span = span;
        this.message = message;
    }
}
