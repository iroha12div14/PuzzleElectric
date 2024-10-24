package scenes.scene.game.gameobject;

import sceneutil.draw.ParamDrawing;
import sceneutil.draw.SideDrawing;
import sceneutil.draw.blueprint.Blueprint;
import sceneutil.font.FontBox;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VirtualController extends GameObject{
    /** コン **/
    public VirtualController(int x, int y) {
        super(x, y, WIDTH, HEIGHT, GameObjectAttribute.OTHER);

        buttonLeftBp.setAnchorPoint(getPoint(LEFT, false));
        buttonLeftBp.setDrawRectangleMode();
        buttonLeftBp.setColor(buttonColor);
        buttonIconLeftBp.setAnchorPoint(getPoint(LEFT, false));
        buttonIconLeftBp.setSide(iconVtSide);
        buttonIconLeftBp.setColor(buttonIconColor);

        buttonDownBp.setAnchorPoint(getPoint(DOWN, false));
        buttonDownBp.setDrawRectangleMode();
        buttonDownBp.setColor(buttonColor);
        buttonIconDownBp.setAnchorPoint(getPoint(DOWN, false));
        buttonIconDownBp.setHeight(-ICON_HEIGHT);
        buttonIconDownBp.setSide(iconHzSide);
        buttonIconDownBp.setColor(buttonIconColor);

        buttonRightBp.setAnchorPoint(getPoint(RIGHT, false));
        buttonRightBp.setDrawRectangleMode();
        buttonRightBp.setColor(buttonColor);
        buttonIconRightBp.setAnchorPoint(getPoint(RIGHT, false));
        buttonIconRightBp.setHeight(-ICON_HEIGHT);
        buttonIconRightBp.setSide(iconVtSide);
        buttonIconRightBp.setColor(buttonIconColor);

        buttonUpBp.setAnchorPoint(getPoint(UP, false));
        buttonUpBp.setDrawRectangleMode();
        buttonUpBp.setColor(buttonColor);
        buttonIconUpBp.setAnchorPoint(getPoint(UP, false));
        buttonIconUpBp.setSide(iconHzSide);
        buttonIconUpBp.setColor(buttonIconColor);
    }

    // 設計図の座標の取得
    private Point getPoint(int dir, boolean isPressed) {
        Point point = getPoint();
        int padding = isPressed ? 3 : 0;

        return dir == LEFT
                ?   new Point(point.x - WIDTH / 3 + padding, point.y + HEIGHT / 4 + padding)
                : dir == DOWN
                ?   new Point(point.x             + padding, point.y + HEIGHT / 4 + padding)
                : dir == RIGHT
                ?   new Point(point.x + WIDTH / 3 + padding, point.y + HEIGHT / 4 + padding)
                : dir == UP
                ?   new Point(point.x + padding, point.y - HEIGHT / 4 + padding)
                : new Point(300, 300); // ダミー
    }
    // 設計図の色の取得
    private Color getColor(boolean isPressed) {
        return isPressed ? buttonPressColor : buttonColor;
    }
    // ボタンの押下によって設計図のパラメータ（ボタン位置、ボタン色、ボタンアイコン位置）を変化
    public void inputButtonPress(boolean isLeftHeld, boolean isDownHeld, boolean isRightHeld, boolean isUpHeld) {
        buttonLeftBp.setAnchorPoint(getPoint(LEFT, isLeftHeld));
        buttonLeftBp.setColor(getColor(isLeftHeld));
        buttonIconLeftBp.setAnchorPoint(getPoint(LEFT, isLeftHeld));

        buttonDownBp.setAnchorPoint(getPoint(DOWN, isDownHeld));
        buttonDownBp.setColor(getColor(isDownHeld));
        buttonIconDownBp.setAnchorPoint(getPoint(DOWN, isDownHeld));

        buttonRightBp.setAnchorPoint(getPoint(RIGHT, isRightHeld));
        buttonRightBp.setColor(getColor(isRightHeld));
        buttonIconRightBp.setAnchorPoint(getPoint(RIGHT, isRightHeld));

        buttonUpBp.setAnchorPoint(getPoint(UP, isUpHeld));
        buttonUpBp.setColor(getColor(isUpHeld));
        buttonIconUpBp.setAnchorPoint(getPoint(UP, isUpHeld));
    }

    @Override
    public List<Blueprint> getBlueprint() {
        List<Blueprint> bps = new ArrayList<>();

        bps.add(buttonLeftBp);
        bps.add(buttonDownBp);
        bps.add(buttonRightBp);
        bps.add(buttonUpBp);

        bps.add(buttonIconLeftBp);
        bps.add(buttonIconDownBp);
        bps.add(buttonIconRightBp);
        bps.add(buttonIconUpBp);

        return bps;
    }

    @Override
    public List<FontBox> getFontBox(Graphics2D g2d) {
        return List.of();
    }

    // size:50x50, grid:3x2
    private static final int WIDTH  = 162;
    private static final int HEIGHT = 108;

    private static final int BUTTON_RADIUS = 23;
    private static final int ICON_WIDTH  = 24;
    private static final int ICON_HEIGHT = 16;

    private static final int LEFT  = 0;
    private static final int DOWN  = 1;
    private static final int RIGHT = 2;
    private static final int UP    = 3;

    SideDrawing centerSide = new SideDrawing(SideDrawing.CENTER, SideDrawing.CENTER);
    SideDrawing iconHzSide = new SideDrawing(SideDrawing.CENTER, SideDrawing.CENTER, SideDrawing.HORIZONTAL);
    SideDrawing iconVtSide = new SideDrawing(SideDrawing.CENTER, SideDrawing.CENTER, SideDrawing.VERTICAL);

    ParamDrawing buttonIconParam = new ParamDrawing(null, null, 0, ICON_WIDTH, ICON_HEIGHT);
    Color buttonIconColor = Color.DARK_GRAY;
    Blueprint buttonIconBpTemp = new Blueprint(buttonIconParam);
    Blueprint buttonIconLeftBp  = new Blueprint(buttonIconBpTemp);
    Blueprint buttonIconDownBp  = new Blueprint(buttonIconBpTemp);
    Blueprint buttonIconRightBp = new Blueprint(buttonIconBpTemp);
    Blueprint buttonIconUpBp    = new Blueprint(buttonIconBpTemp);

    ParamDrawing buttonParam = new ParamDrawing(null, null, BUTTON_RADIUS);
    Color buttonColor = Color.LIGHT_GRAY;
    Color buttonPressColor = new Color(255, 200, 150);
    Blueprint buttonBpTemp = new Blueprint(buttonParam, centerSide);
    Blueprint buttonLeftBp  = new Blueprint(buttonBpTemp);
    Blueprint buttonDownBp  = new Blueprint(buttonBpTemp);
    Blueprint buttonRightBp = new Blueprint(buttonBpTemp);
    Blueprint buttonUpBp    = new Blueprint(buttonBpTemp);
}
