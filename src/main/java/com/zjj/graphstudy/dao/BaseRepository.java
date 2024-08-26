package com.zjj.graphstudy.dao;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年08月26日 17:16
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends Repository<T, ID> {

    T save(T entity);

    T findById(ID id);

    void deleteById(ID id);

    List<T> findAll();

    void deleteAllById(Iterable<ID> ids);
}
