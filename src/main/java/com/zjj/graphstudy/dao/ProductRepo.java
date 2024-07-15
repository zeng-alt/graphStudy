package com.zjj.graphstudy.dao;


import com.zjj.graphstudy.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<Product, Integer> {
	
}