package sceneutil.draw;

import java.awt.*;

/** 長方形の描画を行うクラス **/
public class DrawRect extends DrawPolygon {
    @Override
    public void draw(Graphics2D g2d, Color c, ParamDrawing param) {
        g2d.setColor(c);
        g2d.drawRect(param.anchorX, param.anchorY, param.width, param.height);
    }
    @Override
    public void fill(Graphics2D g2d, Color c, ParamDrawing param) {
        g2d.setColor(c);
        g2d.fillRect(param.anchorX, param.anchorY, param.width, param.height);
    }
}
