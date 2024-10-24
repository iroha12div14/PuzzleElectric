package sceneutil.draw;

import java.awt.*;

// sin,cosによる曲座標変換(角度が時計回り)とdrawArcの描画(角度が反時計回り)が上下逆(？？？？？)なので、
// 角度の扱いを描画座標を基準として統一した……　が、
// Graphics -> Graphics2Dにキャストするとちゃんと時計回りの挙動になるらしい。
// まさかの差し戻し

/** 弧と扇の描画を行うクラス **/
public class DrawArc extends DrawPolygon {
    /**
     * 弧の描画（中抜きの扇ではない）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    @Override
    public void draw(Graphics2D g2d, Color c, ParamDrawing param) {
        ParamDrawing p = convertAngle(param);
        g2d.setColor(c);
        g2d.drawArc(p.anchorX, p.anchorY, p.width, p.height, p.angle, p.angle2);
    }

    /**
     * 扇の描画（中塗りあり）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    @Override
    public void fill(Graphics2D g2d, Color c, ParamDrawing param) {
        ParamDrawing p = convertAngle(param);
        g2d.setColor(c);
        g2d.fillArc(p.anchorX, p.anchorY, p.width, p.height, p.angle, p.angle2);
    }

    private ParamDrawing convertAngle(ParamDrawing param) {
        int startAngle = param.angle;
        int arcAngle = param.angle2 - param.angle;

        // 部分的な書き換え
        ParamDrawing p = new ParamDrawing(param);
        p.setAngles(startAngle, arcAngle);
        return p;
    }
}
