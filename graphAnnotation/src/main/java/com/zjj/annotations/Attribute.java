package com.zjj.annotations;

import com.zjj.enums.Condition;

import java.lang.annotation.*;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年09月12日 16:40
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Attribute {
    String value();
    Condition condition() default Condition.LIKE;

}
