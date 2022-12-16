package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public abstract class AbstractSpan {
    public final @NotNull AbstractPosition startPosition;
    public final @NotNull AbstractPosition endPosition;

    protected AbstractSpan(@NotNull AbstractPosition startPosition, @NotNull AbstractPosition endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public boolean isMultiLine() {
        return startPosition.line != endPosition.line;
    }

    public boolean isIn(int line) {
        return startPosition.line <= line && line <= endPosition.line;
    }

    /**
     * get offset of two position, returns -1 when two positions are in different lines or end position is in front of start position.
     * @return offset of two position.
     */
    public int offset() {
        if (startPosition.line != endPosition.line || startPosition.pos > endPosition.pos) {
            return -1;
        }
        return endPosition.pos - startPosition.pos;
    }

    /**
     * expand copy current instance of {@link AbstractSpan} and attempt to extend end position to {@code endSpan}.
     * If {@code endSpan} is null, returns copy of current instance; if {@code endSpan}'s {@link AbstractSpan#endPosition}
     * is in front of the current instance's {@link AbstractSpan#startPosition}, returns copy of current instance, otherwise,
     * returns a copy of current instance, which its {@link AbstractSpan#endPosition} is replaced by {@code endSpan}'s
     * {@link AbstractSpan#endPosition}.
     * @param endSpan the span to expand with
     * @return a copy of current instance, field data would be different based on {@code endSpan}'s data
     */
    public abstract @NotNull AbstractSpan expand(@Nullable AbstractSpan endSpan);

    public abstract @NotNull AbstractSpan copy();

    /**
     * range constructs a span that ignores positions in lines, which is effective when using in source segment capturing.
     * @param startLineNumber start of range, must be larger than 1.
     * @param endLineNumber end of range.
     * @return span based on providing line range
     */
    public static AbstractSpan range(int startLineNumber, int endLineNumber) {
        return multipleLine(startLineNumber, 0, endLineNumber, 0);
    }

    /**
     * singleLine constructs a span that only capture a string in single line.
     * @param lineNumber start and end line, must be larger than 1.
     * @param start start of span in line.
     * @param end end of span in line.
     * @return span.
     */
    public static AbstractSpan singleLine(int lineNumber, int start, int end) {
        return new Span(new Position(lineNumber, start), new Position(lineNumber, end));
    }

    /**
     * multipleLine constructs a span with full control of all positions.
     * @param startLineNumber start line, must be larger than 1.
     * @param start start of span in start line.
     * @param endLineNumber end line.
     * @param end end of span in end line.
     * @return span.
     */
    public static AbstractSpan multipleLine(int startLineNumber, int start, int endLineNumber, int end) {
        return new Span(new Position(startLineNumber, start), new Position(endLineNumber, end));
    }
}
