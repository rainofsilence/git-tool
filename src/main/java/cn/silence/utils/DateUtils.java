package cn.silence.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2023/05/20 21:14:09
 */
public class DateUtils implements DateConstant {

    public static String formatDatetime() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATETIME_FMT);
        return sdf.format(new Date());
    }

    public static String formatDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FMT);
        return sdf.format(new Date());
    }
}
