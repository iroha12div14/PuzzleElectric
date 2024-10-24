package scenes.scene.game.gameobject;

import sceneutil.draw.blueprint.Blueprint;
import sceneutil.font.FontBox;

import java.awt.*;
import java.util.List;

abstract public class GameObject {
    // 種別
    private final GameObjectAttribute attribute;
    // 生存の有無
    private boolean isAlive;
    // 座標
    private float x;
    private float y;
    // 移動量と移動方向
    private float dir;
    private float mov;
    // 物体の当たり判定の全身
    private float width;
    private float height;

    /**
     * コン
     * @param colWidth   当たり判定の全身（横）
     * @param colHeight  当たり判定の全身（縦）
     * @param attribute  属性（ポインタ、カード）
      */
    protected GameObject(
            float x, float y,
            float colWidth, float colHeight,
            GameObjectAttribute attribute
    ) {
        this.isAlive = true;
        this.x = x;
        this.y = y;
        this.width = colWidth;
        this.height = colHeight;
        this.attribute = attribute;

        mov = 0.0F;
        dir = 0.0F;
    }

    /**
     * 重複判定
     * @param obj 重複判定対象
     */
    public boolean isCollision(GameObject obj) {
        // 重複判定を取らない条件の一覧（複数必要な場合は書き足す）
        // 1. どちらかのオブジェクトが既に死亡判定されている
        boolean notCollision1 = !this.isAlive || !obj.isAlive;
        if(notCollision1) {
            return false;
        }

        // X,Y座標上の重複判定
        return isCollision(obj.getPoint(), obj.getSize() );
    }

    /** 重複判定（座標と領域を用いて）**/
    public boolean isCollision(Point point, Dimension dimension) {
        boolean collisionX
                =  this.x + this.width  / 2 > point.x - (float) dimension.width  / 2
                && this.x - this.width  / 2 < point.x + (float) dimension.width  / 2;
        boolean collisionY
                =  this.y + this.height / 2 > point.y - (float) dimension.height / 2
                && this.y - this.height / 2 < point.y + (float) dimension.height / 2;

        return collisionX && collisionY;
    }

    // ----------------------------------------------------------- //
    // 下駄雪駄

    // 抽象メソッド：設計図出力
    abstract public List<Blueprint> getBlueprint();
    abstract public List<FontBox> getFontBox(Graphics2D g2d);

    // 破壊
    public void dead() {
        isAlive = false;
    }

    public boolean isAlive() {
        return isAlive;
    }
    public GameObjectAttribute getAttribute() {
        return attribute;
    }
    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getWidth() {
        return width;
    }
    public float getHeight() {
        return height;
    }
    public float getDir() {
        return dir;
    }
    public float getMov() {
        return mov;
    }
    public Point getPoint() {
        return new Point( (int) x, (int) y);
    }
    public Dimension getSize() {
        return new Dimension( (int) width, (int) height);
    }

    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }
    public void setDir(float dir) {
        this.dir = dir;
    }
    public void setMov(float mov) {
        this.mov = mov;
    }
    public void setPoint(Point point) {
        x = point.x;
        y = point.y;
    }
    public void setSize(Dimension size) {
        width = size.width;
        height = size.height;
    }
}
