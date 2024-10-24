package sceneutil.font;

import java.awt.*;

public class FontBox {
    // 字体
    public static final int PLAIN  = Font.PLAIN;
    public static final int BOLD   = Font.BOLD;
    public static final int ITALIC = Font.ITALIC;

    private String str;
    private Color color;
    private Font font;
    private Point point;

    // コン
    public FontBox(String str, Font font, Color color, Point point) {
        this.str = str;
        this.font = font;
        this.color = color;
        this.point = point;
    }

    // 雪駄
    public void setStr(String str) {
        this.str = str;
    }
    public void setFont(Font font) {
        this.font = font;
    }
    public void setColor(Color color) {
        this.color = color;
    }
    public void setPoint(Point point) {
        this.point = point;
    }

    // フォントインスタンスの作成
    public static Font Arial(int size, int style) {
        return new Font("Arial", style, size);
    }
    public static Font Arial(int size) {
        return new Font("Arial", PLAIN, size);
    }

    public static Font MSGothic(int size, int style) {
        return new Font("ＭＳ ゴシック", style, size);
    }
    public static Font MSGothic(int size) {
        return new Font("ＭＳ ゴシック", PLAIN, size);
    }

    public static Font Meiryo(int size, int style) {
        return new Font("Meiryo", style, size);
    }
    public static Font Meiryo(int size) {
        return new Font("Meiryo", PLAIN, size);
    }

    /** フォントと色を設定する **/
    public void set(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.setFont(font);
    }

    /** 文字を描画する **/
    public void draw(Graphics2D g2d) {
        g2d.drawString(str, point.x, point.y);
    }

    /** ズラして文字を描画する **/
    public void draw(Graphics2D g2d, int paddingX, int paddingY) {
        g2d.drawString(str, point.x + paddingX, point.y + paddingY);
    }

    /** 色とフォントを設定して文字を描画する **/
    public void drawWithSetColor(Graphics2D g2d) {
        set(g2d);
        g2d.drawString(str, point.x, point.y);
    }

    /** 一時的にパラメータを付与して文字を描画する（影文字とか） **/
    public void drawOneTimeParam(Graphics2D g2d, Color oneTimeColor, int paddingX, int paddingY) {
        Color oldColor = g2d.getColor();
        g2d.setColor(oneTimeColor);
        g2d.setFont(font);
        g2d.drawString(str, point.x + paddingX, point.y + paddingY);
        g2d.setColor(oldColor);
    }

    /** 文字列の幅を算出する **/
    public int strWidth(Graphics2D g2d) {
        return g2d.getFontMetrics(font).stringWidth(str);
    }

    /** 文字の高さを算出する **/
    public int strHeight(Graphics2D g2d) {
        return g2d.getFontMetrics(font).getHeight();
    }
}
