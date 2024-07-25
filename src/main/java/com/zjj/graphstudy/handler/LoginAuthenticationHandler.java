package com.zjj.graphstudy.handler;

import com.alibaba.fastjson2.JSON;
import com.zjj.graphstudy.dto.Result;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月22日 22:07
 * @version 1.0
 */
@Slf4j
@Component
public class LoginAuthenticationHandler implements AuthenticationFailureHandler, AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        renderString(response, HttpStatus.UNAUTHORIZED.value(), "登录失败 ", exception.getMessage());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        HashMap<String, Object> chaims = new HashMap<>();
        chaims.put("user", principal);
        String token = Jwts.builder().setClaims(chaims).signWith(SignatureAlgorithm.HS512, "mysecret").compact();
        renderString(response, HttpStatus.OK.value(), "登录成功", token);
    }

    public  void renderString(HttpServletResponse response, int status, String msg, String data) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            Result result = new Result();
            result.setCode(status);
            result.setMsg(msg);
            result.setData(data);
            response.getWriter().print(JSON.toJSON(result).toString());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
