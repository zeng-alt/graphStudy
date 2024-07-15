package com.zjj.graphstudy.service;

import com.zjj.graphstudy.dao.ProductRepo;
import com.zjj.graphstudy.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ProductService {

	@Autowired
	private ProductRepo productRepo; // interface
	
	public Product saveProduct(Product p) {
		return productRepo.save(p);
	}
	
	public List<Product> getProducts() {
		return productRepo.findAll();
	}
	
	public Product getProductById(int id) {
		return productRepo.findById(id).get();
	}
}