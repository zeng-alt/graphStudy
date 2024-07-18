package com.zjj.graphstudy;

import com.zjj.graphstudy.dao.UserRepository;
import com.zjj.graphstudy.entity.QProduct;
import com.zjj.graphstudy.entity.Users;
import graphql.com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Arrays;
import java.util.List;


@SpringBootApplication
@EnableJpaRepositories
public class GraphStudyApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(GraphStudyApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        List<Users> objects = List.of(
            new Users("张三", "123@qq.com", "123455"),
            new Users("阿四", "123@qq.com", "123455"),
            new Users("李四", "123@qq.com", "123455"),
            new Users("大天", "123@qq.com", "123455"),
            new Users("小王", "123@qq.com", "123455"),
            new Users("小一", "123@qq.com", "123455"),
            new Users("小五", "123@qq.com", "123455"),
            new Users("大旬", "123@qq.com", "123455"),
            new Users("张旬", "123@qq.com", "123455"),
            new Users("张在", "123@qq.com", "123455")
        );
        userRepository.saveAll(objects);
    }
}
