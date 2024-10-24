package function.logger;

/** デバッグ用メッセージをコンソールに出力する。 **/
public class MessageLogger {
    /**
     * クラスを明示しつつログを出力する
     * @param obj メッセージ元のクラス（thisでいい）
     * @param msg 表示メッセージ
     * @param tab インデント
     */
    public static void printMessage(Object obj, String msg, int tab) {
        System.out.println(msg + "\t".repeat(tab) + "@" + getClassName(obj) );
    }

    /**
     * クラスを明示しつつログを出力する（インデント無し）
     * @param obj メッセージ元のクラス（thisでいい）
     * @param msg 表示メッセージ
     */
    public static void printMessage(Object obj, String msg) {
        System.out.println(msg + " @" + getClassName(obj) );
    }

    // アドレスからファイル名を抽出
    public static String getFileNameFromAddress(String fileAddress) {
        if(fileAddress.contains("\\") ) {
            return getLast(fileAddress.split("\\\\") );
        } else {
            return  fileAddress;
        }
    }

    // 配列の最後の要素を取得
    private static <T> T getLast(T[] obj) {
        return obj[obj.length - 1];
    }

    // クラス名のみを抽出
    private static String getClassName(Object obj) {
        String className = obj.getClass().getName();
        String[] classNameArr = className.split("\\.");
        return getLast(classNameArr);
    }
}
