import function.filemanage.bgm.BackGroundMusicManager;
import function.filemanage.se.SoundEffectManager;
import scenes.scene.Scene;
import sceneutil.data.GameDataElements;
import sceneutil.data.GameDataIO;
import function.logger.MessageLogger;
import scenes.scene.SceneManager;

import java.awt.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

class Main {

    // メインでやる事
    //      1. ゲーム内で運用するデータの初期化
    //      2. ウインドウを立ち上げて最初の場面を表示
    // 以上
    public static void main(String[] args){
        Main main = new Main(); // ログ出力用
        MessageLogger.printMessage(main, "データの初期化中 ", 0);

        // dataの初期化
        GameDataIO dataIO = gameDataInit(args);

        // ウインドウを立ち上げ、最初の場面を表示する
        SceneManager sceneManager = dataIO.getSceneManager();
        sceneManager.activateDisplay(dataIO);

        MessageLogger.printMessage(main, "データの初期化完了 ", 0);
    }

    // ゲーム内で運用するデータの初期化
    private static GameDataIO gameDataInit(String[] args) {
        // インスタンスを取得
        // この書き方ならインスタンス取得失敗したときにぬるぽを吐いてくれるらしい
        GameDataIO dataIO = Objects.requireNonNull(GameDataIO.getSingleton() );

        // シーン切り替え時に稼働するインスタンスの場所を埋め込んでおく
        SceneManager sceneManager = Objects.requireNonNull(SceneManager.getSingleton() );
        dataIO.put(GameDataElements.SCENE_MANAGER, sceneManager);
        dataIO.put(GameDataElements.SCENE, Scene.GAME);

        // ウインドウの設定
        dataIO.put(GameDataElements.WINDOW_NAME,   "落ちもの回路パズル");
        dataIO.put(GameDataElements.WINDOW_SIZE,   new Dimension(800, 600) );
        dataIO.put(GameDataElements.WINDOW_POINT,  new Point(600, 300) );

        // JARファイルのディレクトリを取得（文字型）
        Path exePath;
        try {
            exePath = Paths.get(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI() );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String exeDir = exePath.getParent().toString();
        String assetsDir = "assets\\";

        // 使用ディレクトリ
        dataIO.put(GameDataElements.ROOT_DIRECTORY, exeDir);
        dataIO.put(GameDataElements.DIR_BGM,  assetsDir + "bgm");
        dataIO.put(GameDataElements.DIR_SE,   assetsDir + "se");

        // 使用ファイル
        dataIO.put(GameDataElements.FILE_SE_MOVE_BLOCK,   "move_block01.wav");
        dataIO.put(GameDataElements.FILE_SE_DELETE_BLOCK, "delete_block01.wav");
        dataIO.put(GameDataElements.FILE_BGM_GAME_START,  "game_start_jingle.wav");
        dataIO.put(GameDataElements.FILE_BGM_GAME_OVER,   "game_over_jingle.wav");
        dataIO.put(GameDataElements.FILE_BGM_FAILED,      "failed01.wav");
        dataIO.put(GameDataElements.FILE_BGM_PAUSE,       "pause_jingle02.wav");
        dataIO.put(GameDataElements.FILE_BGM_BGM01,       "hibikukaze2.wav");

        // SEマネージャ
        String seMoveBlock   = dataIO.get(GameDataElements.FILE_SE_MOVE_BLOCK, String.class);
        String seDeleteBlock = dataIO.get(GameDataElements.FILE_SE_DELETE_BLOCK, String.class);
        String dirPathSoundEffect = dataIO.getDirectoryPathStr(GameDataElements.DIR_SE);
        String[] seFileNames = {seMoveBlock, seDeleteBlock};
        SoundEffectManager seManager
                = Objects.requireNonNull(SoundEffectManager.getInstance(dirPathSoundEffect, seFileNames));
        dataIO.put(GameDataElements.SE_MANAGER, seManager);

        // BGMマネージャ
        String bgmGameStart = dataIO.get(GameDataElements.FILE_BGM_GAME_START, String.class);
        String bgmGameOver  = dataIO.get(GameDataElements.FILE_BGM_GAME_OVER, String.class);
        String bgmFailed    = dataIO.get(GameDataElements.FILE_BGM_FAILED, String.class);
        String bgmPause     = dataIO.get(GameDataElements.FILE_BGM_PAUSE, String.class);
        String bgm01        = dataIO.get(GameDataElements.FILE_BGM_BGM01, String.class);
        String dirPathBackGroundMusic = dataIO.getDirectoryPathStr(GameDataElements.DIR_BGM);
        String[] bgmFileNames = {bgmGameStart, bgmGameOver, bgmFailed, bgmPause, bgm01};
        BackGroundMusicManager bgmManager
                = Objects.requireNonNull(BackGroundMusicManager.getInstance(dirPathBackGroundMusic, bgmFileNames));
        dataIO.put(GameDataElements.BGM_MANAGER, bgmManager);

        dataIO.put(GameDataElements.SCENE_ID, 1);

        dataIO.put(GameDataElements.FRAME_RATE, 50); // フレームレート
        dataIO.put(GameDataElements.MASTER_VOLUME, 0.5F); //主音量

        dataIO.put(GameDataElements.CHEAT, true); // デバッグ用チート

        dataIO.put(GameDataElements.HI_SCORE, 0); // ハイスコア

        return dataIO;
    }
}

/*
 * GameScene           794
 * GameDrawer          270
 * *
 * GameObject          137
 * GameObjectAttribute   7
 * Block               240
 * ElectricWall        129
 * NormalWall           35
 * VirtualController   141
 *
 * ----------------
 * sum                1753
 *
 *
 * SoundEffectManager     126
 * BackGroundMusicManager 205
 *
 * ----------------
 * sum                    331
 */