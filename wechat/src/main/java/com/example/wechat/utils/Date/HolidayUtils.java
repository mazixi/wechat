package com.example.wechat.utils.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.wechat.bean.Holiday;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @Author mzx
 * @Date 2021/6/15 17:01
 * @Version 1.0
 */
@Component
public class HolidayUtils {

    private String appUrl = "https://api.topthink.com/calendar/month";
    private String appcode = "614d91184cbda3282c6810b29856b4ec";


    /**
     * 每月更新节假日 每月一号执行
     */
    @Scheduled(cron = "0 0 2 1 * ? *")
    public List<Holiday> holiday() {
        String ny = DateUtil.getny();
        List<Holiday> holidays = new ArrayList<>();
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();

            MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            JSONObject map = new JSONObject();
            map.put("yearMonth", ny);
            map.put("appCode",appcode);
            HttpEntity<Map> requestEntity = new HttpEntity<>(map, headers);

            String post = restTemplate.postForObject(appUrl, requestEntity, String.class);

            if (post == null || "".equals(post) || !post.contains("\"code\":0")) {
                return null;
            }
            map.clear();
            JSONObject holidayJson = JSON.parseObject(post);
            JSONObject data = holidayJson.getJSONObject("data");
            List<JSONObject> holidayArray = JSON.parseArray(data.getString("holiday_array"), JSONObject.class);
            for (JSONObject holiday : holidayArray) {
                List<JSONObject> list = JSON.parseArray(holiday.getString("list"), JSONObject.class);
                for (JSONObject jsonObject : list) {
                    String date = jsonObject.getString("date");
                    String status = jsonObject.getString("status");
                    Holiday holiday1 = new Holiday();
                    if ("1".equals(status)) {
                        //节假日日期：名称
                        holiday1.setHolidayDate(DateUtil.parseDate(date));
                        holiday1.setDesc(holiday.getString("name"));
                        holiday1.setStatus(1);
                    } else {
                        //调休工作日时间：备注
                        holiday1.setHolidayDate(DateUtil.parseDate(date));
                        holiday1.setDesc(holiday.getString("desc"));
                        holiday1.setStatus(2);
                    }
                    holidays.add(holiday1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return holidays;
    }

}
