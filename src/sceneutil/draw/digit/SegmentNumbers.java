package sceneutil.draw.digit;

import java.awt.*;

/** デジタル数字列を描画するクラス **/
public class SegmentNumbers {
    private final DrawSegment ds = new DrawSegment();
    private static final float PADDING_X = 0.85F;

    // セグメント色 点灯・消灯
    private static final Color SEGMENT_COLOR = new Color(255,60,60);
    private static final Color SEGMENT_COLOR_OFF = new Color(20,20,40);
    // セグメントの高さ、幅、太さ
    private static final int SEGMENT_SIZE = 100;
    private static final int SEGMENT_BOLD = 12;

    // パラメータ
    private int size;
    private int x;
    private int y;
    private Color color;
    private Color colorOff;
    private int bold;
    private boolean frame;
    private float paddingX;

    /** コンストラクタ
     * @param size  大きさ（100が基準値として高さが100、幅が60になる）
     * @param posX  X座標
     * @param posY  Y座標
     */
    public SegmentNumbers(int size, int posX, int posY) {
        this.size = size;
        this.x = posX;
        this.y = posY;
        color = SEGMENT_COLOR;
        colorOff = SEGMENT_COLOR_OFF;
        bold = SEGMENT_BOLD;
        frame = false;
        paddingX = PADDING_X;
    }
    public SegmentNumbers() {
        this(SEGMENT_SIZE, 0, 0);
    }

    /**
     * セグメントの描画
     * @param number 表示する数値列
     */
    public void drawSegmentNumbers(Graphics2D g2d, String number) {
        String[] numbers = number.split("");

        int posX = x;
        for(String numStr : numbers) {
            int n = Integer.parseInt(numStr);
            ds.drawSegmentNumber(g2d, n, size, posX, y, bold, frame, color, colorOff);
            posX += (int) (size * PADDING_X);
        }
    }
    /**
     * セグメントの描画
     * @param number 表示する数値列
     */
    public void drawSegmentNumbers(Graphics2D g2d, int number) {
        drawSegmentNumbers(g2d, String.valueOf(number) );
    }

    // 雪駄

    /**
     * 位置と大きさを指定する。
     * @param size サイズ。デフォルトは100（高さ100、幅60）
     */
    public void setParam(int size, int x, int y) {
        setSegmentSize(size);
        setX(x);
        setY(y);
    }
    public void setParam(int size, int x, int y, Color color) {
        setParam(size, x, y);
        setSegmentColor(color);
    }
    public void setParam(int size, int x, int y, Color color, Color colorOff) {
        setParam(size, x, y, color);
        setSegmentColorOff(colorOff);
    }
    public void setParam(int size, int x, int y, Color color, Color colorOff, int bold) {
        setParam(size, x, y, color, colorOff);
        setSegmentBold(bold);
    }
    public void setParam(int size, int x, int y, Color color, Color colorOff, int bold, boolean f) {
        setParam(size, x, y, color, colorOff, bold);
        setSegmentFrame(f);
    }
    public void setSegmentSize(int size) {
        this.size = size;
    }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setSegmentColor(Color color) {
        this.color = color;
    }
    public void setSegmentColorOff(Color color) {
        this.colorOff = color;
    }
    /** デフォルトは 12 **/
    public void setSegmentBold(int bold) {
        this.bold = bold;
    }
    public void setSegmentFrame(boolean f) {
        this.frame = f;
    }
    /** デフォルトは 0.85F **/
    public void setPaddingX(float padding) {
        this.paddingX = padding;
    }

    // セグメントの原寸サイズとデフォルト色
    public int getSegmentSize() {
        return SEGMENT_SIZE;
    }
    public Color getSegmentColor() {
        return SEGMENT_COLOR;
    }
    public Color getSegmentColorOff() {
        return SEGMENT_COLOR_OFF;
    }
    public int getSegmentBold() {
        return SEGMENT_BOLD;
    }
    public boolean isFrameDraw() {
        return frame;
    }
    public float getPaddingX() {
        return paddingX;
    }
}
