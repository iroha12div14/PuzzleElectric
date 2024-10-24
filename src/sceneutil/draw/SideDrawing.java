package sceneutil.draw;

public class SideDrawing {
    public final int x;
    public final int y;
    public int dir;

    public static final int CENTER =  0;
    public static final int LEFT   = -1;
    public static final int RIGHT  =  1;
    public static final int TOP    = -1;
    public static final int BOTTOM =  1;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL   = 1;

    /** 上下左右寄せ（と、台形であれば縦向き・横向き）の設定 **/
    public SideDrawing(int x, int y, int dir) {
        this.x = x;
        this.y = y;
        this.dir = dir;
    }
    /** 上下左右寄せの設定 **/
    public SideDrawing(int x, int y) {
        this(x, y, HORIZONTAL);
    }
    public SideDrawing() {
        this(LEFT, TOP, HORIZONTAL);
    }
    /** パラメータをコピー **/
    public SideDrawing(SideDrawing sd) {
        this.x = sd.x;
        this.y = sd.y;
        this.dir = sd.dir;
    }
}
