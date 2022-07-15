package chaos.unity.nenggao;

import com.diogonunes.jcolor.AnsiFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Label {
    public @Nullable AnsiFormat format = null;
    public final @NotNull Span span;
    public final @NotNull String message;
    public @Nullable String hint = null;

    public Label(@NotNull Span span, @NotNull String message) {
        this.span = span;
        this.message = message;
    }


    public void setHint(@NotNull String hint) {
        this.hint = hint;
    }

    public @Nullable String getHint() {
        return hint;
    }

    public boolean isInSameLine(int lineNumber) {
        return span.startPosition.line == lineNumber;
    }

    public boolean isIn(int line) {
        return span.isIn(line);
    }

    public boolean isMultiLine() {
        return span.isMultiLine();
    }
}
