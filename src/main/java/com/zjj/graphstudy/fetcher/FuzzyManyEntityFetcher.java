package com.zjj.graphstudy.fetcher;

import graphql.schema.DataFetchingEnvironment;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.data.util.TypeInformation;
import org.springframework.graphql.data.query.QueryByExampleDataFetcher;
import org.springframework.graphql.execution.SelfDescribingDataFetcher;
import org.springframework.validation.BindException;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年09月03日 09:10
 */
public class FuzzyManyEntityFetcher<T, R>
        extends FuzzyQueryByExampleDataFetcher<T> implements SelfDescribingDataFetcher<Iterable<R>> {

    private final QueryByExampleExecutor<T> executor;

    private final Class<R> resultType;

    private final Sort sort;

    public FuzzyManyEntityFetcher(
            QueryByExampleExecutor<T> executor, TypeInformation<T> domainType,
            Class<R> resultType, Sort sort) {

        super(domainType);
        this.executor = executor;
        this.resultType = resultType;
        this.sort = sort;
    }

    @Override
    public ResolvableType getReturnType() {
        return ResolvableType.forClassWithGenerics(Iterable.class, this.resultType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<R> get(DataFetchingEnvironment env) throws BindException {
        return this.executor.findBy(buildExample(env), (query) -> {
            FluentQuery.FetchableFluentQuery<R> queryToUse = (FluentQuery.FetchableFluentQuery<R>) query;

            if (this.sort.isSorted()) {
                queryToUse = queryToUse.sortBy(this.sort);
            }

            if (requiresProjection(this.resultType)) {
                queryToUse = queryToUse.as(this.resultType);
            }
            else {
                queryToUse = queryToUse.project(buildPropertyPaths(env.getSelectionSet(), this.resultType));
            }

            return getResult(queryToUse, env);
        });
    }

    protected Iterable<R> getResult(FluentQuery.FetchableFluentQuery<R> queryToUse, DataFetchingEnvironment env) {
        return queryToUse.all();
    }

}
