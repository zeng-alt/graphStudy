package com.zjj.graphstudy.dao;

import com.zjj.graphstudy.entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.graphql.data.GraphQlRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月16日 22:25
 * @version 1.0
 */
@GraphQlRepository
public interface UserRepository extends JpaRepository<Users, Long>, QuerydslPredicateExecutor<Users> {

    @Query("select min(id) from Users")
    Long findMinId();

    @Query("select max(id) from Users")
    Long findMaxId();

    List<Users> findByIdGreaterThan(Long id, Pageable pageable);

    Optional<Users> findByUsername(String username);
}
