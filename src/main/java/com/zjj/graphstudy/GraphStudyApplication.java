package com.zjj.graphstudy;

import com.zjj.graphstudy.entity.QProduct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class GraphStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphStudyApplication.class, args);
        QProduct product = QProduct.product;

    }

}
