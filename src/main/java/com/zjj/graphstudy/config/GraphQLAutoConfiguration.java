package com.zjj.graphstudy.config;

import com.zjj.graphstudy.dao.*;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLCodeRegistry;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.query.QuerydslDataFetcher;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月15日 22:56
 * @version 1.0
 */
@Configuration
public class GraphQLAutoConfiguration {

    @Resource
    private RoleExclusiveRepository roleExclusiveRepository;
    @Resource
    private UserRoleRepository userRoleRepository;
    @Resource
    private UserGroupRepository userGroupRepository;

    public static Class<?> getEntityClass(Object repositoryInstance) {
        Class<?> repoClass = repositoryInstance.getClass();
        Type genericSuperclass = repoClass.getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            return (Class<?>) actualTypeArguments[0];
        }

        throw new IllegalArgumentException("Not a valid Repository instance");
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(ProductRepo productRepo, RoleRepository roleRepository, List<BaseRepository> list) {

//        for (BaseRepository baseRepository : list) {
//            Class<?> entityClass = getEntityClass(baseRepository);
//            System.out.println(entityClass);
//        }


        return wiringBuilder -> wiringBuilder
                .scalar(ExtendedScalars.Currency)
                .scalar(ExtendedScalars.Date)
                .scalar(ExtendedScalars.DateTime)
                .scalar(ExtendedScalars.GraphQLLong)
                .scalar(ExtendedScalars.GraphQLBigDecimal)
                .type("Query", builder -> builder
                        .dataFetcher("product", QuerydslDataFetcher.builder(productRepo).sortBy(Sort.by("id")).single())
                        .dataFetcher("products", QuerydslDataFetcher.builder(productRepo).many())
                        .dataFetcher("productById", env -> productRepo.findById(env.getArgument("id")))
                )
                .type("Query", builder -> builder
                        .dataFetcher("findRole", QuerydslDataFetcher.builder(roleRepository).single())
                        .dataFetcher("roles", QuerydslDataFetcher.builder(roleRepository).many())
                )
                .type("Query", builder -> builder
                        .dataFetcher("roleExclusive", QuerydslDataFetcher.builder(roleExclusiveRepository).single())
                        .dataFetcher("roleExclusives", QuerydslDataFetcher.builder(roleExclusiveRepository).many())
                )
                .type("Query", builder -> builder
                        .dataFetcher("userRole", QuerydslDataFetcher.builder(userRoleRepository).single())
                        .dataFetcher("userRoles", QuerydslDataFetcher.builder(userRoleRepository).many())
                )
                .type("Query", builder -> builder
                        .dataFetcher("userGroup", QuerydslDataFetcher.builder(userGroupRepository).single())
                        .dataFetcher("userGroups", QuerydslDataFetcher.builder(userGroupRepository).many())
                )
                .type("Mutation", builder -> builder
                );
    }

}
