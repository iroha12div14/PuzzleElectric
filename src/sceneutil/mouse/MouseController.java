package sceneutil.mouse;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** マウスボタンの一覧をアサインし、マウスの状態を取得するクラス **/
public class MouseController implements MouseListener, MouseMotionListener {
    /**
     * マウスボタンアサインの一覧を貰って初期化
     * @param mouseAssign "MouseEvent."系のボタン一覧を格納したリストを引数として初期化する
     */
    public MouseController(List<Integer> mouseAssign) {
        this.mouseAssign = mouseAssign;
        for(int mouseButton : mouseAssign) {
            mouseClick.put(mouseButton, false);
        }
        mousePoint = new Point(-1, -1);
        resetDragPoint();
    }

    // クリック
    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        for(int mouseButton : mouseAssign) {
            if(mouseEvent.getButton() == mouseButton) {
                mouseClick.put(mouseButton, true);
            }
        }
    }
    // ボタンを離す
    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        resetDragPoint();
    }
    // マウス移動
    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        mousePoint = mouseEvent.getPoint();
        moveState = MOVE;
    }
    // ドラッグ
    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        mousePoint = mouseEvent.getPoint();
        moveState = DRAG;
        if(dragStartPoint.x == -1 && dragStartPoint.y == -1) {
            dragStartPoint = mousePoint;
        }
    }
    // actionPerformメソッドで稼働させて多重反応しないようにする
    public void avoidChattering() {
        for(int mouseButton : mouseAssign){
            mouseClick.put(mouseButton, false);
        }
        moveState = MOTIONLESS;
    }

    // マウスの状態から色々取得
    /** 座標を取得する **/
    public Point getMousePoint() {
        return mousePoint;
    }
    /** あるボタンの押下の有無を取得する
     * @param mouseButton マウスボタンの種類（"MouseEvent."系のint型）
     */
    public boolean getMouseClick(int mouseButton) {
        return mouseClick.get(mouseButton);
    }
    /** マウスの静止/移動/ドラッグの状態を調べる
     * @return 0:MOTIONLESS、1:MOVE、2:DRAG
     */
    public int getMouseMove() {
        return moveState;
    }
    /** ドラッグを開始した座標を得る **/
    public Point getDragStartPoint() {
        return dragStartPoint;
    }

    /**
     * マウスリスナを登録するためのメソッド
     * @param panel マウスリスナを登録したいコンポーネント(JPanel)
     */
    public void setMouseListener(JPanel panel) {
        panel.setFocusable(true);
        panel.addMouseListener(this);
        panel.addMouseMotionListener(this);
    }

    private void resetDragPoint() {
        dragStartPoint = new Point(-1, -1);
    }

    // ----------------------------------------------------- //

    private final List<Integer> mouseAssign;
    private final Map<Integer, Boolean> mouseClick = new HashMap<>();

    public static final int MOTIONLESS = 0;
    public static final int MOVE = 1;
    public static final int DRAG = 2;

    private Point mousePoint;
    private Point dragStartPoint;
    private int moveState; // 動いていない、動いている、ドラッグしている

    // ----------------------------------------------------- //

    // クリックのみ使用するので使わない
    @Override
    public void mousePressed(MouseEvent mouseEvent) {}
    @Override
    public void mouseEntered(MouseEvent mouseEvent) {}
    @Override
    public void mouseExited(MouseEvent mouseEvent) {}
}
