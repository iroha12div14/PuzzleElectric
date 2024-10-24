package sceneutil.draw;

import sceneutil.draw.blueprint.Blueprint;

import java.awt.*;

public class ParamDrawing {
    public Integer anchorX;
    public Integer anchorY;
    public Integer width;
    public Integer height;
    public Integer radius;
    public Integer topWidth; // 台形の上辺
    public Integer bottomWidth; // 台形の下辺
    public Integer angle;
    public Integer angle2;
    public Integer length;
    private Blueprint.DrawMode drawMode;

    /**
     * 長方形や楕円のパラメータ設定
     * @param anchorX   アンカーのX座標
     * @param anchorY   アンカーのY座標
     * @param width     幅
     * @param height    高さ
     */
    public ParamDrawing(Integer anchorX, Integer anchorY, Integer width, Integer height) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.width = width;
        this.height = height;
        drawMode = Blueprint.DrawMode.POLYGON;
    }
    /**
     * 正方形や正円のパラメータ設定
     * @param anchorX   アンカーのX座標
     * @param anchorY   アンカーのY座標
     * @param radius    半径
     */
    public ParamDrawing(Integer anchorX, Integer anchorY, Integer radius) {
        this(anchorX, anchorY, 0, 0);
        this.radius = radius;
        drawMode = Blueprint.DrawMode.REGULAR;
    }
    /**
     * 正方形や正円のパラメータ設定
     * @param anchor    アンカーの座標（Point型）
     * @param radius    半径
     */
    public ParamDrawing(Point anchor, Integer radius) {
        this(anchor.x, anchor.y, 0, 0);
        this.radius = radius;
        drawMode = Blueprint.DrawMode.REGULAR;
    }
    /**
     * 台形や三角形のパラメータ設定
     * @param anchorX       アンカーのX座標
     * @param anchorY       アンカーのY座標
     * @param topWidth      上辺
     * @param bottomWidth   下辺
     * @param height        高さ
     */
    public ParamDrawing(Integer anchorX, Integer anchorY, Integer topWidth, Integer bottomWidth, Integer height) {
        this(anchorX, anchorY, 0, height);
        this.topWidth = topWidth;
        this.bottomWidth = bottomWidth;
        drawMode = Blueprint.DrawMode.TRAPEZOID;
    }
    /**
     * 弧や扇形のパラメータ設定
     * @param anchorX   アンカーのX座標
     * @param anchorY   アンカーのY座標
     * @param width     幅
     * @param height    高さ
     * @param angle     角度①
     * @param angle2    角度②(角度①からの増分ではない)
     */
    public ParamDrawing(Integer anchorX, Integer anchorY, Integer width, Integer height, Integer angle, Integer angle2) {
        this(anchorX, anchorY, width, height);
        this.angle = angle;
        this.angle2 = angle2;
        drawMode = Blueprint.DrawMode.ARC;
    }
    /** パラメータをコピー **/
    public ParamDrawing(ParamDrawing param) {
        anchorX     = param.anchorX;
        anchorY     = param.anchorY;
        width       = param.width;
        height      = param.height;
        radius      = param.radius;
        topWidth    = param.topWidth;
        bottomWidth = param.bottomWidth;
        angle       = param.angle;
        angle2      = param.angle2;
        length      = param.length;
        drawMode    = param.drawMode;
    }

    // パラメータ変更用
    public void setAnchorX (int anchorX) {
        this.anchorX = anchorX;
    }
    public void setAnchorY (int anchorY) {
        this.anchorY = anchorY;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public void setRadius(int radius) {
        this.radius = radius;
    }
    public void setTopWidth(int topWidth) {
        this.topWidth = topWidth;
    }
    public void setBottomWidth(int bottomWidth) {
        this.bottomWidth = bottomWidth;
    }
    public void setLength(int length) {
        this.length = length;
    }
    public void setAngle(int angle) {
        this.angle = angle;
    }
    public void setAngle2(int angle2) {
        this.angle2 = angle2;
    }

    // パラメータまとめて変更用
    public void setAnchorPoint(Point point) {
        setAnchorX(point.x);
        setAnchorY(point.y);
    }
    public void setAnchorPoint(int x, int y) {
        setAnchorX(x);
        setAnchorY(y);
    }
    public void setArea(Dimension area) {
        setWidth(area.width);
        setHeight(area.height);
    }
    public void setArea(int width, int height) {
        setWidth(width);
        setHeight(height);
    }
    public void setAngles(int angle, int angle2) {
        setAngle(angle);
        setAngle2(angle2);
    }
    public void setTrapezoidArea(int topWidth, int bottomWidth, int height) {
        setTopWidth(topWidth);
        setBottomWidth(bottomWidth);
        setHeight(height);
    }
    public void setLA(int length, int angle) {
        setLength(length);
        setAngle(angle);
    }

    public Blueprint.DrawMode getDrawMode() {
        return drawMode;
    }

    // nullチェック
    public boolean isNonNull() {
        return anchorX != null && anchorY != null && switch(drawMode) {
            case POLYGON, LINE, RECTANGLE, OVAL
                    -> width != null && height != null;
            case REGULAR, RECTANGLE_REGULAR, OVAL_REGULAR
                    -> radius != null;
            case TRAPEZOID
                    -> topWidth != null && bottomWidth != null && height != null;
            case ARC
                    -> width != null && height != null && angle != null && angle2 != null;
            case LINE_ANGLE
                    -> length != null && angle != null;
        };
    }
}
