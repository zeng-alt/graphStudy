package com.zjj.graphstudy.dao;

import com.zjj.graphstudy.entity.Permissions;
import com.zjj.graphstudy.entity.Users;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.graphql.data.GraphQlRepository;

import java.util.Collection;
import java.util.List;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年08月26日 17:16
 */
@NoRepositoryBean
@GraphQlRepository
public interface BaseRepository<T, ID> extends Repository<T, ID>, QuerydslPredicateExecutor<T> {

    T save(T entity);

    T findById(ID id);

    void deleteById(ID id);

    List<T> findAll();

    void deleteAllById(Iterable<ID> ids);

    void saveAll(Iterable<T> objects);
//    void saveAll(List<T> objects);
}
