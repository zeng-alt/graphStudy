//package com.zjj.graphstudy.filter.login.failure;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import com.zjj.graphstudy.cache.CacheConfig;
//import com.zjj.graphstudy.handler.LoginAuthenticationHandler;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.ServletRequest;
//import jakarta.servlet.ServletResponse;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.CacheManager;
//import org.springframework.http.MediaType;
//import org.springframework.lang.Nullable;
//import org.springframework.security.authentication.*;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.*;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//import org.springframework.stereotype.Component;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//
///**
// * @author zengJiaJun
// * @version 1.0
// * @crateTime 2024年08月14日 20:29
// */
//@Component
//public class LoginFailureExcessiveAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
//
//    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login/mobilecode", "POST");
//    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";
//
//
//    private CacheManager cacheManager;
//
//    private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
//
//    protected LoginFailureExcessiveAuthenticationFilter(String defaultFilterProcessesUrl) {
//        super(defaultFilterProcessesUrl);
//    }
//
//    protected LoginFailureExcessiveAuthenticationFilter() {
//        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
//    }
//
//    @Autowired
//    public LoginFailureExcessiveAuthenticationFilter(AuthenticationManager authenticationManager, LoginAuthenticationHandler loginAuthenticationHandler, CacheManager cacheManager) {
//
//        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
//        setAuthenticationSuccessHandler(loginAuthenticationHandler);
//        setAuthenticationFailureHandler(loginAuthenticationHandler);
//        this.cacheManager = cacheManager;
//    }
//
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
//        if (!request.getMethod().equals("POST")) {
//            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
//        }
//
//        String username = "";
//        if (MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType())) {
//            JSONObject json = JSON.parseObject(getBody(request));
//            username = json.get(this.usernameParameter).toString();
//        } else if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType())) {
//            username = obtainUsername(request);
//        }
//        Integer i = cacheManager.getCache(CacheConfig.Caches.USER.name()).get(username, Integer.class);
//        if (i != null && i >= 3) {
//            throw new InternalAuthenticationServiceException("登录失败次数过多");
//        }
//        cacheManager.getCache(CacheConfig.Caches.USER.name()).put(username, i == null ? 1 : i + 1);
//        return null;
//
//
//    }
//
//
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
//    }
//
//    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        if (!requiresAuthentication(request, response)) {
//            chain.doFilter(request, response);
//            return;
//        }
//
//        try {
//            Authentication authenticationResult = attemptAuthentication(request, response);
//            if (authenticationResult == null) {
//                // return immediately as subclass has indicated that it hasn't completed
//                return;
//            }
//
//            if (!request.getMethod().equals("POST")) {
//                throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
//            }
//
//            String username = "";
//            if (MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType())) {
//                JSONObject json = JSON.parseObject(getBody(request));
//                username = json.get(this.usernameParameter).toString();
//            } else if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType())) {
//                username = obtainUsername(request);
//            }
//
//            Integer i = cacheManager.getCache(CacheConfig.Caches.USER.name()).get(username, Integer.class);
//            if (i != null && i >= 3) {
//                throw new LockedException("登录失败次数过多, 用户被锁定");
//            }
//
//            chain.doFilter(request, response);
//        }
//        catch (InternalAuthenticationServiceException failed) {
//            this.logger.error("An internal error occurred while trying to authenticate the user.", failed);
//            unsuccessfulAuthentication(request, response, failed);
//        }
//		catch (AuthenticationException ex) {
//            // Authentication failed
//            unsuccessfulAuthentication(request, response, ex);
//        }
//
//    }
//
//    @Nullable
//    protected String obtainUsername(HttpServletRequest request) {
//        return request.getParameter(this.usernameParameter);
//    }
//
//    private String getBody(HttpServletRequest request) {
//        StringBuilder wholeStr = new StringBuilder();
//        try {
//            BufferedReader br = request.getReader();
//            String str;
//
//            while ((str = br.readLine()) != null) {
//                wholeStr.append(str);
//            }
//        } catch (IOException e) {
//            logger.error("读取body异常", e);
//        }
//        return wholeStr.toString();
//    }
//
//
//}
