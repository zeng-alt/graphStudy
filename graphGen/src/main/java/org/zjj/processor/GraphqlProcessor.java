package org.zjj.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年09月11日 16:55
 */
@SupportedAnnotationTypes({"com.querydsl.core.annotations.*", "jakarta.persistence.*", "javax.persistence.*"})
@AutoService(Processor.class)
public class GraphqlProcessor extends AbstractProcessor {





    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        return false;
    }
}
