package com.zjj.graphstudy.config;

import com.zjj.graphstudy.dao.ProductRepo;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.query.QuerydslDataFetcher;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月15日 22:56
 * @version 1.0
 */
@Configuration
public class GraphQLAutoConfiguration {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(ProductRepo productRepo) {
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
                ).type("Mutation", builder -> builder
                );
    }

}
