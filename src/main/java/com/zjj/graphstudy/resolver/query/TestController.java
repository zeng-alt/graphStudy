package com.zjj.graphstudy.resolver.query;

import com.zjj.graphstudy.entity.Users;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月11日 22:11
 * @version 1.0
 */
@Controller
public class TestController {

    @QueryMapping
    public Users getUsers() {
        return null;
    }
}
