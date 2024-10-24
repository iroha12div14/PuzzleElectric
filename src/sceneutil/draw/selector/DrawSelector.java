package sceneutil.draw.selector;

import sceneutil.draw.ParamDrawing;
import sceneutil.draw.SideDrawing;
import sceneutil.draw.blueprint.Blueprint;
import sceneutil.font.FontBox;

import java.awt.*;

/** 文字列配列の一覧を並べ、選択された項目を示すセレクタを作成するクラス **/
public class DrawSelector {
    private final Color frameColor = new Color(250, 150, 0);

    ParamDrawing frameParam = new ParamDrawing(null, null, null, null);
    ParamDrawing innerParam = new ParamDrawing(null, null, null, null);
    ParamDrawing selectorParam = new ParamDrawing(null, null, 16, 0, 16);
    SideDrawing sideHorizontal = new SideDrawing(SideDrawing.LEFT, SideDrawing.BOTTOM);
    SideDrawing sideVertical = new SideDrawing(SideDrawing.LEFT, SideDrawing.BOTTOM, SideDrawing.VERTICAL);

    Blueprint frame = new Blueprint(frameParam, sideHorizontal, frameColor);
    Blueprint inner = new Blueprint(innerParam, sideHorizontal, Color.WHITE);
    Blueprint selector = new Blueprint(selectorParam, sideVertical);
    FontBox horizontalText, verticalText;

    /**
     * 横並びのセレクタを描画する
     * @param strArray  選択できる項目の一覧（文字列配列）
     * @param pointer   選択されている項目
     * @param x         X座標
     * @param y         Y座標
     * @param f         文字のフォント
     */
    public void drawSelector(Graphics2D g2d, String[] strArray, int pointer, int x, int y, Font f) {
        int i = 0;
        for(String str : strArray) {
            horizontalText = new FontBox(str, f, Color.WHITE, new Point(x, y) ); // 色は仮置き
            int width = horizontalText.strWidth(g2d);
            int height = horizontalText.strHeight(g2d);
            if(i == pointer) {
                frameParam = new ParamDrawing(x - 3, y + 4, width + 5, height + 4);
                frame.setParam(frameParam);
                frame.setDrawRectangleMode();
                frame.fill(g2d);

                innerParam = new ParamDrawing(x - 1, y + 2, width + 1, height);
                inner.setParam(innerParam);
                inner.setDrawRectangleMode();
                inner.fill(g2d);

                horizontalText.setColor(Color.BLACK);
            } else {
                horizontalText.setColor(Color.WHITE);
            }
            horizontalText.drawWithSetColor(g2d);
            x += width + 15;
            i++;
        }
    }

    /**
     * 縦並びのセレクタを描画する
     * @param strArray  選択できる項目の一覧（文字列配列）
     * @param cursor    選択されている項目
     * @param x         X座標
     * @param y         Y座標
     * @param f         文字のフォント
     * @param paddingY  項目同士でどれだけ間隔を空けるか
     */
    public void drawVerticalSelector(Graphics2D g2d, String[] strArray, int cursor, int x, int y, Font f, int paddingY) {
        int cursorY = y + paddingY * cursor;

        selectorParam.setAnchorPoint(x, cursorY);
        selector.setParam(selectorParam);
        selector.fill(g2d, Color.WHITE);
        selector.draw(g2d, Color.BLACK); // 設計図を使いまわして枠線を描く

        for(String str : strArray) {
            verticalText = new FontBox(str, f, Color.WHITE, new Point(x + 24, y) );
            verticalText.drawWithSetColor(g2d);
            y += paddingY;
        }
    }
}
