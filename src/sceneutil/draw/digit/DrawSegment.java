package sceneutil.draw.digit;

import sceneutil.draw.DrawTrapezoid;
import sceneutil.draw.ParamDrawing;
import sceneutil.draw.SideDrawing;
import sceneutil.draw.blueprint.Blueprint;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;

/** セグメントの描画を行うクラス */
public class DrawSegment extends DrawTrapezoid {
    /**
     * セグメントの描画
     * @param number        表示する数値
     * @param size          大きさ（100が基準値として高さが100、幅が60になる）
     * @param posX          X座標
     * @param posY          Y座標
     * @param drawFrame     枠線の描画の有無
     * @param segColorON    セグメント点灯時の色
     * @param segColorOFF   セグメント消灯時の色
     */
    public void drawSegmentNumber(
            Graphics2D g2d,
            int number,
            int size, int posX, int posY, int bold,
            boolean drawFrame,
            Color segColorON,
            Color segColorOFF
    ) {
        Map<segPosition, Boolean> segState = updateSegment(number);
        for(segPosition seg : segPosition.values() ){
            // セグメントの設計図を作成
            // 台形を対称に貼り合わせて六角形を作る
            Blueprint segment1 = segBlueprint(seg, size, posX, posY, bold, false);
            Blueprint segment2 = segBlueprint(seg, size, posX, posY, bold, true);
            setSegmentSide(segment1, seg);
            setSegmentSide(segment2, seg);

            Color segColor = segState.get(seg) ? segColorON : segColorOFF;

            // セグメントを描画
            segment1.setColor(segColor);
            segment1.fill(g2d);
            segment2.setColor(segColor);
            segment2.fill(g2d);
            // セグメントの枠線を描画
            if(drawFrame) {
                segment1.setColor(segColorOFF);
                segment1.draw(g2d);
                segment2.setColor(segColorOFF);
                segment2.draw(g2d);
            }
        }
    }

    // セグメントの状態の更新
    // 一旦全部OFFにしてから必要な場所をONにする感じ 正直ちょっと手間
    private Map<segPosition, Boolean> updateSegment(int number){
        initSegment();
        for(segPosition seg : numberToSegmentLight(number) ) {
            segmentState.put(seg, true);
        }
        return segmentState;
    }
    // イニシャライザ
    private void initSegment(){
        for(segPosition seg : segPosition.values() ){
            segmentState.put(seg, false);
        }
    }
    // コンストラクタ ついでに初期化
    public DrawSegment(){
        initSegment();
    }

    // セグメントの点灯状態
    private final Map<segPosition, Boolean> segmentState = new HashMap<>();
    // セグメントの点灯位置定義
    enum segPosition {
        TOP,
        LEFT_TOP,
        LEFT_BOTTOM,
        CENTER,
        RIGHT_TOP,
        RIGHT_BOTTOM,
        BOTTOM
    }
    // 省略語の登録
    final static segPosition T  = segPosition.TOP;
    final static segPosition LT = segPosition.LEFT_TOP;
    final static segPosition LB = segPosition.LEFT_BOTTOM;
    final static segPosition C  = segPosition.CENTER;
    final static segPosition RT = segPosition.RIGHT_TOP;
    final static segPosition RB = segPosition.RIGHT_BOTTOM;
    final static segPosition B  = segPosition.BOTTOM;

    // ナンバーに対する点灯セグメントの一覧
    private segPosition[] numberToSegmentLight(int num) {
        return switch(num) {
            case 0 -> new segPosition[] {T, LT, LB, RT, RB, B};
            case 1 -> new segPosition[] {RT, RB};
            case 2 -> new segPosition[] {T, LB, C, RT, B};
            case 3 -> new segPosition[] {T, C, RT, RB, B};
            case 4 -> new segPosition[] {LT, C, RT, RB};
            case 5 -> new segPosition[] {T, LT, C, RB, B};
            case 6 -> new segPosition[] {T, LT, LB, C, RB, B};
            case 7 -> new segPosition[] {T, LT, RT, RB};
            case 8 -> new segPosition[] {T, LT, LB, C, RT, RB, B};
            case 9 -> new segPosition[] {T, LT, C, RT, RB, B};
            default -> new segPosition[] {};
        };
    }
    // セグメントの点灯位置定義に対する描画パラメータ
    private Blueprint segBlueprint(
            segPosition seg,
            int size,
            int paddingX,
            int paddingY,
            int bold,
            boolean reverse
    ) {
        // switch文でenumを使う場合は省略語だと判定できないっぽい
        int dtzX = switch(seg) {
            case TOP, LEFT_TOP, LEFT_BOTTOM, CENTER, BOTTOM -> bold / 2;
            case RIGHT_TOP, RIGHT_BOTTOM -> SEGMENT_WIDTH - bold / 2;
        };
        int dtzY = switch (seg) {
            case TOP, LEFT_TOP, RIGHT_TOP -> bold / 2;
            case LEFT_BOTTOM, CENTER, RIGHT_BOTTOM -> SEGMENT_HEIGHT / 2;
            case BOTTOM -> SEGMENT_HEIGHT - bold / 2;
        };
        int dtzWT = switch (seg) {
            case TOP, CENTER, BOTTOM -> SEGMENT_WIDTH - bold;
            case LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM -> (SEGMENT_HEIGHT - bold) / 2;
        };
        int dtzWB = dtzWT - bold;
        // 高さパラメータに負の値を入れると基準点をそのままに逆向きに描画できる裏技的仕様
        int dtzH = (!reverse) ? bold / 2 : -bold / 2;

        // パラメータの挿入した設計図を返す
        ParamDrawing param = new ParamDrawing(
                paddingX + resize(dtzX, size),
                paddingY + resize(dtzY, size),
                resize(dtzWT, size),
                resize(dtzWB, size),
                resize(dtzH, size)
        );
        return new Blueprint(param);
    }
    private void setSegmentSide(Blueprint segment, segPosition s) {
        SideDrawing segSide;
        if(s == LT || s == LB || s == RT || s == RB) {
            segSide = new SideDrawing(SideDrawing.LEFT, SideDrawing.TOP, SideDrawing.VERTICAL);
        }
        else { // s == T, C, B: HORIZONTAL
            segSide = new SideDrawing(); // LEFT, TOP, VERTICAL
        }
        segment.setSide(segSide);
    }
    // パラメータのリサイズ用
    private int resize(int val, int size) {
        return Math.round((float) (val * size) / 100);
    }

    // セグメントの高さ、幅の基準値
    private static final int SEGMENT_HEIGHT = 100;
    private static final int SEGMENT_WIDTH = 60;
}
