package com.zjj.graphstudy.dao;


import com.zjj.graphstudy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.graphql.data.GraphQlRepository;

@GraphQlRepository
public interface ProductRepo extends JpaRepository<Product, Integer>, QuerydslPredicateExecutor<Product> {
	
}