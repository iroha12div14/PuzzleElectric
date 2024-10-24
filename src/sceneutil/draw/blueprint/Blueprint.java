package sceneutil.draw.blueprint;

import sceneutil.draw.*;

import java.awt.*;
import java.util.List;

/** 図形を描画するためのパラメータ（設計図）を設定し、描画を行うクラス **/
public class Blueprint {
    // 描画スタイル
    public static final DrawPolygon DRAW_RECT = new DrawRect();
    public static final DrawPolygon DRAW_OVAL = new DrawOval();
    public static final DrawTrapezoid DRAW_TRAPEZOID = new DrawTrapezoid();
    public static final DrawArc DRAW_ARC = new DrawArc();
    public static final DrawLine DRAW_LINE = new DrawLine();

    // 描画モード
    public enum DrawMode {
        POLYGON,
        REGULAR,
        TRAPEZOID,
        ARC,
        LINE,
        LINE_ANGLE,
        RECTANGLE,
        RECTANGLE_REGULAR,
        OVAL,
        OVAL_REGULAR
    }

    // フィールド
    private ParamDrawing param;
    private SideDrawing side;
    private DrawMode drawMode; // 描画モード
    private Color color;

    // ----------------------------------------------------------------------- //

    /**
     * 設計図の設定を流し込む
     * @param param 位置や大きさのパラメータ
     * @param side  上下左右寄せのパラメータ
     */
    public Blueprint(ParamDrawing param, SideDrawing side) {
        this.param = param;
        this.side = side;
        this.drawMode = param.getDrawMode();
    }
    /**
     * 設計図の設定を流し込む
     * @param param 位置や大きさのパラメータ
     */
    public Blueprint(ParamDrawing param) {
        this(param, new SideDrawing() );
    }
    /**
     * 設計図の設定を流し込む
     * @param param 位置や大きさのパラメータ
     * @param side  上下左右寄せのパラメータ
     * @param color 色
     */
    public Blueprint(ParamDrawing param, SideDrawing side, Color color) {
        this(param, side);
        this.color = color;
    }
    /**
     * 設計図の設定を流し込む
     * @param param 位置や大きさのパラメータ
     * @param color 色
     */
    public Blueprint(ParamDrawing param, Color color) {
        this(param);
        this.color = color;
    }
    /**
     * 設計図のデータをコピー
     */
    public Blueprint(Blueprint bp) {
        param    = new ParamDrawing(bp.param);
        side     = new SideDrawing(bp.side);
        drawMode = bp.drawMode;
        color    = null;             // 色はコピーしない
    }

    // ----------------------------------------------------------------------- //

    // パラメータの再設定
    /**
     * 位置と大きさの設定
     * @param param Paramパラメータ
     */
    public void setParam(ParamDrawing param) {
        this.param = param;
    }
    /**
     * 上下左右寄せと向きの設定
     * @param side Sideパラメータ
     */
    public void setSide(SideDrawing side) {
        this.side = side;
    }

    /** 描画モードを図形から線（タテ・ヨコ）にする **/
    public void setDrawLineMode() {
        if(drawMode == DrawMode.POLYGON) {
            drawMode = DrawMode.LINE;
        }
    }
    /** 描画モードを図形から線（長さ・角度）にする **/
    public void setDrawLineAngleMode() {
        if(drawMode == DrawMode.POLYGON) {
            drawMode = DrawMode.LINE_ANGLE;
            param.setLA(param.width, param.height);
            param.setArea(new Dimension(0, 0) );
        }
    }
    // 描画モードを指定する
    public void setDrawRectangleMode() {
        if(drawMode == DrawMode.POLYGON) {
            drawMode = DrawMode.RECTANGLE;
        } else if(drawMode == DrawMode.REGULAR) {
            drawMode = DrawMode.RECTANGLE_REGULAR;
        }
    }
    public void setDrawOvalMode() {
        if(drawMode == DrawMode.POLYGON) {
            drawMode = DrawMode.OVAL;
        } else if(drawMode == DrawMode.REGULAR) {
            drawMode = DrawMode.OVAL_REGULAR;
        }
    }

    // パラメータ変更用
    public void setAnchorX (int anchorX) {
        param.setAnchorX(anchorX);
    }
    public void setAnchorY (int anchorY) {
        param.setAnchorY(anchorY);
    }
    public void setWidth(int width) {
        param.setWidth(width);
    }
    public void setHeight(int height) {
        param.setHeight(height);
    }
    public void setRadius(int radius) {
        param.setRadius(radius);
    }
    public void setTopWidth(int topWidth) {
        param.setTopWidth(topWidth);
    }
    public void setBottomWidth(int bottomWidth) {
        param.setBottomWidth(bottomWidth);
    }
    public void setLength(int length) {
        param.setLength(length);
    }
    public void setAngle(int angle) {
        param.setAngle(angle);
    }
    public void setAngle2(int angle2) {
        param.setAngle2(angle2);
    }
    public void setColor(Color color) {
        this.color = color;
    }
    // パラメータまとめて変更用
    public void setAnchorPoint(Point point){
        param.setAnchorPoint(point);
    }
    public void setAnchorPoint(int x, int y) {
        param.setAnchorPoint(x, y);
    }
    public void setArea(Dimension area) {
        param.setArea(area);
    }
    public void setArea(int width, int height) {
        param.setArea(width, height);
    }
    public void setAngles(int angle, int angle2) {
        param.setAngles(angle, angle2);
    }
    public void setTrapezoidArea(int topWidth, int bottomWidth, int height) {
        param.setTrapezoidArea(topWidth, bottomWidth, height);
    }


