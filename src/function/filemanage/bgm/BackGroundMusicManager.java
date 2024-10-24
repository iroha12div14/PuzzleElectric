package function.filemanage.bgm;

import function.logger.MessageLogger;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

/** 背景音楽の再生に用いるシングルトンクラス **/
public class BackGroundMusicManager {
    private static BackGroundMusicManager INSTANCE;

    private Clip[] clips;
    private final String dirPath; // ディレクトリの絶対パス
    private final String[] fileNames; // 拡張子を含まないファイル名の一覧

    private String playedBGMFile = "";  // 再生中のファイル名(stopに用いる情報の保持)

    private float masterVolume = 1.0F;
    private static final float previewVolume = 0.6F; // プレビューの再生音量
    private boolean isMute = false;

    /**
     * コンストラクタ. 読み込み先のディレクトリ名と使うファイル名をここで格納
     *
     * @param dirPath   読み込み先ディレクトリの絶対パス（文字型）
     * @param fileNames ファイル名の一覧（文字列配列）
     */
    private BackGroundMusicManager(String dirPath, String[] fileNames) {
        this.dirPath = dirPath;
        this.fileNames = new String[fileNames.length];
        for (int f = 0; f < fileNames.length; f++) {
            // 単純な置き換えだけどファイル名のド真ん中に".txt"が含まれてる音声ファイルは多分無いでしょう 多分
            this.fileNames[f] = fileNames[f].replace(".txt", "");
        }
    }

    /**
     * インスタンスの呼び出し. 読み込み先のディレクトリ名と使うファイル名をここで格納
     *
     * @param dirPath   読み込み先ディレクトリの絶対パス（文字型）
     * @param fileNames ファイル名の一覧（文字列配列）
     */
    public static BackGroundMusicManager getInstance(String dirPath, String[] fileNames) {
        if (INSTANCE == null) {
            INSTANCE = new BackGroundMusicManager(dirPath, fileNames);
            return INSTANCE;
        } else return null;
    }

    /**
     * ファイルの読み込み
     **/
    public void loadWaveFile() {
        clips = new Clip[fileNames.length];
        int fileCount = fileNames.length;

        for (int f = 0; f < fileCount; f++) {
            String address = dirPath + "\\" + fileNames[f];
            File file = new File(address);
            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(file);
                AudioFormat format = stream.getFormat();

                DataLine.Info info = new DataLine.Info(Clip.class, format);
                clips[f] = (Clip) AudioSystem.getLine(info);
                clips[f].open(stream);
            } catch (FileNotFoundException e) {
                // プレビューファイルが無い場合はここを通る
                // clip[f]はnullになるのでぬるぽが出ないよう各メソッド側で弾いておく
                MessageLogger.printMessage(this, "<Error> " + address + "が見つかりません。");
            } catch ( // エラーをまとめてポイ
                    UnsupportedAudioFileException |
                    LineUnavailableException |
                    IOException e
            ) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * BGMの再生
     *
     * @param s 音源名（文字列）
     */
    public void startSound(String s) {
        stopSound();
        int i = findFileName(s);
        if (i != -1) {
            startSound(i);
        }
    }

    private void startSound(int i) {
        if (clips[i] != null) {
            setVolume(clips[i]);
            clips[i].start();
        }
    }

    /**
     * BGMの停止
     **/
    public void stopSound() {
        int i = findFileName(playedBGMFile);
        if (i != -1) {
            stopSound(i);
        }
    }
    private void stopSound(int i) {
        if (clips[i] != null) {
            clips[i].stop();
            clips[i].flush();
            clips[i].setFramePosition(0);
        }
    }

    /** BGMの一時停止 **/
    public int pauseSound() {
        int i = findFileName(playedBGMFile);
        if (i != -1) {
            clips[i].stop();
            return clips[i].getFramePosition();
        }
        else return -1;
    }
    /** BGMの再スタート **/
    public void restartSound(String bgmFile, int position) {
        stopSound();
        playedBGMFile = bgmFile;
        int i = findFileName(playedBGMFile);
        if (i != -1) {
            clips[i].setFramePosition(position);
            clips[i].start();
        }
    }

    // ファイル名探索(start, stopどちらでも使う)
    private int findFileName(String scoreTextName) {
        String fileName = scoreTextName.replace(".txt", "");
        playedBGMFile = fileName;
        for (int f = 0; f < fileNames.length; f++) {
            if (Objects.equals(fileNames[f], fileName)) {
                return f;
            }
        }
        return -1;
    }

    /** 再生位置の取得 **/
    public int getPosition() {
        int i = findFileName(playedBGMFile);
        if (i != -1) {
            return clips[i].getFramePosition();
        } else return -1;
    }
    /** 再生位置の指定（ループ用） **/
    public void setPosition(String bgmName, int position) {
        if(Objects.equals(playedBGMFile, bgmName)) {
            int i = findFileName(playedBGMFile);
            if(i != -1) {
                clips[i].setFramePosition(position);
            }
        }
    }

    // 音量の調整
    private void setVolume(Clip c) {
        float volume = masterVolume * previewVolume * (isMute ? 0 : 1);
        FloatControl volumeControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
        float maxVolume = volumeControl.getMaximum();
        volumeControl.setValue(Math.min( (float) Math.log10(volume) * 20, maxVolume) ); // 音量制限
    }

    /**
     * 主音量を調整する
     * @param v ボリューム（浮動点小数）
     */
    public void setMasterVolume(float v) {
        masterVolume = v;
    }

    /** ミュートする **/
    public void setMute(boolean b) {
        isMute = b;
        int i = findFileName(playedBGMFile);
        setVolume(clips[i]);
    }

    /**
     * クリップをクローズする
     */
    public void closeClips() {
        for(Clip clip : clips) {
            if(clip != null) {
                clip.stop();
                clip.flush();
                clip.close();
            }
        }
    }
}
