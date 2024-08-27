package com.zjj.graphstudy.dao;

import com.zjj.graphstudy.entity.RoleExclusive;
import com.zjj.graphstudy.entity.UserGroups;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.graphql.data.GraphQlRepository;

/**
 * @author zengJiaJun
 * @crateTime 2024年08月26日 20:27
 * @version 1.0
 */
@GraphQlRepository
public interface UserGroupRepository extends BaseRepository<UserGroups, Long> {
}
