package sceneutil.draw;

import java.awt.*;
import java.util.Map;

/** 図形の描画を行うインターフェース **/
public interface Draw {
    /**
     * 辺だけ描画
     * @param g2d   ？
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    void draw(Graphics2D g2d, Color c, ParamDrawing param);

    /**
     * 中を塗って描画
     * @param g2d   ？
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    void fill(Graphics2D g2d, Color c, ParamDrawing param);
}
