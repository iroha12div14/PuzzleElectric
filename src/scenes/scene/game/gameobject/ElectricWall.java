package scenes.scene.game.gameobject;

import sceneutil.draw.ParamDrawing;
import sceneutil.draw.SideDrawing;
import sceneutil.draw.blueprint.Blueprint;
import sceneutil.font.FontBox;
import sceneutil.frametimer.FrameTimer;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ElectricWall extends GameObject {

    /** コン **/
    public ElectricWall(float x, float y, int dir) {
        super(x, y, WIDTH, HEIGHT, GameObjectAttribute.ENERGY_WALL);
        this.dir = dir;

        wallBp.setAnchorPoint((int) x, (int) y);
        wallBp.setDrawRectangleMode();
        wallBp.setColor(dir == LEFT ? leftColor : rightColor);

        wallChargingBp.setAnchorPoint((int) x, (int) y + HEIGHT); // 左下寄せなのでアンカーの位置をHEIGHTだけ下に
        wallChargingBp.setDrawRectangleMode();

        int posX = dir == LEFT ? (int) x + WIDTH - CIRCUIT_WIDTH : (int) x;
        for(int i = 0; i < DISTANCE_Y_MAX + 1; i++) {
            int paddingY = HEIGHT / 2 / (DISTANCE_Y_MAX + 1);

            Blueprint wcBp = new Blueprint(wallCircuitBpTemp);
            wcBp.setDrawRectangleMode();
            wcBp.setColor(wallCircuitColor);
            wcBp.setAnchorY((int) y - paddingY + HEIGHT - HEIGHT * i / (DISTANCE_Y_MAX + 1));
            wcBp.setAnchorX(posX);
            wallCircuitBps.add(wcBp);

            Blueprint wccBp = new Blueprint(wallCircuitCoreBpTemp);
            wccBp.setDrawOvalMode();
            wccBp.setColor(wallCircuitColor);
            wccBp.setAnchorY((int) y - paddingY + HEIGHT - HEIGHT * i / (DISTANCE_Y_MAX + 1));
            wccBp.setAnchorX(posX + (dir == LEFT ? 0 : WIDTH / 2));
            //if(dir == RIGHT) wccBp.setRadius(2);
            wallCircuitCoreBps.add(wccBp);
        }

        chargeVal = 0; // dir == RIGHT: false;
    }

    public void charging(FrameTimer deleteBlockTimer, int pin) {
        if(dir == RIGHT) {
            chargeVal = deleteBlockTimer.getProgress();
        }
        if( !deleteBlockTimer.isZero() ) {
            if(pin != -1) {
                wallCircuitBps.get(pin).setColor(wallCircuitEnergizeColor);
                wallCircuitCoreBps.get(pin).setColor(wallCircuitEnergizeColor);
            }
        }
        else {
            for (Blueprint bp : wallCircuitBps) {
                bp.setColor(wallCircuitColor);
            }
            for(Blueprint bp : wallCircuitCoreBps) {
                bp.setColor(wallCircuitColor);
            }
        }
    }

    @Override
    public List<Blueprint> getBlueprint() {
        List<Blueprint> bps = new ArrayList<>();

        bps.add(wallBp);

        if(dir == RIGHT && chargeVal < 1.0F) {
            Color chargingColor = new Color(255, 50 + (int) (150 * chargeVal), 50, 200);
            wallChargingBp.setColor(chargingColor);
            wallChargingBp.setHeight( (int) (HEIGHT * chargeVal) );
            bps.add(wallChargingBp);
        }

        bps.addAll(wallCircuitBps);
        bps.addAll(wallCircuitCoreBps);

        return bps;
    }

    @Override
    public List<FontBox> getFontBox(Graphics2D g2d) {
        return List.of();
    }

    private static final int WIDTH  = 70;
    private static final int HEIGHT = 500;
    private static final int CIRCUIT_WIDTH = WIDTH / 2;
    private static final int CIRCUIT_BOLD = 6;
    private static final int CIRCUIT_CORE_RADIUS = 10;
    private static final int DISTANCE_Y_MAX = 9;

    public static final int LEFT  = -1;
    public static final int RIGHT = 1;

    private final int dir;

    private float chargeVal;

    Color leftColor = new Color(200, 150, 0);
    Color rightColor = new Color(100, 0, 0);

    ParamDrawing wallParam = new ParamDrawing(null, null, WIDTH, HEIGHT);
    Blueprint wallBp = new Blueprint(wallParam);

    ParamDrawing wallChargingParam = new ParamDrawing(null, null, WIDTH, 0);
    SideDrawing wallChargingSide = new SideDrawing(SideDrawing.LEFT, SideDrawing.BOTTOM);
    Blueprint wallChargingBp = new Blueprint(wallChargingParam, wallChargingSide);

    SideDrawing wallCircuitSide = new SideDrawing(SideDrawing.LEFT, SideDrawing.CENTER);
    Color wallCircuitColor = new Color(200, 0, 0);
    Color wallCircuitEnergizeColor = new Color(250, 200, 0);
    ParamDrawing wallCircuitParam = new ParamDrawing(null, null, CIRCUIT_WIDTH, CIRCUIT_BOLD);
    Blueprint wallCircuitBpTemp = new Blueprint(wallCircuitParam, wallCircuitSide);
    List<Blueprint> wallCircuitBps = new ArrayList<>();

    SideDrawing wallCircuitCoreSide = new SideDrawing(SideDrawing.CENTER, SideDrawing.CENTER);
    ParamDrawing wallCircuitCoreParam = new ParamDrawing(null, null, CIRCUIT_CORE_RADIUS);
    Blueprint wallCircuitCoreBpTemp = new Blueprint(wallCircuitCoreParam, wallCircuitCoreSide);
    List<Blueprint> wallCircuitCoreBps = new ArrayList<>();
}
