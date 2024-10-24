package sceneutil;

import java.awt.*;

/** 場面の描画内容を設定するクラス **/
public abstract class SceneDrawer {
    // 画面サイズ
    protected Dimension windowSize;
    protected int windowWidthHalf;
    protected int windowHeightHalf;

    /**
     * 画面サイズの設定
     * @param windowSize 画面サイズ（Dimensionオブジェクト）
     */
    public void setWindowSize(Dimension windowSize) {
        this.windowSize  = windowSize;
        windowWidthHalf  = windowSize.width  / 2;
        windowHeightHalf = windowSize.height / 2;
    };

    /** 設計図を設定する **/
    protected abstract void setBlueprint();

    /**
     * アニメーションタイマーを設定する
     * @param frameRate フレームレート
     */
    protected abstract void setAnimationTimer(int frameRate);

    /** 経過させるアニメーションタイマーのインスタンスをここにまとめて書く。**/
    protected abstract void pastAnimationTimer();
}
