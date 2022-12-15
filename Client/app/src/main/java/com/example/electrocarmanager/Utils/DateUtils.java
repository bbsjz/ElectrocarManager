package com.example.electrocarmanager.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    /**
     * 秒转时分秒
     * @param duration
     * @return
     */
    public static String convertMillis (long duration) {
        long hour = duration/ 3600;
        long minute = (duration % 3600) / 60;
        long second = (duration % 3600) % 60;

        String hourStr = hour == 0 ? "00" : hour >= 10 ? hour + "" : "0" + hour;
        String minuteStr = minute == 0 ? "00" : minute >= 10 ? minute + "" : "0" + minute;
        String secondStr = second == 0 ? "00" : second >= 10 ? second + "" : "0" + second;

        return hourStr + ":" + minuteStr + ":" + secondStr;
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
