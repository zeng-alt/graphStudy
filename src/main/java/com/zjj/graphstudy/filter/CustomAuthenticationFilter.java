package com.zjj.graphstudy.filter;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年07月24日 17:33
 */
@Component
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }

    private String getBody(HttpServletRequest request) {
        StringBuilder wholeStr = new StringBuilder();
        try {
            BufferedReader br = request.getReader();
            String str;

            while ((str = br.readLine()) != null) {
                wholeStr.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wholeStr.toString();
    }

    @Nullable
    protected String obtainPassword(HttpServletRequest request) {
        String password = JSON.parseObject(getBody(request)).get(getPasswordParameter()).toString();
        return request.getParameter(getPasswordParameter());
    }

    @Nullable
    protected String obtainUsername(HttpServletRequest request) {
        String username = JSON.parseObject(getBody(request)).get(getUsernameParameter()).toString();
        return request.getParameter(getUsernameParameter());
    }

}
