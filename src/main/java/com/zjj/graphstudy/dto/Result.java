package com.zjj.graphstudy.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月15日 21:38
 * @version 1.0
 */
@Data
public class Result implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;
    private Object data;

}