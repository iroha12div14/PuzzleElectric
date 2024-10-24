package sceneutil.draw;

import java.awt.*;

/** 台形の描画を行うクラス。上辺・下辺・高さを指定して描画する。 **/
public class DrawTrapezoid implements Draw {
    // 上下・左右・中央寄せによる座標補正込み
    // Sideパラメータがここのメソッドでロストするので、Side.DIRキーの値だけ退避している(あまりスマートじゃない処理)
    /**
     * 台形の描画（位置補正あり、辺のみ）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     * @param side  描画パラメータ（上下左右寄せと向き）
     */
    public void draw(Graphics2D g2d, Color c, ParamDrawing param, SideDrawing side) {
        ParamDrawing p = sideFixParam(param, side);
        setDIR(side.dir);
        draw(g2d, c, p);
    }

    /**
     * 台形の描画（位置補正あり、中塗りあり）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     * @param side  描画パラメータ（上下左右寄せと向き）
     */
    public void fill(Graphics2D g2d, Color c, ParamDrawing param, SideDrawing side) {
        ParamDrawing p = sideFixParam(param, side);
        setDIR(side.dir);
        fill(g2d, c, p);
    }

    // 座標補正
    private ParamDrawing sideFixParam(ParamDrawing param, SideDrawing side){
        // Side_X,_Y未記入の場合は左上寄せ
        int sa = switch(side.dir){
            case SideDrawing.HORIZONTAL -> side.x;
            case SideDrawing.VERTICAL   -> side.y;
            default -> SideDrawing.LEFT;
        };
        int sb = switch(side.dir){
            case SideDrawing.HORIZONTAL -> side.y;
            case SideDrawing.VERTICAL   -> side.x;
            default -> SideDrawing.TOP;
        };

        // より長い辺を採用して補正する
        int w = Math.max(param.topWidth, param.bottomWidth);
        int qa = w * (sa+1) / 2;
        int qb = param.height * (sb+1) / 2;

        // Side.DIRによる部分的な書き換え
        // ここだけ説明不可能な構造してる(良くない)
        ParamDrawing p = new ParamDrawing(param);
        if(side.dir == SideDrawing.HORIZONTAL) {
            p.setAnchorPoint(new Point(param.anchorX - qa, param.anchorY - qb) );
        }
        else if(side.dir == SideDrawing.VERTICAL) {
            p.setAnchorPoint(new Point(param.anchorY - qa, param.anchorX - qb) );
        }
        // else -> 無補正(HORIZONTAL)
        return p;
    }

    /**
     * 台形の描画（辺のみ）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    @Override
    public void draw(Graphics2D g2d, Color c, ParamDrawing param) {
        int[] pArrA = pointArrayA(param);
        int[] pArrB = pointArrayB(param);

        g2d.setColor(c);
        if(getDIR() == SideDrawing.VERTICAL) {
            g2d.drawPolygon(pArrB, pArrA, 4);
        }
        else if(getDIR() == SideDrawing.HORIZONTAL) {
            g2d.drawPolygon(pArrA, pArrB, 4);
        }
        else { // その他は一応デフォルト(=HORIZONTAL)扱い
            g2d.drawPolygon(pArrA, pArrB, 4);
        }
    }
    /**
     * 台形の描画（中塗りあり）
     * @param c     色
     * @param param 描画パラメータ（位置と大きさ）
     */
    @Override
    public void fill(Graphics2D g2d, Color c, ParamDrawing param) {
        int[] pArrA = pointArrayA(param);
        int[] pArrB = pointArrayB(param);

        g2d.setColor(c);
        if(getDIR() == SideDrawing.VERTICAL) {
            g2d.fillPolygon(pArrB, pArrA, 4);
        }
        else if(getDIR() == SideDrawing.HORIZONTAL) {
            g2d.fillPolygon(pArrA, pArrB, 4);
        }
        else { // その他は一応デフォルト(=HORIZONTAL)扱い
            g2d.fillPolygon(pArrA, pArrB, 4);
        }
    }

    // 座標要素の構成
    private int[] pointArrayA(ParamDrawing param) {
        int wt = param.topWidth;
        int wb = param.bottomWidth;

        int wah = (wt > wb) ? wt / 2 : wb / 2;
        int wbh = (wt + wb) / 2 - wah;
        int p = param.anchorX;
        return new int[] {p, p+wah*2, p+wah+wbh, p+wah-wbh};
    }
    private int[] pointArrayB(ParamDrawing param) {
        int wt = param.topWidth;
        int wb = param.bottomWidth;
        int h = param.height;

        int ha = (wt > wb) ? 0 : h;
        int hb = h - ha;
        int p = param.anchorY;
        return new int[] {p+ha, p+ha, p+hb, p+hb};
    }

    //Side.DIRキーの値が取得できなくなるの面倒なので、それ用のセッタとゲッタと変数を用意した
    private void setDIR(int d){
        dir = d;
    }
    private int getDIR(){
        return dir;
    }
    private int dir;
}
