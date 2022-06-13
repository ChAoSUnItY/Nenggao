package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

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

    public static Span singleLine(int lineNumber, int start, int end) {
        return new Span(new Position(lineNumber, start), new Position(lineNumber, end));
    }

    public static Span multipleLine(int startLineNumber, int start, int endLineNumber, int end) {
        return new Span(new Position(startLineNumber, start), new Position(endLineNumber, end));
    }
}
