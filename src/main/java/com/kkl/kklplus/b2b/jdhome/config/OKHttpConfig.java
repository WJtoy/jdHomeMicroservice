package com.kkl.kklplus.b2b.jdhome.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Configuration
public class OKHttpConfig {

    @Value("${okhttp.connect-timeout}")
    private Integer connectTimeout = 10;

    /**
     * 设置写超时
     */
    @Value("${okhttp.write-timeout}")
    private Integer writeTimeout = 10;

    /**
     * 设置读超时
     */
    @Value("${okhttp.read-timeout}")
    private Integer readTimeout = 10;

    /**
     * 是否自动重连
     */
    @Value("${okhttp.retry-on-connection-failure}")
    private Boolean retryOnConnectionFailure = true;

    /**
     * 设置ping检测网络连通性的间隔
     */
    @Value("${okhttp.ping-interval}")
    private Integer pingInterval = 0;

    /**
     * 最大连接数
     */
    @Value("${okhttp.max-active}")
    private Integer maxActive = 10;


    /**
     * socket的keepAlive时间，单位：秒
     */
    @Value("${okhttp.keep-alive}")
    private Integer keepAlive = 5;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient().newBuilder()
                .retryOnConnectionFailure(retryOnConnectionFailure)
                .connectionPool(new ConnectionPool(maxActive, keepAlive, TimeUnit.MINUTES))
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .pingInterval(pingInterval, TimeUnit.SECONDS)
                .build();
    }

}
