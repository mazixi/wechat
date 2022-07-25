package com.example.wechat;

import com.example.wechat.bean.Holiday;
import com.example.wechat.utils.cofig.AssociationFieldHandlerUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class WechatApplicationTests {

    @Test
    public void contextLoads() {
        String s = AssociationFieldHandlerUtil
                .deadLineHandler("2月").get(AssociationFieldHandlerUtil.NUM_KEY);
        System.out.println("S:"+s);
    }

    @Test
    public void batchDown4() {
        List<Holiday> holidayList1= new ArrayList<>();
        List<Holiday> holidayList2= new ArrayList<>();

        Holiday holiday = new Holiday();
        holiday.setStatus(1);
        holidayList1.add(holiday);
        List<Holiday> msgAndStructuredMsgInfoPage = holidayList1;
        Holiday holiday2 = new Holiday();
        holiday2.setStatus(2);
        holidayList2.add(holiday2);
        if (holidayList2.size() > 0){
            msgAndStructuredMsgInfoPage = Stream.of(holidayList1, holidayList2)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }
//        List<Holiday> msgAndStructuredMsgInfoPage = Stream.of(holidayList1, holidayList2)
//                .flatMap(Collection::stream)
//                .collect(Collectors.toList());
//        List<Holiday> msgAndStructuredMsgInfoPage = holidayList2.stream().sequential().collect(Collectors.toCollection(() -> holidayList1));
        System.out.println(msgAndStructuredMsgInfoPage.size());
        System.out.println(msgAndStructuredMsgInfoPage.get(0).getStatus());
    }
    @Test
    public void batchDown3() {
        String time = "20220718-20:20:02";
        String time2 = time.substring(0,time.length()-2)+"00";
        System.out.println(time2);
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss");
        LocalDateTime validUntilTime = LocalDateTime.parse(time2, df);
        long validUntilTimeStr = validUntilTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        Date date = new Date(validUntilTimeStr);
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        //Date转换为LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        System.out.println(localDateTime);

//        ZoneId zoneId = ZoneId.systemDefault();
//        ZonedDateTime zdt = validUntilTime.atZone(zoneId);
//        Date date1 = Date.from(zdt.toInstant());
//        System.out.println(localDateTime);
//        System.out.println(date);
//        System.out.println(new Date());


//        DateTimeFormatter df1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String nowtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
//        LocalDateTime validUntilTime1 = LocalDateTime.parse(nowtime, df1);
//        System.out.println(JSON.toJSONString(validUntilTime1));
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String dateString = formatter.format(formatter);
//        System.out.println(JSON.toJSONString(localDateTime));
    }
}
