package com.zjj.graphstudy.dto;

import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年07月24日 20:58
 */
public interface UserMobileDetails extends UserDetails, Serializable {

    String getPhone();

    String getCode();
}
