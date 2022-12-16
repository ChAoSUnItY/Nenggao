package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Span extends AbstractSpan {
    public Span(@NotNull AbstractPosition startPosition, @NotNull AbstractPosition endPosition) {
        super(startPosition, endPosition);
    }

    @Override
    public @NotNull AbstractSpan expand(@Nullable AbstractSpan endSpan) {
        AbstractSpan copied = copy();

        if (endSpan == null)
            return copied;

        if (endSpan.endPosition.line < startPosition.line)
            return copied;
        else if (endSpan.endPosition.line == startPosition.line) {
            if (endSpan.endPosition.pos < startPosition.pos)
                return copied;
        }

        AbstractPosition startPosition = this.startPosition;
        AbstractPosition endPosition = endSpan.endPosition;

        return new Span(startPosition, endPosition);
    }

    @Override
    public @NotNull AbstractSpan copy() {
        return new Span(startPosition, endPosition);
    }
}
