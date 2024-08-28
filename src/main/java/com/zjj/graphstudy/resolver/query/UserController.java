package com.zjj.graphstudy.resolver.query;

import com.zjj.graphstudy.dao.UserRepository;
import com.zjj.graphstudy.dto.PageQuery;
import com.zjj.graphstudy.entity.Users;
import graphql.relay.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.client.HttpSyncGraphQlClient;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月11日 22:11
 * @version 1.0
 */
@Controller
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserRepository userRepository;

//    @QueryMapping
//    public Users user() {
//        Optional<Users> byId = userRepository.findById(5L);
//        return byId.orElseGet(null);
//    }

    @QueryMapping
    public List<Users> getUsers() {
        return userRepository.findAll();
    }


//    @QueryMapping
    public Connection<Users> users(@Argument PageQuery page) {

        Long minId = userRepository.findMinId();
        Long maxId = userRepository.findMaxId();

        List<Users> users = userRepository.findByIdGreaterThan((long) page.getStart(), PageRequest.of(0, page.getSize() + 1));

        List edges = users.stream()
                .limit(page.getSize())
                .map(user -> new DefaultEdge(user, new DefaultConnectionCursor(String.valueOf(user.getId()))))
                .collect(Collectors.toList());

        PageInfo pageInfo = new DefaultPageInfo(
                new DefaultConnectionCursor(String.valueOf(minId)),
                new DefaultConnectionCursor(String.valueOf(maxId)),
                page.getStart() > minId,
                users.size() > page.getSize()
        );

//        RestClient restClient = RestClient.create("");
//        HttpSyncGraphQlClient httpSyncGraphQlClient = HttpSyncGraphQlClient.create(restClient);
        return new DefaultConnection<>(edges, pageInfo);
    }
}
