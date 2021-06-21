package com.example.wechat.bean;

import java.util.Date;

/**
 * @Author mzx
 * @Date 2021/6/15 17:39
 * @Version 1.0
 */
public class Holiday {
    /**
     * 节假日期
     */
    private Date holidayDate;
    /**
     * 备注
     */
    private String desc;
    /**
     * 状态 1节日2节日前后调整的法定工作日
     */
    private Integer status;

    public Date getHolidayDate() {
        return holidayDate;
    }

    public void setHolidayDate(Date holidayDate) {
        this.holidayDate = holidayDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Holiday{" +
                "holidayDate=" + holidayDate +
                ", desc='" + desc + '\'' +
                ", status=" + status +
                '}';
    }
}
