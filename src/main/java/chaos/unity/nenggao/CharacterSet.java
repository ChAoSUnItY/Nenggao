package chaos.unity.nenggao;

public enum CharacterSet {
    UNICODE(
            '▶',
            '─',
            '│',
            '┼',
            '┬',
            '├',
            '·',
            '⋮',
            '╭',
            '╰',
            '╯',
            '─'
    ),
    ASCII(
            '>',
            '-',
            '|',
            '+',
            '|',
            '|',
            '*',
            ':',
            ',',
            '`',
            '\'',
            '^'
    );

    // ARROWS
    public final char rightArrow;

    // BARS
    public final char horizontalBar;
    public final char verticalBar;
    public final char crossBar;
    public final char underBar;
    public final char leftCross;

    // VERTICAL BAR VARIANTS
    public final char verticalBarBreaking;
    public final char verticalEllipsis;

    // LINE EDGES
    public final char leftTop;
    public final char leftBottom;
    public final char rightBottom;

    // LINES
    public final char underline;


    CharacterSet(
            char rightArrow,
            char horizontalBar,
            char verticalBar,
            char crossBar,
            char underBar,
            char leftCross,
            char verticalBarBreaking,
            char verticalEllipsis,
            char leftTop,
            char leftBottom,
            char rightBottom,
            char underline
    ) {
        this.rightArrow = rightArrow;
        this.horizontalBar = horizontalBar;
        this.verticalBar = verticalBar;
        this.crossBar = crossBar;
        this.underBar = underBar;
        this.leftCross = leftCross;
        this.verticalBarBreaking = verticalBarBreaking;
        this.verticalEllipsis = verticalEllipsis;
        this.leftTop = leftTop;
        this.leftBottom = leftBottom;
        this.rightBottom = rightBottom;
        this.underline = underline;
    }
}
