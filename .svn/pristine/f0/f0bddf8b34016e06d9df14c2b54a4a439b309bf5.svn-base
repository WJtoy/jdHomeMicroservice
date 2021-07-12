package com.kkl.kklplus.b2b.jdhome.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "jd")
public class B2BJdProperties {

    @Getter
    @Setter
    private final AuthorizationConfig authorizationConfig = new AuthorizationConfig();

    /**
     * 京东授权配置
     */
    public static class AuthorizationConfig {

        @Getter
        @Setter
        private String url;

        @Getter
        @Setter
        private String appKey;

        @Getter
        @Setter
        private String appSecret;

        @Getter
        @Setter
        private String accessToken;

        @Getter
        @Setter
        private String refreshToken;

        @Getter
        @Setter
        private String venderCode;

        @Getter
        @Setter
        private String token;

        @Getter
        @Setter
        private Boolean scheduleEnabled = false;
        @Getter
        @Setter
        private Boolean cancelScheduleEnabled = false;
    }

    //子系统
    @Getter
    @Setter
    private final SiteInfo site = new SiteInfo();
    /**
     * b2b配置路由微服务HTTP路径
     */
    @Getter
    @Setter
    private String b2bConfigUrl;

    @Getter
    @Setter
    private String cancelFlagStr = "";

    public static class SiteInfo {

        //负责的子系统
        @Getter
        @Setter
        private String code;

        @Getter
        @Setter
        private Map<String,String> otherSites = new HashMap<>();
    }
    @Getter
    private final ThreadPoolProperties threadPool = new ThreadPoolProperties();

    public static class ThreadPoolProperties {

        @Getter
        @Setter
        private Integer corePoolSize = 1;

        @Getter
        @Setter
        private Integer maxPoolSize = 12;

        @Getter
        @Setter
        private Integer keepAliveSeconds = 60;

        @Getter
        @Setter
        private Integer queueCapacity = 24;

    }
}