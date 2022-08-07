package com.example.wechat.jobhandler;

import com.webHook.entity.WebHookMessage;
import com.webHook.service.MessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * XXL-JOB任务执行
 * @author mzx
 */
@Component
@Slf4j
public class EchoServer {
    @Resource
    private MessageService messageService;

    @XxlJob("runJobHandler")
    public void runJobHandler() throws Exception{
        try {
            log.info("进入Xxl-job发消息");
            WebHookMessage webHookMessage = WebHookMessage.buildText("测试!");
            messageService.send(webHookMessage);
            XxlJobHelper.log(String.valueOf(webHookMessage));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
