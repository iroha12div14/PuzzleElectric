package sceneutil.data;

/** ゲーム内でやり取りされるデータの要素 **/
public enum GameDataElements {
    // 使用ディレクトリ名
    ROOT_DIRECTORY,     // String
    DIR_BGM,            // String
    DIR_SE,             // String

    // 使用ファイル名
    FILE_SE_MOVE_BLOCK,     // String
    FILE_SE_DELETE_BLOCK,   // String
    FILE_BGM_GAME_START,    // String
    FILE_BGM_GAME_OVER,     // String
    FILE_BGM_FAILED,        // String
    FILE_BGM_PAUSE,         // String
    FILE_BGM_BGM01,         // String

    // 保存するデータ
    FRAME_RATE,         // int
    MASTER_VOLUME,      // float

    // 場面の管理
    SCENE_MANAGER,      // SceneManager
    SCENE,              // Scene
    SCENE_ID,           // int

    // ウインドウの名前やサイズ
    WINDOW_NAME,        // String
    WINDOW_POINT,       // java.awt.Point
    WINDOW_SIZE,        // java.awt.Dimension

    // 音声の管理
    SE_MANAGER,         // SoundEffectManager
    BGM_MANAGER,        // BackGroundMusicManager

    CHEAT,              // boolean

    // ゲーム内情報
    HI_SCORE,           // int
}
