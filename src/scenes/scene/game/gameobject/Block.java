package scenes.scene.game.gameobject;

import sceneutil.draw.ParamDrawing;
import sceneutil.draw.SideDrawing;
import sceneutil.draw.blueprint.Blueprint;
import sceneutil.font.FontBox;
import sceneutil.frametimer.FrameTimer;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Block extends GameObject {
    /** コン（初期位置設定するだけ） **/
    public Block(float x, float y, int pattern, boolean isCopy) {
        super(x, y, WIDTH, HEIGHT, GameObjectAttribute.BRICK);

        blockBpTemp.setDrawRectangleMode();
        coreBpTemp.setDrawRectangleMode();
        deleteMotionBp.setDrawRectangleMode();

        // ランダムに回路を生成
        circuit = circuitPatterns.get(pattern);
        isEnergized = new boolean[] {F, F, F, F};
        isUnEnergized = false;

        deleteMotionFlip = false;

        if( !isCopy ) {
            num = ++counter;
        } else {
            num = 0;
        }
    }
    public Block(Block block) {
        super(block.getX(), block.getY(), WIDTH, HEIGHT, GameObjectAttribute.BRICK);

        circuit = block.getCircuit().clone();
        isEnergized = new boolean[] {F, F, F, F};
        isUnEnergized = false;
        deleteMotionFlip = false;
        num = 0;
    }

    public static int getRandomPattern() {
        return rand.nextInt(circuitPatterns.size());
    }

    // 回路の設計図データ
    public Blueprint makeCircuitBp(int i) {
        Blueprint ccBp = switch (i) {
            case TOP, BOTTOM -> new Blueprint(circuitVtBpTemp);
            case LEFT, RIGHT -> new Blueprint(circuitHzBpTemp);
            default -> new Blueprint(new ParamDrawing(null, null, null, null));
        };
        Point point = getPoint();
        Point p = switch (i) {
            case TOP    -> new Point(point.x, point.y - HEIGHT / 4);
            case BOTTOM -> new Point(point.x, point.y + HEIGHT / 4);
            case LEFT   -> new Point(point.x - WIDTH / 4, point.y);
            case RIGHT  -> new Point(point.x + WIDTH / 4, point.y);
            default     -> new Point(0, 0);
        };
        ccBp.setAnchorPoint(p);
        ccBp.setDrawRectangleMode();
        return ccBp;
    }

    // 回路の取得
    public boolean[] getCircuit() {
        return circuit;
    }
    public boolean getCircuit(int dir) {
        return circuit[dir];
    }

    // 座標のセット
    public void setPlace(Point position) {
        setX(150 + position.x * WIDTH);
        setY( 75 + position.y * HEIGHT);
    }

    // 通電
    public void energize(int i) {
        isEnergized[i] = i >= 0 && i < 4; // iに-1とか来たら通電させない
    }
    public void energizeAll() {
        for(int i = 0; i < 4 ; i++) energize(i);
    }
    public void unEnergized() {
        isUnEnergized = true;
    }

    // コアの通電（任意の回路が通電すればここも通電する）
    public boolean isCoreEnergized() {
        return isEnergized[TOP] || isEnergized[LEFT] || isEnergized[RIGHT] || isEnergized[BOTTOM];
    }

    // 削除モーション用のフラグを設定
    public void setDeleteMotion(FrameTimer timer) {
        deleteMotionFlip = timer.getTimer() % 12 >= 6;
        //deleteMotionFlip = timer.getTimer() > 0; // デバッグ用
    }

    public int getNum() {
        return num;
    }
    public static int getCounter() {
        return counter;
    }
    public static void resetCounter() {
        counter = 0;
    }

    @Override
    public List<Blueprint> getBlueprint() {
        List<Blueprint> bps = new ArrayList<>();

        Point point = getPoint();

        // ブロック
        Blueprint blockBp = new Blueprint(blockBpTemp);
        blockBp.setAnchorPoint(point);
        blockBp.setDrawRectangleMode();
        blockBp.setColor(blockColor);
        bps.add(blockBp);

        // 回路
        for(int i = 0; i < 4; i++) {
            if( circuit[i] ) {
                Blueprint cc = makeCircuitBp(i);
                cc.setColor(isEnergized[i] ? energizedColor : isUnEnergized ? blockColor : circuitColor);
                bps.add(cc);
            }
        }

        // コア
        Blueprint coreBp = new Blueprint(coreBpTemp);
        coreBp.setAnchorPoint(point);
        coreBp.setDrawRectangleMode();
        coreBp.setColor(isCoreEnergized() ? energizedColor : circuitColor);
        bps.add(coreBp);

        // 削除モーション用の設計図
        deleteMotionBp.setAnchorPoint(point);
        deleteMotionBp.setColor(deleteMotionFlip ? deleteColor1 : deleteColor2);
        bps.add(deleteMotionBp);

        return bps;
    }

    @Override
    public List<FontBox> getFontBox(Graphics2D g2d) {
        Point point = getPoint();
        FontBox fb = new FontBox(
                String.valueOf(num),
                FontBox.Arial(10),
                Color.BLACK,
                new Point(point.x - 2, point.y + 18)
        );
        return List.of(fb);
    }
    // デバッグ用
    public String circuitStr() {
        StringBuilder str = new StringBuilder();
        for(boolean b : circuit) {
            str.append(b ? "1" : "0");
        }
        return str.toString();
    }

    private static final Random rand = new Random();

    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final int CORE_RADIUS = 3;
    private static final int CIRCUIT_BOLD = 6;

    private static final boolean F = false;
    private static final boolean T = true;

    public static final int TOP    = 2;
    public static final int LEFT   = 0;
    public static final int RIGHT  = 3;
    public static final int BOTTOM = 1;

    private static final List<boolean[]> circuitPatterns = Arrays.asList(
            new boolean[] {T, T, F, F},
            new boolean[] {T, F, T, F},
            new boolean[] {T, F, F, T},
            new boolean[] {F, T, T, F},
            new boolean[] {F, T, F, T},
            new boolean[] {F, F, T, T},

            new boolean[] {T, T, T, F},
            new boolean[] {T, T, F, T},
            new boolean[] {T, F, T, T},
            new boolean[] {F, T, T, T},

            new boolean[] {T, T, T, F},
            new boolean[] {T, T, F, T},
            new boolean[] {T, F, T, T},
            new boolean[] {F, T, T, T},

            new boolean[] {T, T, T, T},
            new boolean[] {T, T, T, T}
    );

    private static int counter = 0;
    private final int num;

    private final boolean[] circuit;      // 回路
    private final boolean[] isEnergized;  // 通電部分
    private boolean isUnEnergized;  // 非通電
    private boolean deleteMotionFlip;

    private final ParamDrawing blockParam = new ParamDrawing(null, null, WIDTH - 2, HEIGHT - 2);
    private final SideDrawing blockSide = new SideDrawing(SideDrawing.CENTER, SideDrawing.CENTER);
    private final Color blockColor = Color.LIGHT_GRAY;
    private final Blueprint blockBpTemp = new Blueprint(blockParam, blockSide);

    private final Color circuitColor = new Color(200, 0, 0);
    private final Color energizedColor = new Color(250, 200, 0);

    private final ParamDrawing coreParam = new ParamDrawing(null, null, CORE_RADIUS);
    private final SideDrawing coreSide = new SideDrawing(SideDrawing.CENTER, SideDrawing.CENTER);
    private final Blueprint coreBpTemp = new Blueprint(coreParam, coreSide);

    private final ParamDrawing circuitHzParam = new ParamDrawing(null, null, WIDTH / 2 - 1, CIRCUIT_BOLD);
    private final ParamDrawing circuitVtParam = new ParamDrawing(null, null, CIRCUIT_BOLD, HEIGHT / 2 - 1);
    private final SideDrawing circuitSide = new SideDrawing(SideDrawing.CENTER, SideDrawing.CENTER);
    private final Blueprint circuitHzBpTemp = new Blueprint(circuitHzParam, circuitSide);
    private final Blueprint circuitVtBpTemp = new Blueprint(circuitVtParam, circuitSide);

    private final Blueprint deleteMotionBp = new Blueprint(blockBpTemp);
    private final Color deleteColor1 = new Color(255, 255, 50, 150);
    private final Color deleteColor2 = new Color(255, 255, 50, 0);
}
