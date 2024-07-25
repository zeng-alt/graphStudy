package com.zjj.graphstudy.config;

import com.zjj.graphstudy.filter.CustomAuthenticationFilter;
import com.zjj.graphstudy.mobilecode.MobilecodeAuthenticationFilter;
import com.zjj.graphstudy.mobilecode.MobilecodeAuthenticationProvider;
import jakarta.annotation.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年07月24日 22:54
 */
@Component
public class MobilecodeDsl extends AbstractHttpConfigurer<MobilecodeDsl, HttpSecurity> {


//    @Resource
//    private MobilecodeAuthenticationFilter mobilecodeAuthenticationFilter;
//    @Resource
//    private MobilecodeAuthenticationProvider mobilecodeAuthenticationProvider;


    @Override
    public void configure(HttpSecurity http) throws Exception {
        ApplicationContext context = http.getSharedObject(ApplicationContext.class);
        MobilecodeAuthenticationFilter myFilter = context.getBean(MobilecodeAuthenticationFilter.class);
        http.addFilterBefore(myFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
