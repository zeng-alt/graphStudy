package com.zjj.graphstudy.service;

import com.zjj.graphstudy.exception.MobilecodeNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年07月24日 20:52
 */
public interface MobileDetailsService {

    UserDetails loadUserByPhone(String phone) throws MobilecodeNotFoundException;
}
