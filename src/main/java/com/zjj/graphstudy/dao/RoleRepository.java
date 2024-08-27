package com.zjj.graphstudy.dao;

import com.zjj.graphstudy.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.graphql.data.GraphQlRepository;

import javax.management.relation.Role;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年08月26日 15:05
 */
@GraphQlRepository
public interface RoleRepository extends BaseRepository<Roles, Long> {

    Roles findRolesById(Long id);
}
