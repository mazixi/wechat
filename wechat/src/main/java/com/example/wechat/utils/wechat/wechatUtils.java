package com.example.wechat.utils.wechat;

import com.example.wechat.bean.Holiday;
import com.example.wechat.utils.Date.DateUtil;
import com.example.wechat.utils.Date.HolidayUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.params.HttpMethodParams;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author mzx
 * @Date 2021/6/11 17:56
 * @Version 1.0
 */
public class wechatUtils {
    private static final Logger log = LoggerFactory.getLogger(wechatUtils.class);
    /**
     * 推送消息到企业微信
     *
     * @return
     */
    public static JSONObject sendMessageToEnterpriseWechat() {
        JSONObject obj = new JSONObject();
        JSONObject text = new JSONObject();
        String getAwardUrl= null;
        //申请企业微信token
        getAwardUrl = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=wwcfb4d17ecc475260&corpsecret=BRLIVySC5ZBVB7x2E8UJZz2JJ0vFq_TvhfjXkoUjTWE";
        JSONObject json = new JSONObject();
        try {
            String result = commonGetHttpClient(getAwardUrl);
            log.info("发送结果" + result);
            json = JSONObject.parseObject(result);
            String accessToken = json.getString("access_token");
            //推送消息格式
            text.put("content","有人申请账号权限啦!");
            obj.put("touser", "mzx");
            obj.put("msgtype", "text");
            obj.put("agentid", 1000002);
            obj.put("text", text);
            obj.put("safe", 0);
            String sr = doPost("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+accessToken, obj.toString());
            log.info(sr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }
	/**
	 * 推送消息到企业微信
	 *
	 * @return
	 * @param desc
	 */
	public static JSONObject sendMessageRoot(String desc) {
		JSONObject obj = new JSONObject();
		JSONObject text = new JSONObject();
		try {
			//推送消息格式
			DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			String nowTime = dayFormat.format(new Date());
			DateFormat everyDayFormat = new SimpleDateFormat("HH:mm");
			String nowTimes = everyDayFormat.format(new Date());
			String moinTime = everyDayFormat.format(everyDayFormat.parse("09:00"));
			String noonTime = everyDayFormat.format(everyDayFormat.parse("12:30"));
			String nightTime = everyDayFormat.format(everyDayFormat.parse("18:30"));
			if (nowTimes.equals(moinTime)){
				text.put("content","当前时间为："+ nowTime +"，早上好!铁汁们！");
			}
			if (nowTimes.equals(noonTime)){
				text.put("content","当前时间为："+ nowTime +"，中午好!去吃饭吧！铁汁们！");
			}
			if (desc!=null && nowTimes.equals(nightTime)){
					text.put("content","当前时间为："+ nowTime +"！！！"+desc+"，下班后好好休息吧！");
			}else if (nowTimes.equals(nightTime)){
					text.put("content","当前时间为："+ nowTime +"，下班啦!铁汁们");
			}
			obj.put("msgtype", "text");
			obj.put("text", text);
			obj.put("safe", 0);
			System.out.println(text.getString("content"));
			String sr = doPost("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=b4a30fc5-7963-42d3-87f4-9905c8e17584", obj.toString());
			obj = JSONObject.parseObject(sr);
			log.info(sr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 判断是否为节假日
	 * @return
	 */
	public static JSONObject sendMessageForTime(){
		JSONObject jsonObjects = new JSONObject();
		HolidayUtils holidayUtils = new HolidayUtils();
		List<Holiday> holidays = holidayUtils.holiday();
		List<Date> dates = holidays.stream().map(Holiday::getHolidayDate).collect(Collectors.toList());
		DateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
		String nowTimes = dayFormat.format(new Date());
		String nowTime = nowTimes.replaceAll("-0", "-");
		Date date = DateUtil.parseDate(nowTime);
		//获取当天日期,是否是周末
		String dayEnum = getStringIsWeekday(nowTime);
		String desc =null;
		if (!dates.contains(date) || dayEnum == null){
			//今天是工作日
			//判断明天是否是节假日
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			// 在当前日基础上+1天
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			String format1 = format.format(calendar.getTime());
			Date date1 = DateUtil.parseDate(format1);
			//后一天是否是节假日
			//获取当天日期,是否是周末
			String dayTomoEnum = getStringIsWeekday(nowTime);

			if (dates.contains(date1) || dayTomoEnum != null){
				//明天是休息日
				List<Holiday> holidayList = holidays.stream().filter(holiday -> holiday.getHolidayDate().equals(date1)).collect(Collectors.toList());
				desc = holidayList.get(0).getDesc();
				//今天正常提醒
				jsonObjects = wechatUtils.sendMessageRoot(desc);

			}else{
				//明天是工作日,正常提醒
				jsonObjects = wechatUtils.sendMessageRoot(desc);
			}

		}
		return jsonObjects;
	}


	/**
     * Http-Get请求
     * @param url
     * @return
     */
    public static String commonGetHttpClient(String url) {
        // 创建httpClient实例对象
        HttpClient httpClient = new HttpClient();
        // 设置httpClient连接主机服务器超时时间：30000毫秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
        GetMethod getMethod = new GetMethod(url);
        // 设置post请求超时时间
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 60000);
        getMethod.addRequestHeader("Content-Type", "application/json");
        try {
            httpClient.executeMethod(getMethod);
            String result = getMethod.getResponseBodyAsString();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
	 * post请求（用于请求json格式的参数）
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	public static String doPost(String url, String params) {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);// 创建httpPost
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");
		String charSet = "UTF-8";
		StringEntity entity = new StringEntity(params, charSet);
		httpPost.setEntity(entity);
		CloseableHttpResponse response = null;

		try {

			response = httpclient.execute(httpPost);
			StatusLine status = response.getStatusLine();
			int state = status.getStatusCode();
			if (state == HttpStatus.SC_OK) {
				HttpEntity responseEntity = response.getEntity();
				String jsonString = EntityUtils.toString(responseEntity);
				return jsonString;
			} else {
				log.error("请求返回:" + state + "(" + url + ")");
			}
		} catch (Exception e) {
			log.error("获取企业微信凭证：" + e.getMessage(), e);
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 判断当天是否是星期六/星期日
	 * @param nowTime
	 * @return
	 */
	private static String getStringIsWeekday(String nowTime) {
		JSONObject json;
		String getTomoUrl = "https://api.topthink.com/calendar/day?appCode=614d91184cbda3282c6810b29856b4ec&date=" + nowTime;
		String resultTomo = commonGetHttpClient(getTomoUrl);
		json = JSONObject.parseObject(resultTomo);
		JSONObject dataTomo = json.getJSONObject("data");
		String weekdayTomo = dataTomo.getString("weekday");
		return com.example.wechat.Enum.dayEnum.getDayEnum(weekdayTomo);
	}

}
