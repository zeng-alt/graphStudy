package com.zjj.graphstudy.filter.mobilecode;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.zjj.graphstudy.handler.LoginAuthenticationHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年07月24日 16:57
 */
@Component
public class MobilecodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login/mobilecode", "POST");

    public static final String SPRING_SECURITY_FORM_PHONE_KEY = "phone";

    public static final String SPRING_SECURITY_FORM_CODE_KEY = "code";

    private String phoneParameter = SPRING_SECURITY_FORM_PHONE_KEY;

    private String codeParameter = SPRING_SECURITY_FORM_CODE_KEY;

    protected MobilecodeAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    @Autowired
    public MobilecodeAuthenticationFilter(AuthenticationManager authenticationManager, LoginAuthenticationHandler loginAuthenticationHandler) {

        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        setAuthenticationSuccessHandler(loginAuthenticationHandler);
        setAuthenticationFailureHandler(loginAuthenticationHandler);
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

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            JSONObject json = JSON.parseObject(getBody(request));
            String phone = json.get(this.phoneParameter).toString();
            String code = json.get(this.codeParameter).toString();
            phone = (phone != null) ? phone.trim() : "";
            code = (code != null) ? code : "";
            MobilecodeAuthenticationToken authRequest = MobilecodeAuthenticationToken.unauthenticated(phone, code);
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    protected void setDetails(HttpServletRequest request, MobilecodeAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}
