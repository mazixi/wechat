package com.example.wechat.utils.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Author mzx
 * @Date 2021/6/15 14:53
 * @Version 1.0
 */
public class DateUtil {

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
    private static final DateFormat df3 = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
    private static final DateFormat df4 = new SimpleDateFormat("yyyy-M");
    private static final DateFormat df5 = new SimpleDateFormat("yyyy-M-d");

    private static final Calendar calendar = Calendar.getInstance();

    /**
     * 返回当前时间
     *
     * @return
     */
    public static String getTime() {
        Date date = new Date();
        String format = df.format(date);
        return format;
    }

    /**
     * 获取两个时间差值 几天几时几分几秒
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String dateDifference(String startTime, String endTime) {
        String str = "";
        try {
            Date start = df.parse(startTime);
            Date end = df.parse(endTime);
            //除以1000是为了转换成秒
            long between = (end.getTime() - start.getTime()) / 1000;
            long day = between / (24 * 3600);
            long hour = between % (24 * 3600) / 3600;
            long minute = between % 3600 / 60;
            long second = between % 60;
            System.out.println(between % 60);
            str = day + "天" + hour + "小时" + minute + "分" + second + "秒";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 加天数
     *
     * @param date
     * @param days
     * @return
     */
    public static String addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return df2.format(cal.getTime());
    }


    /**
     * 转换时间为年月日
     *
     * @param date
     * @return
     */
    public static String formatDay(Date date) {
        return df2.format(date);
    }

    /**
     * 获取两个时间差值 几天几时几分几秒
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static String dateDifferenceByDf2(String startTime, String endTime) {
        String str = "";
        try {
            Date start = df2.parse(startTime);
            Date end = df2.parse(endTime);
            //除以1000是为了转换成秒
            long between = (end.getTime() - start.getTime()) / 1000;
            long day = between / (24 * 3600);
            long hour = between % (24 * 3600) / 3600;
            long minute = between % 3600 / 60;
            long second = between % 60;
            System.out.println(between % 60);
            str = day + "天" + hour + "小时" + minute + "分" + second + "秒";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获取两个时间差值 几天
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long dateLagByDay(String startTime, String endTime) {
        String str = "";
        try {
            Date start = df2.parse(startTime);
            Date end = df2.parse(endTime);
            //除以1000是为了转换成秒
            long between = (end.getTime() - start.getTime()) / 1000;
            long day = between / (24 * 3600);
            return day;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前年月
     *
     * @return
     */
    public static String getny() {
        Date date = new Date();
        String format = df4.format(date);
        return format;
    }


    /**
     * 比较时分 比较两个值得大小
     *
     * @param nowdate     时间
     * @param comparedate 比较时间 例如7:30
     * @return
     */
    public static boolean hourMinuteCompare(String nowdate, String comparedate) {
        //确保校验格式
        String[] a = nowdate.split(":");
        String[] b = comparedate.split(":");
        //当前时间时分
        int nowdateHour = Integer.parseInt(a[0]);
        int nowdateMinute = Integer.parseInt(a[1]);
        //比较时间时分
        int comparedateHour = Integer.parseInt(b[0]);
        int comparedateMinute = Integer.parseInt(b[1]);
        if (nowdateHour < comparedateHour) {
            return true;
        } else if (nowdateHour == comparedateHour) {
            return nowdateMinute <= comparedateMinute;
        }
        return false;
    }

    /**
     * 日期转换为年月日
     *
     * @param startTime
     * @return
     */
    public static String formatDf(Date startTime) {
        String str = "";
        try {
            str = df3.format(startTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 查询当天是否为工作日
     *
     * @param startTime
     * @return
     */
    public static int ifDateWeekend(String startTime) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(df2.parse(startTime));
            // 0周末 6 周六
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            return dayOfWeek;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     *
     * 查询给定时间范围内的每一天的日期(yyyy-MM-dd HH:mm:ss")
     *
     * @param startDateStr 开始时间 (yyyy-MM-dd HH:mm:ss")
     * @param endDateStr   结束时间 (yyyy-MM-dd HH:mm:ss")
     * @return List<String>
     */
    public static List<String> handleRangeDate(String startDateStr, String endDateStr) {
        List<String> listDate = new ArrayList<>();
        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter df2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDateTime startDate = LocalDateTime.parse(startDateStr, df1);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr, df1);
            LocalDateTime tempDate = null;
            while (!(LocalDateTime.of(startDate.plusDays(-1).toLocalDate(), LocalTime.MIN)
                    .equals(LocalDateTime.of(endDate.toLocalDate(), LocalTime.MIN)))) {
                tempDate = startDate;
                String format = tempDate.format(df2);
                listDate.add(format);
                startDate = startDate.plusDays(1);
            }
            System.out.println(listDate.toString());
            return listDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转日期
     *
     * @param date
     * @return
     */
    public static Date parseDate(String date) {
        try {
            Date parse = df5.parse(date);
            return parse;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 返回当前时间之前N周 本周 之后N周 的周一周日准确时间
     *
     * @param date 当前时间
     * @param week 获取上几周就传负数几 下几周就正数＋1  举例上周 传 -1 本周传 0 下周传 1
     * @return
     */
    public static Map<String, String> reMonSun(String date, int week) {
        Map<String, String> map = new HashMap();

        try {
            calendar.setTime(df2.parse(date));
            //设置一周第一天是星期一  国外的一周第一天是星期天 默认是按照国外算得
            calendar.setFirstDayOfWeek(Calendar.MONDAY);

            calendar.add(Calendar.WEEK_OF_MONTH, week);

            //查询当前星期几 在本周中 从周日开始算 星期天就是 1 星期一就是2 以此类推
            int nowTime = calendar.get(Calendar.DAY_OF_WEEK);
            //是1的时候就是星期天 给他赋值8 可以在下边得出负数 用于消减时间 其他状况不做处理
            if (nowTime == 1) {
                nowTime = 8;
            }
            calendar.add(Calendar.DATE, calendar.getFirstDayOfWeek() - nowTime);
            Date monday = calendar.getTime();
            String weekBegin = df.format(monday);
            // 此时日期已经是星期一 给当前日期加上6天必然是周末
            calendar.add(Calendar.DATE, 6);
            Date sundayDate = calendar.getTime();
            String weekEnd = df.format(sundayDate);
            weekEnd = weekEnd.replace("00:00:00", "23:59:59");
            map.put("mondayDate", weekBegin);
            map.put("sundayDate", weekEnd);
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回当前时间之前N月 本月 之后N月第一天和最后一天
     *
     * @param date  当前时间
     * @param month 获取上几月就传负数几 下几月就正数＋1  举例上月 传 -1 本月传 0 下月传 1
     * @return
     */
    public static Map<String, String> reOneLast(String date, int month) {

        Map<String, String> map = new HashMap();
        try {
            calendar.setTime(df2.parse(date));
            //当前月的上个月
            calendar.add(Calendar.MONTH, month);
            //设置当前月的最大天数
            calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
            Date lastDateOfMonth = calendar.getTime();
            String lastDay = df2.format(lastDateOfMonth);
            //设置一 即为每月第一天
            calendar.set(Calendar.DATE, 1);
            Date oneDayofMonth = calendar.getTime();
            String oneDay = df2.format(oneDayofMonth);
            map.put("oneDay", oneDay);
            map.put("lastDay", lastDay);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    /**
     * 返回当月最大天数
     *
     * @param date 当前时间
     * @return
     */
    public static int reMonthMaxDay(String date) {
        try {
            calendar.setTime(df2.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int actualMaximum = calendar.getActualMaximum(Calendar.DATE);
        return actualMaximum;
    }

    public static void main(String[] args) {
        String date = "2020-11-05 12:11:04";
        try {
            calendar.setTime(df2.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<String> strings = handleRangeDate("2021-05-09 17:39:09", "2021-05-17 17:39:09");
        System.out.println(strings);


    }

}
