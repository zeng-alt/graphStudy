package com.zjj.graphstudy;

import com.zjj.graphstudy.entity.Product;
import com.zjj.graphstudy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class ProductClient implements CommandLineRunner {
	
	@Autowired
	ProductService service;
	
	// gets called as soon as Spring container is created and wiring completed
	@Override
	public void run(String... args) throws Exception {
		addProducts();
		getProducts();
	}

	private void getProducts() {
		for(Product p : service.getProducts()) {
			System.out.println(p);
		}
	}

	private void addProducts() {
		service.saveProduct(new Product(1, "A", 3434.22));
		service.saveProduct(new Product(2, "B", 3434.22));
		service.saveProduct(new Product(3, "C", 3434.22));
	}

}