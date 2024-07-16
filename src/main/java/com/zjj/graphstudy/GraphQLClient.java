package com.zjj.graphstudy;

import com.zjj.graphstudy.dao.ProductRepo;
import com.zjj.graphstudy.entity.Product;
import graphql.scalars.ExtendedScalars;
import graphql.schema.DataFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.graphql.data.query.QuerydslDataFetcher;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;


@Configuration
public class GraphQLClient {

	@Bean
	public RuntimeWiringConfigurer runtimeWiringConfigurer(ProductRepo productRepo) {
		DataFetcher<Product> single = QuerydslDataFetcher.builder(productRepo).sortBy(Sort.by("id")).single();
		DataFetcher<Iterable<Product>> many = QuerydslDataFetcher.builder(productRepo).many();
		return wiringBuilder -> wiringBuilder
				.scalar(ExtendedScalars.Date)
				.scalar(ExtendedScalars.DateTime)
				.scalar(ExtendedScalars.GraphQLLong)
				.type("Query", builder -> builder
						.dataFetcher("product", single)
						.dataFetcher("products", many)
						.dataFetcher("productById", env -> productRepo.findById(env.getArgument("id")))
				).type("Mutation", builder -> builder
				);
	}

}