    // ----------------------------------------------------------------------- //

    // 座標の取得
    public int centerX() {
        return param.anchorX - param.width * (side.x + 1) / 2;
    }
    public int centerY() {
        return param.anchorY - param.height * (side.y + 1) / 2;
    }
    // X,Y座標の取得
    public Point getAnchorPointCenter() {
        return new Point(centerX(), centerY() );
    }
    public Point getAnchorPoint() {
        return new Point(param.anchorX, param.anchorY);
    }
    // Width,Heightの取得
    public Dimension getDimension() {
        return new Dimension(param.width, param.height);
    }
    // 色の取得
    public Color getColor() {
        return color;
    }
    // パラメータの取得
    public List<Integer> getParameter() {
        return switch(drawMode) {
            case POLYGON, LINE, RECTANGLE, OVAL
                    -> List.of(param.anchorX, param.anchorY, param.width, param.height);
            case REGULAR, RECTANGLE_REGULAR, OVAL_REGULAR
                    -> List.of(param.anchorX, param.anchorY, param.radius);
            case TRAPEZOID
                    -> List.of(param.anchorX, param.anchorY, param.topWidth, param.bottomWidth, param.height);
            case ARC
                    -> List.of(param.anchorX, param.anchorY, param.width, param.height, param.angle, param.angle2);
            case LINE_ANGLE
                    -> List.of(param.anchorX, param.anchorY, param.length, param.angle);
        };
    }

    // ----------------------------------------------------------------------- //

    /**
     * 設計図の描画（線無し）
     * @param drawStyle 描画スタイル
     */
    public void draw(Graphics2D g2d, DrawPolygon drawStyle, Color color) {
        if(param.isNonNull() ) {
            switch (drawMode) {
                case POLYGON:
                    drawStyle.draw(g2d, color, param, side);
                    break;

                case REGULAR:
                    drawStyle.drawRegular(g2d, color, param, side);
                    break;

                case TRAPEZOID:
                    DRAW_TRAPEZOID.draw(g2d, color, param, side);
                    break;

                case ARC:
                    DRAW_ARC.draw(g2d, color, param, side);
                    break;

                case LINE:
                    DRAW_LINE.draw(g2d, color, param);
                    break;

                case LINE_ANGLE:
                    DRAW_LINE.drawLA(g2d, color, param);
                    break;

                case RECTANGLE:
                    DRAW_RECT.draw(g2d, color, param, side);
                    break;

                case RECTANGLE_REGULAR:
                    DRAW_RECT.drawRegular(g2d, color, param, side);
                    break;

                case OVAL:
                    DRAW_OVAL.draw(g2d, color, param, side);
                    break;

                case OVAL_REGULAR:
                    DRAW_OVAL.drawRegular(g2d, color, param, side);
                    break;
            }
        }
    }
    /** 設計図の描画（線無し、描画スタイル省略） **/
    public void draw(Graphics2D g2d, Color color) {
        if(param.isNonNull() ) {
            switch (drawMode) {
                // POLYGONとREGULARの場合のみ除けて実行
                case POLYGON, REGULAR:
                    break;

                default:
                    draw(g2d, null, color);
            }
        }
    }
    /** 設計図の描画（中塗りあり） **/
    public void fill(Graphics2D g2d, DrawPolygon drawStyle, Color color) {
        if(param.isNonNull() ) {
            switch (drawMode) {
                case POLYGON:
                    drawStyle.fill(g2d, color, param, side);
                    break;

                case REGULAR:
                    drawStyle.fillRegular(g2d, color, param, side);
                    break;

                case TRAPEZOID:
                    DRAW_TRAPEZOID.fill(g2d, color, param, side);
                    break;

                case ARC:
                    DRAW_ARC.fill(g2d, color, param, side);
                    break;

                case RECTANGLE:
                    DRAW_RECT.fill(g2d, color, param, side);
                    break;

                case RECTANGLE_REGULAR:
                    DRAW_RECT.fillRegular(g2d, color, param, side);
                    break;

                case OVAL:
                    DRAW_OVAL.fill(g2d, color, param, side);
                    break;

                case OVAL_REGULAR:
                    DRAW_OVAL.fillRegular(g2d, color, param, side);
                    break;

                default:
                    break;
            }
        }
    }
    /** 設計図の描画（中塗りあり、描画スタイル省略） **/
    public void fill(Graphics2D g2d, Color color) {
        if(param.isNonNull() ) {
            switch (drawMode) {
                // POLYGONとREGULARの場合のみ除けて実行
                case POLYGON, REGULAR:
                    break;

                default:
                    fill(g2d, null, color);
                    break;
            }
        }
    }

    // 色省略版の描画
    public void draw(Graphics2D g2d, DrawPolygon drawStyle) {
        if(color != null) {
            draw(g2d, drawStyle, color);
        }
    }
    public void draw(Graphics2D g2d) {
        if(color != null) {
            draw(g2d, color);
        }
    }
    public void fill(Graphics2D g2d, DrawPolygon drawStyle) {
        if(color != null) {
            fill(g2d, drawStyle, color);
        }
    }
    public void fill(Graphics2D g2d) {
        if(color != null) {
            fill(g2d, color);
        }
    }
}
