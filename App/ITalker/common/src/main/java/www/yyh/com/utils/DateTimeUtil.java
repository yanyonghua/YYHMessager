package www.yyh.com.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 56357 on 2018/6/28
 */
public class DateTimeUtil {
    private static final SimpleDateFormat FORMAT =new SimpleDateFormat("yy--MM--dd", Locale.ENGLISH);

    /**
     * 获取一个简单的时间字符串
     * @param date
     * @return
     */
    public static String getSimpleDate(Date date){
        return FORMAT.format(date);
    }
}
