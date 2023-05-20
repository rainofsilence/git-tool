package cn.silence.utils;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/20 21:31:28
 */
public final class Assert {

    private Assert() {
    }

    public static void notBlank(String str, String msg) {
        if (StringUtils.isBlank(str)) throw new RuntimeException(msg);
    }

}
