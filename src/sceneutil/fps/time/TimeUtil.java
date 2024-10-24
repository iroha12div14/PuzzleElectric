package sceneutil.fps.time;

/** 時刻の取得を行うインタフェース **/
public interface TimeUtil {
    int getTime();

    long time();
    int time(Unit unit);

    enum Unit {
        NANO,
        MICRO,
        MILLI,
        SECOND,
        MINUTE,
        HOUR
    }
}
