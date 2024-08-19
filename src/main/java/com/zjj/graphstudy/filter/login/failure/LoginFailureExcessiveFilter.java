package com.zjj.graphstudy.filter.login.failure;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.zjj.graphstudy.cache.CacheConfig;
import com.zjj.graphstudy.handler.LoginAuthenticationHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.core.log.LogMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年08月14日 20:29
 */
@Component
public class LoginFailureExcessiveFilter extends OncePerRequestFilter {

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login/mobilecode", "POST");
    public static final String SPRING_SECURITY_FORM_USERNAME_KEY = "username";


    private CacheManager cacheManager;

    private String usernameParameter = SPRING_SECURITY_FORM_USERNAME_KEY;
    private RequestMatcher requiresAuthenticationRequestMatcher = DEFAULT_ANT_PATH_REQUEST_MATCHER;

    private AuthenticationFailureHandler failureHandler;

    protected LoginFailureExcessiveFilter(String defaultFilterProcessesUrl) {
        this.requiresAuthenticationRequestMatcher = new AntPathRequestMatcher(defaultFilterProcessesUrl);
    }


    @Autowired
    public LoginFailureExcessiveFilter(LoginAuthenticationHandler loginAuthenticationHandler, CacheManager cacheManager) {

        this.failureHandler = loginAuthenticationHandler;
        this.cacheManager = cacheManager;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
            return;
        }

        try {

            if (!request.getMethod().equals("POST")) {
                throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
            }

            String username = "";
            if (MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType())) {
                JSONObject json = JSON.parseObject(getBody(request));
                username = json.get(this.usernameParameter).toString();
            } else if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(request.getContentType())) {
                username = obtainUsername(request);
            }

            Integer i = cacheManager.getCache(CacheConfig.Caches.USER.name()).get(username, Integer.class);
            if (i != null && i >= 3) {
                throw new LockedException("登录失败次数过多, 用户被锁定");
            }

            chain.doFilter(request, response);
        }
        catch (InternalAuthenticationServiceException failed) {
            this.logger.error("An internal error occurred while trying to authenticate the user.", failed);
            unsuccessfulAuthentication(request, response, failed);
        }
        catch (AuthenticationException ex) {
            // Authentication failed
            unsuccessfulAuthentication(request, response, ex);
        }
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        this.logger.trace("Failed to process authentication request", failed);
        this.logger.trace("Cleared SecurityContextHolder");
        this.logger.trace("Handling authentication failure");
        this.failureHandler.onAuthenticationFailure(request, response, failed);
    }

    @Nullable
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(this.usernameParameter);
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
            logger.error("读取body异常", e);
        }
        return wholeStr.toString();
    }

    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        if (this.requiresAuthenticationRequestMatcher.matches(request)) {
            return true;
        }
        if (this.logger.isTraceEnabled()) {
            this.logger
                    .trace(LogMessage.format("Did not match request to %s", this.requiresAuthenticationRequestMatcher));
        }
        return false;
    }


}
