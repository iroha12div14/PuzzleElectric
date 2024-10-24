package sceneutil.draw.slider;

import sceneutil.draw.ParamDrawing;
import sceneutil.draw.SideDrawing;
import sceneutil.draw.blueprint.Blueprint;

import java.awt.*;

/** 目盛とその間にポインタを描画し、数値を視覚的に示すスライダーを描画するクラス **/
public class DrawSlider {
    private final Color frameColor = new Color(250, 150, 0);

    ParamDrawing sliderLineParam = new ParamDrawing(null, null, null, 2);
    ParamDrawing mParam = new ParamDrawing(null, null, 2, 20);
    SideDrawing mSide = new SideDrawing(SideDrawing.CENTER, SideDrawing.TOP);
    ParamDrawing pointerInnerParam = new ParamDrawing(null, null, 6, null);
    ParamDrawing pointerFrameParam = new ParamDrawing(null, null, 2, null);
    SideDrawing pointerSide = new SideDrawing(SideDrawing.CENTER, SideDrawing.CENTER);
    ParamDrawing[] bpScaleParam;

    Blueprint sliderLine = new Blueprint(sliderLineParam, Color.WHITE);
    Blueprint min = new Blueprint(mParam, mSide, Color.WHITE);
    Blueprint max = new Blueprint(mParam, mSide, Color.WHITE);
    Blueprint pointerInner = new Blueprint(pointerInnerParam, pointerSide, Color.WHITE);
    Blueprint pointerFrame = new Blueprint(pointerFrameParam, pointerSide, frameColor);
    Blueprint[] bpScale;

    /**
     * スライダーの描画
     * @param x         X座標
     * @param y         Y座標
     * @param width     スライダーの幅
     * @param scale     目盛の数
     * @param pointer   ポインタ(0 ≦ pointer ≦ scale)
     */
    public void drawSlider(Graphics2D g2d, int x, int y, int width, int scale, int pointer) {
        sliderLine.setAnchorPoint(x, y + 9);
        sliderLine.setWidth(width);
        sliderLine.setDrawRectangleMode();
        sliderLine.fill(g2d);

        min.setAnchorPoint(x, y);
        min.setDrawRectangleMode();
        min.fill(g2d);

        max.setAnchorPoint(x + width, y);
        max.setDrawRectangleMode();
        max.fill(g2d);

        bpScale = new Blueprint[scale];
        bpScaleParam = new ParamDrawing[scale];
        for(int i = 0; i < scale; i++) {
            int xs = width * i / scale;
            bpScaleParam[i] = new ParamDrawing(x + xs, y + 5, x + xs, y + 15);
            bpScale[i] = new Blueprint(bpScaleParam[i], Color.WHITE);
            bpScale[i].setDrawLineMode();
            bpScale[i].draw(g2d);
        }
        int px = width * pointer / scale;
        int h = pointer == 0 || pointer == scale ? 24 : 16;
        pointerFrameParam = new ParamDrawing(x + px, y + 10, 6, h);
        pointerFrame.setParam(pointerFrameParam);
        pointerFrame.setDrawRectangleMode();
        pointerFrame.fill(g2d);

        pointerInnerParam = new ParamDrawing(x + px, y + 10, 2, h - 4);
        pointerInner.setParam(pointerInnerParam);
        pointerInner.setDrawRectangleMode();
        pointerInner.fill(g2d);
    }
}
