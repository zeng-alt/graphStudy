package com.zjj.graphstudy.dao;


import com.zjj.graphstudy.entity.RoleExclusive;
import com.zjj.graphstudy.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.graphql.data.GraphQlRepository;

@GraphQlRepository
public interface UserRoleRepository extends BaseRepository<UserRole, Long> {
}