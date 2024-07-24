package com.zjj.graphstudy.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年07月24日 15:53
 */
public class MobilecodeNotFoundException extends AuthenticationException {

    public MobilecodeNotFoundException(String msg) {
        super(msg);
    }

    public MobilecodeNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
