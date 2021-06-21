package com.example.wechat.utils.calendarDay;
import com.alibaba.fastjson.JSON;

import java.io.BufferedReader;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.net.HttpURLConnection;

import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.HashMap;
import java.util.Map;
/**
 * @Author mzx
 * @Date 2021/6/15 11:24
 * @Version 1.0
 */
public class calendarUtils {
    /**
     * @param
     * @param httpArg
     * @return 返回结果
     */

    public static String request(String httpArg) {

        String httpUrl = "http://www.easybots.cn/api/holiday.php";

        BufferedReader reader = null;

        String result = null;
        String res = null;
        StringBuffer sbf = new StringBuffer();

        httpUrl = httpUrl + "?d=" + httpArg;

        try {
            URL url = new URL(httpUrl);

            HttpURLConnection connection = (HttpURLConnection) url

                    .openConnection();

            connection.setRequestMethod("GET");

            connection.connect();

            InputStream is = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String strRead = null;

            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);

                sbf.append("\r\n");

            }

            reader.close();

            result = sbf.toString();
            HashMap map = JSON.parseObject(result, HashMap.class);
             res = (String) map.get(httpArg);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

}
