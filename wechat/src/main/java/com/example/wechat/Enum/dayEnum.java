package com.example.wechat.Enum;

/**
 * @Author mzx
 * @Date 2021/6/15 20:37
 * @Version 1.0
 */
public enum dayEnum {
    sat("星期六"),
    week("星期日");
    /**
     * 天
     */
    private String day;

    dayEnum(String s) {
        this.day=s;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    /**
     * 获取是否是周末
     * @param day
     * @return
     */
    public static String getDayEnum(String day) {
        for (dayEnum s: dayEnum.values()) {
            if (s.day.equals(day)){
                return day;
            }
        }
        return null;
    }
}
