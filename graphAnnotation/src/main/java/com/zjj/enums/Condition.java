package com.zjj.enums;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年09月12日 16:41
 */

public enum Condition {
    EQ("等于"),
    NE("不等于"),
    GT("大于"),
    GE("大于等于"),
    LT("小于"),
    LE("小于等于"),
    LIKE("包含"),
    NOT_LIKE("不包含"),
    LIKE_LEFT("左包含"),
    LIKE_RIGHT("右包含"),
    ALL("根据输入的条件做不同的判断"),
    ;
    private final String description;


    Condition(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }
}
