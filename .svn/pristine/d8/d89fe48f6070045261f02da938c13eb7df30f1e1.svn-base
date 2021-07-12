package com.kkl.kklplus.b2b.jdhome.config;

import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class B2BJDConfig {

    @Autowired
    private B2BJdProperties b2BJdProperties;

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public JdClient jdClient() {
        return new DefaultJdClient(b2BJdProperties.getAuthorizationConfig().getUrl(),
                b2BJdProperties.getAuthorizationConfig().getAccessToken(),
                b2BJdProperties.getAuthorizationConfig().getAppKey(),
                b2BJdProperties.getAuthorizationConfig().getAppSecret());
    }

}
