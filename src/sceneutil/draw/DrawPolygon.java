package sceneutil.draw;

import java.awt.*;

/** 辺で囲まれた図形の描画を行うクラス。長方形・楕円・弧、及びその正則図形を描画する。 **/
public abstract class DrawPolygon implements Draw {
    /**
     * 図形の描画（辺のみ）
     * @param c     色
     * @param param パラメータ（位置・大きさ）
     * @param side  パラメータ（上下左右寄せ）
     */
    public void draw(Graphics2D g2d, Color c, ParamDrawing param, SideDrawing side) {
        ParamDrawing p = sideFixParam(param, side);
        draw(g2d, c, p);
    }
    /**
     * 図形の描画（中塗りあり）
     * @param c     色
     * @param param パラメータ（位置・大きさ）
     * @param side  パラメータ（上下左右寄せ）
     */
    public void fill(Graphics2D g2d, Color c, ParamDrawing param, SideDrawing side) {
        ParamDrawing p = sideFixParam(param, side);
        fill(g2d, c, p);
    }

    // 座標補正
    private ParamDrawing sideFixParam(ParamDrawing param, SideDrawing side){
        int x = param.anchorX - param.width  * (1 + side.x) / 2;
        int y = param.anchorY - param.height * (1 + side.y) / 2;

        // sideによる部分的な書き換え
        ParamDrawing p = new ParamDrawing(param);
        p.setAnchorPoint(x, y);
        return p;
    }

    // 正多角形

    /**
     * 正則図形の描画（線のみ）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     * @param s     描画パラメータ（上下左右寄せ）
     */
    public void drawRegular(Graphics2D g2d, Color c, ParamDrawing param, SideDrawing s) {
        ParamDrawing p = R2WH(param);
        draw(g2d, c, p, s);
    }
    /**
     * 正則図形の描画（中塗りあり）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     * @param s     描画パラメータ（上下左右寄せ）
     */
    public void fillRegular(Graphics2D g2d, Color c, ParamDrawing param, SideDrawing s) {
        ParamDrawing p = R2WH(param);
        fill(g2d, c, p, s);
    }
    /**
     * 正則図形の描画（線のみ）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    public void drawRegular(Graphics2D g2d, Color c, ParamDrawing param) {
        ParamDrawing p = R2WH(param);
        draw(g2d, c, p);
    }
    /**
     * 正則図形の描画（中塗りあり）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    public void fillRegular(Graphics2D g2d, Color c, ParamDrawing param) {
        ParamDrawing p = R2WH(param);
        fill(g2d, c, p);
    }

    // 半径のみの表記から幅・高さの変換
    private ParamDrawing R2WH(ParamDrawing param){
        ParamDrawing p = new ParamDrawing(param);
        p.setArea(param.radius * 2, param.radius * 2);
        p.setRadius(0);
        return p;
    }
}
