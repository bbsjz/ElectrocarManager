package com.carmanager.server.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 提供日期按照某种格式转String的方法
 */
public class DateUtils {

    /**
     *  加减对应时间后的日期
     * @param date  需要加减时间的日期
     * @param amount    加减的时间(毫秒)
     * @return  加减对应时间后的日期
     */
    public static Date subtractTime(Date date, int amount) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strTime = sdf.format(date.getTime() + amount);
            Date time = sdf.parse(strTime);
            return time;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前时间的月日信息
     * @param date
     * @return
     */
    public static String toYearAndMonthAndDate(Date date)
    {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
        return sdf.format(date);
    }

    /**
     * 获取当前时间的时分信息
     * @param date
     * @return
     */
    public static String toHourAndMinute(Date date)
    {
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }
}
