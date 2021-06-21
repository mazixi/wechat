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
    public static void rootTime(String time) {
        ScheduledExecutorService executor =  new ScheduledThreadPoolExecutor(3,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
        long oneDay = 24 * 60 * 60 * 1000;
//        long oneDay =  20 * 1000;

//        long initDelay  = 0;
        long initDelay  = RootUtils.getTimeMillis(time) - System.currentTimeMillis();
        initDelay = initDelay >= 0 ? initDelay : oneDay + initDelay;
        executor.scheduleAtFixedRate(
                new EchoServer(),
                initDelay,
                oneDay,
                TimeUnit.MILLISECONDS);
    }
    /**
     * 获取指定时间对应的毫秒数
     * @param time "HH:mm:ss"
     * @return
     */
    public static long getTimeMillis(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    static class EchoServer implements Runnable {
        @Override
        public void run() {
            try {
                //每天六点半发消息
                JSONObject jsonObject = wechatUtils.sendMessageForTime();
                Integer errcode = jsonObject.getInteger("errcode");
                String invaliduser = jsonObject.getString("invaliduser");
                if (!errcode.equals(0)){
                    log.info("出错了！"+invaliduser);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
