package com.zjj.graphstudy.filter.login.failure;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年08月14日 20:11
 */
public interface AuthenticationFailureExcessiveHandler extends AuthenticationFailureHandler {
}
