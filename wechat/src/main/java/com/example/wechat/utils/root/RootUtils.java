package com.example.wechat.utils.root;

import com.alibaba.fastjson.JSONObject;
import com.example.wechat.task.root.timeTaskSendMessageRoot;
import com.example.wechat.utils.wechat.wechatUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author mzx
 * @Date 2021/6/18 17:31
 * @Version 1.0
 */
public class RootUtils {
    private static final Logger log = LoggerFactory.getLogger(RootUtils.class);
    /**
     * 获取指定时间对应的毫秒数
     * @param time "HH:mm:ss"
     * @return
     */
    public static long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
