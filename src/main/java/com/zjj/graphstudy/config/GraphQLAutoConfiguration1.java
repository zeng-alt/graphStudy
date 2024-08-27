package com.zjj.graphstudy.config;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.zjj.graphstudy.dao.*;
import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.graphql.data.query.QuerydslDataFetcher;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月15日 22:56
 * @version 1.0
 */
@Configuration
public class GraphQLAutoConfiguration1 {

    @Resource
    private RoleExclusiveRepository roleExclusiveRepository;
    @Resource
    private UserRoleRepository userRoleRepository;
    @Resource
    private UserGroupRepository userGroupRepository;

    public static Class<?> getEntityClass(Class<?> repoClass) {
        Type genericSuperclass = repoClass.getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return (Class<?>) actualTypeArguments[0];
        }

        throw new IllegalArgumentException("Not a valid Repository class");
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(ProductRepo productRepo, RoleRepository roleRepository, List<BaseRepository> list) {


//        RuntimeWiring.newRuntimeWiring()

//        final RuntimeWiring.Builder[] builder = new RuntimeWiring.Builder[1];

        return new RuntimeWiringConfigurer() {
            @Override
            public void configure(RuntimeWiring.Builder builder) {
                builder.scalar(ExtendedScalars.Currency)
                        .scalar(ExtendedScalars.Date)
                        .scalar(ExtendedScalars.DateTime)
                        .scalar(ExtendedScalars.GraphQLLong)
                        .scalar(ExtendedScalars.GraphQLBigDecimal);

                Set<Class<?>> classes = ClassUtil.scanPackage("com.zjj.graphstudy.dao");
                for (Class<?> aClass : classes) {

                    if (aClass.getTypeName().contains("com.zjj.graphstudy.dao.BaseRepository") || !BaseRepository.class.isAssignableFrom(aClass)) {
                        continue;
                    }

                    Type[] genericInterfaces = aClass.getGenericInterfaces();
                    for (Type genericInterface : genericInterfaces) {
                        if (genericInterface instanceof ParameterizedType parameterizedType) {
//                            ParameterizedType parameterizedType = (ParameterizedType) genericInterface;

                            // 检查是否是BaseRepository的参数化类型
                            Type rawType = parameterizedType.getRawType();
                            if (rawType.getTypeName().contains("com.zjj.graphstudy.dao.BaseRepository")) {
                                continue;
                            }
                            // 获取BaseRepository的泛型参数
                            Type[] typeArguments = parameterizedType.getActualTypeArguments();

                            String type = StringUtils.substringAfterLast(typeArguments[0].getTypeName(), ".");
                            // 如果type最后一个字母是s,去掉
                            if (type.charAt(type.length() - 1) == 's') {
                                type = type.substring(0, type.length() - 1);
                            }
                            Object bean = SpringUtil.getBean(aClass);
                            BaseRepository baseRepository = (BaseRepository) bean;

                            String capitalize = StringUtils.capitalize(type);
                            String uncapitalize = StringUtils.uncapitalize(type);
                            builder.type("Query", b -> b
                                    .dataFetcher(uncapitalize, QuerydslDataFetcher.builder(baseRepository).single())
                                    .dataFetcher(uncapitalize + "s", QuerydslDataFetcher.builder(baseRepository).many())
                                    .dataFetcher("page" + capitalize + "s", QuerydslDataFetcher.builder(baseRepository).scrollable())
                                    .dataFetcher("find" + capitalize + "ById", env -> baseRepository.findById(env.getArgument("id")))
                            );
                        }
                    }
                }
            }
        };





//                .type("Query", builder -> builder
//                        .dataFetcher("product", QuerydslDataFetcher.builder(productRepo).sortBy(Sort.by("id")).single())
//                        .dataFetcher("products", QuerydslDataFetcher.builder(productRepo).many())
//                        .dataFetcher("productById", env -> productRepo.findById(env.getArgument("id")))
//                )
//                .type("Query", builder -> builder
//                        .dataFetcher("findRole", QuerydslDataFetcher.builder(roleRepository).single())
//                        .dataFetcher("roles", QuerydslDataFetcher.builder(roleRepository).many())
//                )
//                .type("Query", builder -> builder
//                        .dataFetcher("roleExclusive", QuerydslDataFetcher.builder(roleExclusiveRepository).single())
//                        .dataFetcher("roleExclusives", QuerydslDataFetcher.builder(roleExclusiveRepository).many())
//                )
//                .type("Query", builder -> builder
//                        .dataFetcher("userRole", QuerydslDataFetcher.builder(userRoleRepository).single())
//                        .dataFetcher("userRoles", QuerydslDataFetcher.builder(userRoleRepository).many())
//                )
//                .type("Query", builder -> builder
//                        .dataFetcher("userGroup", QuerydslDataFetcher.builder(userGroupRepository).single())
//                        .dataFetcher("userGroups", QuerydslDataFetcher.builder(userGroupRepository).many())
//                )
//                .type("Mutation", builder -> builder
//                );


//        return wiringConfigurer;
    }

}
