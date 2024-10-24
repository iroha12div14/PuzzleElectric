package scenes.scene.game.gameobject;

import sceneutil.draw.ParamDrawing;
import sceneutil.draw.blueprint.Blueprint;
import sceneutil.font.FontBox;

import java.awt.*;
import java.util.List;

public class NormalWall extends GameObject {
    /** コン **/
    public NormalWall(float x, float y) {
        super(x, y, WIDTH, HEIGHT, GameObjectAttribute.NORMAL_WALL);

        mainBp.setDrawRectangleMode();
        mainBp.setAnchorPoint((int) x, (int) y);
    }

    @Override
    public List<Blueprint> getBlueprint() {
        return List.of(mainBp);
    }

    @Override
    public List<FontBox> getFontBox(Graphics2D g2d) {
        return List.of();
    }

    private static final int WIDTH = 70 * 2 + 50 * 6;
    private static final int HEIGHT = 20;

    private final Color normalWallColor = Color.GRAY;
    private final ParamDrawing mainParam = new ParamDrawing(null, null, WIDTH, HEIGHT);
    private final Blueprint mainBp = new Blueprint(mainParam, normalWallColor);
}
