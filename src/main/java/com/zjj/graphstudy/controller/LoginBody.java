package com.zjj.graphstudy.controller;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月23日 20:58
 * @version 1.0
 */
@Data
public class LoginBody implements Serializable {
    private String username;
    private String password;
}
