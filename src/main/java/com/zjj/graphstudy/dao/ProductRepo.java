package com.zjj.graphstudy.dao;


import com.zjj.graphstudy.entity.Product;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.graphql.data.GraphQlRepository;

@GraphQlRepository
public interface ProductRepo extends CrudRepository<Product, Long>, QuerydslPredicateExecutor<Product> {
	
}