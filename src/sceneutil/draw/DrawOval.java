package sceneutil.draw;

import java.awt.*;

/** 楕円の描画を行うクラス **/
public class DrawOval extends DrawPolygon {
    @Override
    public void draw(Graphics2D g2d, Color c, ParamDrawing param) {
        g2d.setColor(c);
        g2d.drawOval(param.anchorX, param.anchorY, param.width, param.height);
    }
    @Override
    public void fill(Graphics2D g2d, Color c, ParamDrawing param) {
        g2d.setColor(c);
        g2d.fillOval(param.anchorX, param.anchorY, param.width, param.height);
    }
}
