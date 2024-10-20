package com.zjj.graphstudy.handler;

import com.alibaba.fastjson2.JSON;
import com.zjj.graphstudy.cache.CacheConfig;
import com.zjj.graphstudy.dto.Result;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月22日 22:07
 * @version 1.0
 */
@Slf4j
@Component
//@ConditionalOnClass(JdbcTemplate.class)
public class LoginAuthenticationHandler implements AuthenticationFailureHandler, AuthenticationSuccessHandler, AccessDeniedHandler, AuthenticationEntryPoint {

    @Resource
    private CacheManager cacheManager;


    /**
     * 登录失败
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        renderString(response, HttpStatus.UNAUTHORIZED.value(), exception.getMessage(), exception.getMessage());
    }

    /**
     * 登录成功
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        HashMap<String, Object> chaims = new HashMap<>();
        String uuid = UUID.randomUUID().toString();
        chaims.put("user", uuid);
        String token = Jwts.builder().setClaims(chaims).signWith(SignatureAlgorithm.HS512, "mysecret").compact();
        cacheManager.getCache(CacheConfig.Caches.USER.name()).put(uuid, principal);
        renderString(response, HttpStatus.OK.value(), "登录成功", token);
    }

    public void renderString(HttpServletResponse response, int status, String msg, String data) {
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

    /**
     * 验证失败
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        renderString(response, HttpStatus.UNAUTHORIZED.value(), authException.getMessage(), authException.getMessage());
    }

    /**
     * 访问被拒绝
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        renderString(response, HttpStatus.UNAUTHORIZED.value(), accessDeniedException.getMessage(), accessDeniedException.getMessage());
    }
}
