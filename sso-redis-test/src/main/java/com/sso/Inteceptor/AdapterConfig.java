package com.sso.Inteceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by ql on 2019/7/4.
 */

@Configuration
public class AdapterConfig extends WebMvcConfigurerAdapter {
    @Autowired
    UserLoginInteceptor userLoginInteceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginInteceptor)
                .addPathPatterns("/github/**");
        super.addInterceptors(registry);
    }
}
