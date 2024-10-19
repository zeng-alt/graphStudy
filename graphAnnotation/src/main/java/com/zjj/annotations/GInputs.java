package com.zjj.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年09月12日 16:31
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface GInputs {
    GInput[] value();
}
