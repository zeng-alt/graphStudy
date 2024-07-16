package com.zjj.graphstudy.config;

import com.zjj.graphstudy.dao.ProductRepo;
import com.zjj.graphstudy.entity.Product;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLScalarType;
import graphql.schema.idl.SchemaDirectiveWiring;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.data.query.QuerydslDataFetcher;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月15日 22:56
 * @version 1.0
 */
@Configuration
public class Config {

//    @Bean
//    public RuntimeWiringConfigurer runtimeWiringConfigurer(ProductRepo productRepo) {
//        GraphQLScalarType builder = new GraphQLScalarType.Builder().build();
////        SchemaDirectiveWiring directiveWiring = ... ;
////        // For single result queries
//////        DataFetcher<Product> dataFetcher =
//////                QuerydslDataFetcher.builder(productRepo).single();
//////
//////        // For multi-result queries
//////        DataFetcher<Iterable<Product>> dataFetcher =
//////                QuerydslDataFetcher.builder(productRepo).many();
//////
//////        // For paginated queries
//////        DataFetcher<Iterable<Product>> dataFetcher =
//////                QuerydslDataFetcher.builder(productRepo).scrollable();
//
//        return wiringBuilder -> wiringBuilder
//                .scalar(builder)
//                .directiveWiring(directiveWiring);
//    }

//    @Bean
//    public DataFetcher<Product> productDataFetcher(ProductRepo productRepo) {
//        return QuerydslDataFetcher.builder(productRepo).single();
//    }


}
