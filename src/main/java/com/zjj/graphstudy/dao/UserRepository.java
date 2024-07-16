package com.zjj.graphstudy.dao;

import com.zjj.graphstudy.entity.Users;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.graphql.data.GraphQlRepository;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月16日 22:25
 * @version 1.0
 */
@GraphQlRepository
public interface UserRepository extends Repository<Users, Long>, QuerydslPredicateExecutor<Users> {

    Users findById(Long id);
}
