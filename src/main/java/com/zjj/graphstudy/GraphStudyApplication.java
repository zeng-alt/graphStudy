package com.zjj.graphstudy;

import com.zjj.graphstudy.dao.UserRepository;
import com.zjj.graphstudy.entity.Users;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.util.List;
import java.util.Map;


// 扫描cn.hutool.extra.spring包下所有类并注册之
//@ComponentScan(basePackages={"cn.hutool.extra.spring"})
@Import(cn.hutool.extra.spring.SpringUtil.class)
@SpringBootApplication
@EnableJpaRepositories
public class GraphStudyApplication implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(GraphStudyApplication.class, args);
        System.out.println("已启动");
        UserDetails userDetails = User.builder().username("张三").password("123456").build();
        Users users = new Users();
        BeanUtils.copyProperties(userDetails, users);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        List<Users> objects = List.of(
            new Users("张三", "123@qq.com", passwordEncoder.encode("123455"), "12345678911"),
            new Users("阿四", "123@qq.com", passwordEncoder.encode( "123455"), "12345678912"),
            new Users("李四", "123@qq1.com", passwordEncoder.encode( "123455"), "12345678913"),
            new Users("大天", "123@qq1.com", passwordEncoder.encode( "123455"), "12345678914"),
            new Users("小王", "123@qq1.com", passwordEncoder.encode( "123455"), "12345678915"),
            new Users("小一", "123@qq.com", passwordEncoder.encode( "123455"), "12345678916"),
            new Users("小五", "123@qq.com", passwordEncoder.encode( "123455"), "12345678917"),
            new Users("大旬", "123@qq1.com", passwordEncoder.encode( "123455"), "12345678918"),
            new Users("张旬", "123@qq.com", passwordEncoder.encode( "123455"), "12345678919"),
            new Users("张在", "123@qq.com", passwordEncoder.encode( "123455"), "12345678910")
        );
        userRepository.saveAll(objects);
    }
}
