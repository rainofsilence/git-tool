package cn.silence.utils;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/20 21:33:10
 */
public class StringUtils implements StrLenConstant {

    public static boolean isBlank(CharSequence cs) {
        if (cs == null) return true;
        int strLen = 0;
        for (int i = 0; i < cs.length(); i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
