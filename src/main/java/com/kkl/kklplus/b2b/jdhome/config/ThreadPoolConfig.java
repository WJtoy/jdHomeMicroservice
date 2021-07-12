package com.kkl.kklplus.b2b.jdhome.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/***
 *  线程池
 */
@Configuration
public class ThreadPoolConfig {

    @Autowired
    private B2BJdProperties jdProperties;

    /**
     * 异步处理线程
     * @return
     */
    @Bean
    ThreadPoolTaskExecutor processThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(jdProperties.getThreadPool().getCorePoolSize());
        executor.setKeepAliveSeconds(jdProperties.getThreadPool().getKeepAliveSeconds());
        executor.setMaxPoolSize(jdProperties.getThreadPool().getMaxPoolSize());
        executor.setQueueCapacity(jdProperties.getThreadPool().getQueueCapacity());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        return executor;
    }

}
