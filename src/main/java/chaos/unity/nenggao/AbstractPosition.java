package chaos.unity.nenggao;

public abstract class AbstractPosition {
    public final int line;
    public final int pos;
    
    protected AbstractPosition(int line, int pos) {
        this.line = line;
        this.pos = pos;
    }
}
