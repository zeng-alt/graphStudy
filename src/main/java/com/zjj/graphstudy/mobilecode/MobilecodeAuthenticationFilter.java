package com.zjj.graphstudy.mobilecode;

import com.zjj.graphstudy.handler.LoginAuthenticationHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年07月24日 16:57
 */
public class MobilecodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login/mobilecode", "POST");

    protected MobilecodeAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
    }

    public MobilecodeAuthenticationFilter(AuthenticationManager authenticationManager) {

        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        setAuthenticationSuccessHandler(new LoginAuthenticationHandler());
        setAuthenticationFailureHandler(new LoginAuthenticationHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String phone = this.obtainPhone(request);
            phone = phone != null ? phone.trim() : "";
            String code = this.obtainCode(request);
            code = code != null ? code : "";
            MobilecodeAuthenticationToken authRequest = MobilecodeAuthenticationToken.unauthenticated(phone, code);
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    @Nullable
    private String obtainPhone(HttpServletRequest request) {
        MediaType.APPLICATION_JSON_VALUE.equals(request.getContentType());
        return null;
    }

    @Nullable
    private String obtainCode(HttpServletRequest request) {
        return null;
    }

    protected void setDetails(HttpServletRequest request, MobilecodeAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
}
