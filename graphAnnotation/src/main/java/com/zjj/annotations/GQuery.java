package com.zjj.annotations;

import com.zjj.enums.ReturnType;

import java.lang.annotation.*;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年09月12日 16:31
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(GQuerys.class) // 指定容器注解
public @interface GQuery {

    String value();

    Attribute[] attributes();

    ReturnType returnType() default ReturnType.SINGLE;
}
