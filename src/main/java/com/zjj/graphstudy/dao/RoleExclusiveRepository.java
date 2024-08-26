package com.zjj.graphstudy.dao;

import com.zjj.graphstudy.entity.RoleExclusive;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.graphql.data.GraphQlRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年08月26日 17:15
 */
@Repository("roleExclusiveRepository")
@GraphQlRepository
public interface RoleExclusiveRepository extends BaseRepository<RoleExclusive, Long>, QuerydslPredicateExecutor<RoleExclusive> {
}
