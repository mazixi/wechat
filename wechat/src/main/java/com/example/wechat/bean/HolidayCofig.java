package com.example.wechat.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author kring
 * @Date 2021/6/23 15:39
 * @Version 1.0
 */

@ConfigurationProperties(prefix = "holiday")
@Component
@Data
public class HolidayCofig  {
    private String url;
    private String appcode;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAppcode() {
        return appcode;
    }

    public void setAppcode(String appcode) {
        this.appcode = appcode;
    }
}
