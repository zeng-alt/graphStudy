package com.zjj.graphstudy.dao;

import com.zjj.graphstudy.entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.graphql.data.GraphQlRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月16日 22:25
 * @version 1.0
 */
@GraphQlRepository
public interface UserRepository extends BaseRepository<Users, Long> {

    @Query("select min(id) from Users")
    Long findMinId();

    @Query("select max(id) from Users")
    Long findMaxId();

    List<Users> findByIdGreaterThan(Long id, Pageable pageable);

//    @Query("SELECT u.id, u.email, u.password, u.username FROM Users u where u.username = :username")
    Optional<Users> findByUsername(String username);

    Optional<Users> findByTelephoneNumber(String telephoneNumber);

    Users findUsersById(Long id);
}
