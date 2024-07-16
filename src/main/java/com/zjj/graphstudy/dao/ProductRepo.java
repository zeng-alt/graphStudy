package com.zjj.graphstudy.dao;


import com.zjj.graphstudy.entity.Product;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.graphql.data.GraphQlRepository;


@GraphQlRepository
public interface ProductRepo extends Repository<Product, Long>, QuerydslPredicateExecutor<Product> {
	Product findById(Long id);

	Product findProductById(Long id);
}