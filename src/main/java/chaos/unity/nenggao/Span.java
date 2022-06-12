package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

public class Span {
    public final @NotNull Position startPosition;
    public final @NotNull Position endPosition;

    public Span(@NotNull Position startPosition, @NotNull Position endPosition) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
    }

    public static Span singleLine(int lineNumber, int start, int end) {
        return new Span(new Position(lineNumber, start), new Position(lineNumber, end));
    }

    public static Span multipleLine(int startLineNumber, int endLineNumber, int start, int end) {
        return new Span(new Position(startLineNumber, start), new Position(endLineNumber, end));
    }
}
