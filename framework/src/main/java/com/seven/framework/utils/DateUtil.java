package com.seven.framework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by wangbin on 2018/3/20.
 * 日期工具类
 */

public class DateUtil {

    /**
     * 获得当前的年份
     *
     * @return
     */
    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获得当前的月份
     *
     * @return
     */
    public static int getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获得当前的日期天
     *
     * @return
     */
    public static int getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DATE);
    }

    /**
     * 获取指定格式的当前时间
     *
     * @param timeFormat 时间格式
     * @return
     */
    public static String getFormatCurrentTime(String timeFormat) {
        return new SimpleDateFormat(timeFormat, Locale.CHINA).format(new Date(System.currentTimeMillis()));
    }

    /**
     * 获取常用格式的时间字符串
     * @param millis
     * @return
     */
    public static String formLocalTime(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String format = simpleDateFormat.format(date);
        return format;
    }

    /**
     * 获取指定格式的时间字符串
     * @param pattern
     * @param millis
     * @return
     */
    public static String formLocalTime(String pattern, long millis) {
        Date date = new Date(millis);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        String format = simpleDateFormat.format(date);
        return format;

    }

    /**
     * 获取指定格式的时间字符串
     * @param pattern
     * @param date
     * @return
     */
    public static String formLocalTime(String pattern, Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        String format = simpleDateFormat.format(date);
        return format;

    }

    /**
     * 获取Date对象
     *
     * @param dateString   时间字符串
     * @param formatString 时间格式
     * @return
     * @throws Exception
     */
    public static Date getDateFromDateString(String dateString, String formatString) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatString, Locale.CHINA);
        return simpleDateFormat.parse(dateString);
    }

    /**
     * 通过给定的年、月、周获得该周内的每一天日期
     *
     * @param year  int 年
     * @param month int 月
     * @param week  int 周
     * @return List<Date> 七天的日期
     */
    public static List<Date> getDayByWeek(int year, int month, int week) {
        List<Date> list = new ArrayList<Date>();
        // 先滚动到该年.
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        // 滚动到月:
        c.set(Calendar.MONTH, month - 1);
        // 滚动到周:
        c.set(Calendar.WEEK_OF_MONTH, week);
        // 得到该周第一天:
        for (int i = 0; i < 6; i++) {
            c.set(Calendar.DAY_OF_WEEK, i + 2);
            list.add(c.getTime());
        }
        // 最后一天:
        c.set(Calendar.WEEK_OF_MONTH, week + 1);
        c.set(Calendar.DAY_OF_WEEK, 1);
        list.add(c.getTime());
        return list;
    }

    /**
     * 获得当前日期是本月的第几周
     *
     * @return int
     */
    public static int getCurWeekNoOfMonth() {
        Date date = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 获得当前日期是星期几
     *
     * @return int
     */
    public static int getCurWeekNo(String dat) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date date = null;
        try {
            date = format.parse(dat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }


    /**
     * 判断时间是否属于当天
     *
     * @param time
     * @return
     */
    public static boolean judgeTimeIsCurrentDate(long time) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(time);
        return instance.get(Calendar.DATE) == getCurrentDay();
    }

    /**
     * 根据日期获取生肖
     *
     * @return
     */
    public static String date2Zodica(Calendar time) {
        String[] zodiacArr = {"猴", "鸡", "狗", "猪", "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊"};
        return zodiacArr[time.get(Calendar.YEAR) % 12];
    }


    /**
     * 根据日期获取星座
     *
     * @param time
     * @return
     */
    public static String date2Constellation(Calendar time) {
        String[] constellationArr = {"水瓶座", "双鱼座", "牡羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座",
                "天蝎座", "射手座", "魔羯座"};
        int[] constellationEdgeDay = {20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};
        int month = time.get(Calendar.MONTH);
        int day = time.get(Calendar.DAY_OF_MONTH);
        if (day < constellationEdgeDay[month]) {
            month = month - 1;
        }
        if (month >= 0) {
            return constellationArr[month];
        }
        return constellationArr[11];
    }

    /**
     * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
     *
     * @param time 传进来的时间毫秒数 使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time为null，或者时间格式不匹配，输出空字符""
     */
    public static String formatDisplayTime(Long time) {
        String display = ""; // 要显示的文字
        int tMin = 60 * 1000; // 1分钟的毫秒数
        int tHour = 60 * tMin; // 一个小时的毫秒数
        int tDay = 24 * tHour; // 一天的毫秒数
        if (time != 0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA); // 最终显示的格式类型
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm", Locale.CHINA);
                Date tDate = new Date(time); // 传进来的时间的Date
                long tMill = tDate.getTime(); //传进来的时间的 毫秒
                Date today = new Date(); // 当前时间的Date
                SimpleDateFormat sdfToday = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);//根据当前时间的Date得到今日0点的毫秒数
                long todayMill = sdfToday.parse(sdfToday.format(today)).getTime();
                if (tMill - todayMill >= 0) {
                    //今天
                    display = "今天" + sdf1.format(tDate);
                } else if ((tMill - todayMill < 0) && ((tMill - todayMill) >= (todayMill - tDay))) {
                    //昨天
                    display = "昨天" + sdf1.format(tDate);
                } else {
                    //其他
                    display = sdf.format(tDate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return display;
    }
}
