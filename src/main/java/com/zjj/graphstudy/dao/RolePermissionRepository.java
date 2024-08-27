package com.zjj.graphstudy.dao;

import com.zjj.graphstudy.entity.RolePermission;
import com.zjj.graphstudy.entity.UserRole;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.graphql.data.GraphQlRepository;

/**
 * @author zengJiaJun
 * @crateTime 2024年08月26日 20:34
 * @version 1.0
 */
@GraphQlRepository
public interface RolePermissionRepository extends BaseRepository<RolePermission, Long> {
}
