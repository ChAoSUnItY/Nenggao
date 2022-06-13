package chaos.unity.nenggao;

public enum CharacterSet {
    UNICODE(
            '─',
            '│',
            '┼',
            '┬',
            '·',
            '╭',
            '╰',
            '╯',
            '─'
    ),
    ASCII(
            '-',
            '|',
            '+',
            '|',
            '*',
            ',',
            '`',
            '\'',
            '^'
    );

    // BARS
    public final char horizontalBar;
    public final char verticalBar;
    public final char crossBar;
    public final char underBar;

    // VERTICAL BAR VARIANTS
    public final char verticalBarBreaking;

    // LINE EDGES
    public final char leftTop;
    public final char leftBottom;
    public final char rightBottom;

    // LINES
    public final char underline;


    CharacterSet(
            char horizontalBar,
            char verticalBar,
            char crossBar,
            char underBar,
            char verticalBarBreaking,
            char leftTop,
            char leftBottom,
            char rightBottom,
            char underline
    ) {
        this.horizontalBar = horizontalBar;
        this.verticalBar = verticalBar;
        this.crossBar = crossBar;
        this.underBar = underBar;
        this.verticalBarBreaking = verticalBarBreaking;
        this.leftTop = leftTop;
        this.leftBottom = leftBottom;
        this.rightBottom = rightBottom;
        this.underline = underline;
    }
}
