package chaos.unity.nenggao;

import com.diogonunes.jcolor.AnsiFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Label {
    public @Nullable AnsiFormat format = null;
    public final @NotNull Span span;
    public final @NotNull String message;

    public Label(@NotNull Span span, @NotNull String message) {
        this.span = span;
        this.message = message;
    }

    public boolean isInSameLine(int lineNumber) {
        return span.startPosition.line == lineNumber;
    }

    public boolean isMultiLine() {
        return span.isMultiLine();
    }
}