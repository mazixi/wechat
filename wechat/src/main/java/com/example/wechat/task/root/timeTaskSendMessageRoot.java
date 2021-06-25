package com.example.wechat.task.root;

import com.alibaba.fastjson.JSONObject;
import com.example.wechat.bean.HolidayCofig;
import com.example.wechat.utils.root.RootUtils;
import com.example.wechat.utils.wechat.wechatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * @Author mzx
 * @Date 2021/6/11 17:51
 * @Version 1.0
 */
@Component
public class timeTaskSendMessageRoot implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(timeTaskSendMessageRoot.class);
    @Autowired
    public HolidayCofig holidayCofig;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        timet();
    }
    private void timet() {
        ScheduledExecutorService executor =  new ScheduledThreadPoolExecutor(3,
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build());
        long oneDay = 24 * 60 * 60 * 1000;
        long initDelay  = RootUtils.getTimeMillis("09:00:00") - System.currentTimeMillis();
        long initDelayTwo  = RootUtils.getTimeMillis("12:30:00") - System.currentTimeMillis();
        long initDelayThree  = RootUtils.getTimeMillis("18:30:00") - System.currentTimeMillis();
        initDelay = initDelay >= 0 ? initDelay : oneDay + initDelay;
        initDelayTwo = initDelayTwo >= 0 ? initDelayTwo : oneDay + initDelayTwo;
        initDelayThree = initDelayThree >= 0 ? initDelayThree : oneDay + initDelayThree;
        executor.scheduleAtFixedRate(
                new EchoServer(),
                initDelay,
                oneDay,
                TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(
                new EchoServer(),
                initDelayTwo,
                oneDay,
                TimeUnit.MILLISECONDS);
        executor.scheduleAtFixedRate(
                new EchoServer(),
                initDelayThree,
                oneDay,
                TimeUnit.MILLISECONDS);
    }
    class EchoServer implements Runnable {
        @Override
        public void run() {
            try {
                DateFormat everyDayFormat = new SimpleDateFormat("HH:mm:ss");
                String nowTimes = everyDayFormat.format(new Date());
                log.info(Thread.currentThread().getName()+":" + nowTimes);
                JSONObject jsonObject = wechatUtils.sendMessageForTime(holidayCofig,nowTimes);
                Integer errcode = jsonObject.getInteger("errcode");
                String errmsg = jsonObject.getString("errmsg");
                if (!errcode.equals(0)){
                    log.info("出错了！"+errmsg);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
