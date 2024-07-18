package com.zjj.graphstudy.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年06月26日 19:22
 */
@Data
public class PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    private Integer page = 1;
    private Integer size = 10;
    /**
     * 排序列
     */
    private String orderByColumn;

    /**
     * 排序的方向desc或者
     */
    private String isAsc = DESC;

    public void setPage(Integer page) {
        this.page = page <= 0 ? 1 : page;
    }

    public int getStart() {
        return (page - 1) * size;
    }

    public int getEnd() {
        return getStart() + size;
    }
}
