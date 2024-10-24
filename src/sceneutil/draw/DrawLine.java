package sceneutil.draw;

import java.awt.*;

/** 直線の描画を行うクラス **/
public class DrawLine implements Draw {
    /**
     * ある点から長さと角度で線を引く
     * @param c     色
     * @param param 描画パラメータ
     */
    public void drawLA(Graphics2D g2d, Color c, ParamDrawing param){
        ParamDrawing p = cartesianFixParam(param);
        draw(g2d, c, p);
    }

    /**
     * 曲座標変換
     * @param param パラメータ
     * @return 変換後のパラメータ
     */
    private ParamDrawing cartesianFixParam(ParamDrawing param){
        double radians = Math.toRadians(param.angle);
        int px = param.anchorX + (int) (param.length * Math.cos(radians));
        int y2 = param.anchorY - (int) (param.length * Math.sin(radians));

        ParamDrawing p = new ParamDrawing(param);
        p.setArea(new Dimension(px, y2));
        return p;
    }

    /**
     * 線の描画
     * @param c     色
     * @param param 描画パラメータ
     */
    @Override
    public void draw(Graphics2D g2d, Color c, ParamDrawing param) {
        g2d.setColor(c);
        g2d.drawLine(param.anchorX, param.anchorY, param.width, param.height);
    }
    /**
     * fillは使わない 中身なし
     */
    @Override
    public void fill(Graphics2D g2d, Color c, ParamDrawing param) { }
}
