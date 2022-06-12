package chaos.unity.nenggao;

import org.jetbrains.annotations.NotNull;

public class Line {
    /**
     * lineNumber always starts from 1.
     */
    public final int lineNumber;
    public final int offset;
    public final int len;
    public final @NotNull String chars;

    public Line(int lineNumber, int offset, int len, @NotNull String chars) {
        this.lineNumber = lineNumber;
        this.offset = offset;
        this.len = len;
        this.chars = chars;
    }

    public Span span() {
        return new Span(new Position(lineNumber, offset), new Position(lineNumber, offset + len));
    }
}
