package com.zjj.graphstudy.fetcher;

import graphql.schema.DataFetchingEnvironment;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.data.util.TypeInformation;
import org.springframework.graphql.data.pagination.CursorStrategy;
import org.springframework.graphql.data.query.QueryByExampleDataFetcher;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.util.Assert;

import java.util.function.Function;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年09月03日 09:24
 */
public class FuzzyScrollableEntityFetcher<T, R> extends FuzzyQueryByExampleDataFetcher.ManyEntityFetcher<T, R> {

    private final CursorStrategy<ScrollPosition> cursorStrategy;

    private final int defaultCount;

    private final Function<Boolean, ScrollPosition> defaultPosition;

    private final ResolvableType scrollableResultType;

    public FuzzyScrollableEntityFetcher(
            QueryByExampleExecutor<T> executor, TypeInformation<T> domainType, Class<R> resultType,
            CursorStrategy<ScrollPosition> cursorStrategy,
            int defaultCount,
            Function<Boolean, ScrollPosition> defaultPosition,
            Sort sort) {

        super(executor, domainType, resultType, sort);

        Assert.notNull(cursorStrategy, "CursorStrategy is required");
        Assert.notNull(defaultPosition, "'defaultPosition' is required");

        this.cursorStrategy = cursorStrategy;
        this.defaultCount = defaultCount;
        this.defaultPosition = defaultPosition;
        this.scrollableResultType = ResolvableType.forClassWithGenerics(Window.class, resultType);
    }

    @Override
    public ResolvableType getReturnType() {
        return ResolvableType.forClassWithGenerics(Iterable.class, this.scrollableResultType);
    }

    @Override
    protected Iterable<R> getResult(FluentQuery.FetchableFluentQuery<R> queryToUse, DataFetchingEnvironment env) {
        ScrollSubrange range = com.zjj.graphstudy.utils.RepositoryUtils.getScrollSubrange(env, this.cursorStrategy);
        int count = range.count().orElse(this.defaultCount);
        ScrollPosition position = (range.position().isPresent() ?
                range.position().get() : this.defaultPosition.apply(range.forward()));
        return queryToUse.limit(count).scroll(position);
    }

}
