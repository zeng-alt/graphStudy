package com.zjj.graphstudy.utils;

import com.zjj.annotations.GQuery;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年09月06日 16:59
 */
@NoArgsConstructor
public class EntityUtils {

    /**
     * 判断当前类上是否有jakarta.persistence.Entity注解
     * @param clasz
     * @return
     */
    public static boolean isEntity(Class<?> clasz) {
        return AnnotatedElementUtils.hasAnnotation(clasz, Entity.class);
    }


//    /**
//     * 拿到类上的@GQuery注解
//     * @param clasz
//     * @return
//     */
//    public static GQuery getGQuery(Class<?> clasz) {
//        return AnnotatedElementUtils.findMergedAnnotation(clasz, GQuery.class);
//    }

    /**
     * 拿到类中的字段名和字段类型，只拿拥有get和set方法的字段，并且没有@Transient注解
     * @param clasz
     * @return
     */
    public static Map<String, String> getProperties(Class<?> clasz) {
        Map<String, String> properties = new HashMap<>();
        Field[] fields = clasz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }

            String fieldName = field.getName();
            String fieldType = field.getType().getName();

            // Check for getter method
            String getterMethodName;
            if (fieldType.equals("boolean") || fieldType.equals("java.lang.Boolean")) {
                getterMethodName = "is" + StringUtils.capitalize(fieldName);
            } else {
                getterMethodName = "get" + StringUtils.capitalize(fieldName);
            }

            Method getterMethod = MethodUtils.getAccessibleMethod(clasz, getterMethodName, new Class[]{});
            if (getterMethod == null) {
                continue;
            }

            // Check for setter method
            String setterMethodName = "set" + StringUtils.capitalize(fieldName);
            Method setterMethod = MethodUtils.getAccessibleMethod(clasz, setterMethodName, field.getType());
            if (setterMethod == null) {
                continue;
            }

            properties.put(fieldName, fieldType);
        }

        return properties;
    }
}
