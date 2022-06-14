package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Span {
    public final @NotNull Position startPosition;
    public final @NotNull Position endPosition;

    public Span(@NotNull Position startPosition, @NotNull Position endPosition) {
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
        if (startPosition.pos > endPosition.pos) {
            return -1;
        } else if (startPosition.line != endPosition.line) {
            return -1;
        }
        return endPosition.pos - startPosition.pos;
    }

    /**
     * expand copy current instance of {@link Span} and attempt to extend end position to {@code endSpan}.
     * If {@code endSpan} is null, returns copy of current instance; if {@code endSpan}'s {@link Span#endPosition}
     * is in front of the current instance's {@link Span#startPosition}, returns copy of current instance, otherwise,
     * returns a copy of current instance, which its {@link Span#endPosition} is replaced by {@code endSpan}'s
     * {@link Span#endPosition}.
     * @param endSpan the span to expand with
     * @return a copy of current instance, field data would be different based on {@code endSpan}'s data
     */
    public @NotNull Span expand(@Nullable Span endSpan) {
        Span copied = copy();

        if (endSpan == null)
            return copied;

        if (endSpan.endPosition.line < startPosition.line)
            return copied;
        else if (endSpan.endPosition.line == startPosition.line) {
            if (endSpan.endPosition.pos < startPosition.pos)
                return copied;
        }

        Position startPosition = this.startPosition;
        Position endPosition = endSpan.endPosition;

        return new Span(startPosition, endPosition);
    }

    public @NotNull Span copy() {
        return new Span(startPosition, endPosition);
    }

    /**
     * range constructs a span that ignores positions in lines, which is effective when using in source segment capturing.
     * @param startLineNumber start of range, must be larger than 1.
     * @param endLineNumber end of range.
     * @return span based on providing line range
     */
    public static Span range(int startLineNumber, int endLineNumber) {
        return multipleLine(startLineNumber, 0, endLineNumber, 0);
    }

    /**
     * singleLine constructs a span that only capture a string in single line.
     * @param lineNumber start and end line, must be larger than 1.
     * @param start start of span in line.
     * @param end end of span in line.
     * @return span.
     */
    public static Span singleLine(int lineNumber, int start, int end) {
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
    public static Span multipleLine(int startLineNumber, int start, int endLineNumber, int end) {
        return new Span(new Position(startLineNumber, start), new Position(endLineNumber, end));
    }
}
