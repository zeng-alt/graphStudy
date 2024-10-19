package com.zjj.graphstudy.fetcher;

import com.zjj.graphstudy.utils.PropertySelection;
import com.zjj.graphstudy.utils.RepositoryUtils;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.GraphQLArgument;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.util.TypeInformation;
import org.springframework.graphql.data.GraphQlArgumentBinder;
import org.springframework.graphql.data.GraphQlRepository;
import org.springframework.graphql.data.pagination.CursorEncoder;
import org.springframework.graphql.data.pagination.CursorStrategy;
import org.springframework.graphql.data.query.*;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.graphql.execution.SelfDescribingDataFetcher;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年09月03日 09:14
 */
public class FuzzyQueryByExampleDataFetcher<T> {

    private static final Log logger = LogFactory.getLog(FuzzyQueryByExampleDataFetcher.class);


    private final TypeInformation<T> domainType;

    private final GraphQlArgumentBinder argumentBinder;


    public FuzzyQueryByExampleDataFetcher(TypeInformation<T> domainType) {
        this.domainType = domainType;
        this.argumentBinder = new GraphQlArgumentBinder();
    }


    /**
     * Provides shared implementation of
     * {@link SelfDescribingDataFetcher#getDescription()} for all subclasses.
     * @since 1.2.0
     */
    public String getDescription() {
        return "FuzzyQueryByExampleDataFetcher<" + this.domainType.getType().getName() + ">";
    }

    /**
     * Prepare an {@link Example} from GraphQL request arguments.
     * @param environment contextual info for the GraphQL request
     * @return the resulting example
     */
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    protected Example<T> buildExample(DataFetchingEnvironment environment) throws BindException {
        String name = getArgumentName(environment);
        ResolvableType targetType = ResolvableType.forClass(this.domainType.getType());
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id") // 忽略ID字段，防止精确匹配
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnoreCase()
                .withNullHandler(ExampleMatcher.NullHandler.IGNORE);
        return (Example<T>) Example.of(this.argumentBinder.bind(environment, name, targetType), matcher);
    }

    /**
     * For a single argument that is a GraphQL input type, return the argument
     * name, thereby nesting and having the example Object populated from the
     * sub-map. Otherwise, {@code null} to bind using the top-level map.
     */
    @Nullable
    private static String getArgumentName(DataFetchingEnvironment environment) {
        Map<String, Object> arguments = environment.getArguments();
        List<GraphQLArgument> definedArguments = environment.getFieldDefinition().getArguments();
        if (definedArguments.size() == 1) {
            String name = definedArguments.get(0).getName();
            if (arguments.get(name) instanceof Map<?, ?>) {
                return name;
            }
        }
        return null;
    }

    protected boolean requiresProjection(Class<?> resultType) {
        return !resultType.equals(this.domainType.getType());
    }

    protected Collection<String> buildPropertyPaths(DataFetchingFieldSelectionSet selection, Class<?> resultType) {

        // Compute selection only for non-projections
        if (this.domainType.getType().equals(resultType) ||
                this.domainType.getType().isAssignableFrom(resultType) ||
                this.domainType.isSubTypeOf(resultType)) {
            return PropertySelection.create(this.domainType, selection).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return getDescription();
    }


    /**
     * Create a new {@link FuzzyQueryByExampleDataFetcher.Builder} accepting {@link QueryByExampleExecutor}
     * to build a {@link DataFetcher}.
     * @param executor the QBE repository object to use
     * @param <T> the domain type of the repository
     * @return a new builder
     */
    public static <T> FuzzyQueryByExampleDataFetcher.Builder<T, T> builder(QueryByExampleExecutor<T> executor) {
        return new FuzzyQueryByExampleDataFetcher.Builder<>(executor, RepositoryUtils.getDomainType(executor));
    }

    /**
     * Create a new {@link FuzzyQueryByExampleDataFetcher.ReactiveBuilder} accepting
     * {@link ReactiveQueryByExampleExecutor} to build a {@link DataFetcher}.
     * @param executor the QBE repository object to use
     * @param <T> the domain type of the repository
     * @return a new builder
     */
    public static <T> FuzzyQueryByExampleDataFetcher.ReactiveBuilder<T, T> builder(ReactiveQueryByExampleExecutor<T> executor) {
        return new FuzzyQueryByExampleDataFetcher.ReactiveBuilder<>(executor, RepositoryUtils.getDomainType(executor));
    }

    /**
     * Variation of {@link #autoRegistrationConfigurer(List, List, CursorStrategy, ScrollSubrange)}
     * without a {@code CursorStrategy} and default {@link ScrollSubrange}.
     * For default values, see the respective methods on {@link FuzzyQueryByExampleDataFetcher.Builder} and
     * {@link FuzzyQueryByExampleDataFetcher.ReactiveBuilder}.
     * @param executors repositories to consider for registration
     * @param reactiveExecutors reactive repositories to consider for registration
     */
    public static RuntimeWiringConfigurer autoRegistrationConfigurer(
            List<QueryByExampleExecutor<?>> executors,
            List<ReactiveQueryByExampleExecutor<?>> reactiveExecutors) {

        return autoRegistrationConfigurer(executors, reactiveExecutors, null, null);
    }

    /**
     * Return a {@link RuntimeWiringConfigurer} that installs a
     * {@link graphql.schema.idl.WiringFactory} to find queries with a return
     * type whose name matches to the domain type name of the given repositories
     * and registers {@link DataFetcher}s for them.
     *
     * <p><strong>Note:</strong> This applies only to top-level queries and
     * repositories annotated with {@link GraphQlRepository @GraphQlRepository}.
     * @param executors repositories to consider for registration
     * @param reactiveExecutors reactive repositories to consider for registration
     * @param cursorStrategy for decoding cursors in pagination requests;
     * if {@code null}, then {@link FuzzyQueryByExampleDataFetcher.Builder#cursorStrategy} defaults apply.
     * @param defaultScrollSubrange default parameters for scrolling;
     * if {@code null}, then {@link FuzzyQueryByExampleDataFetcher.Builder#defaultScrollSubrange} defaults apply.
     * @return the created configurer
     * @since 1.2.0
     */
    public static RuntimeWiringConfigurer autoRegistrationConfigurer(
            List<QueryByExampleExecutor<?>> executors,
            List<ReactiveQueryByExampleExecutor<?>> reactiveExecutors,
            @Nullable CursorStrategy<ScrollPosition> cursorStrategy,
            @Nullable ScrollSubrange defaultScrollSubrange) {

        Map<String, AutoRegistrationRuntimeWiringConfigurer.DataFetcherFactory> factories = new HashMap<>();

        for (QueryByExampleExecutor<?> executor : executors) {
            String typeName = RepositoryUtils.getGraphQlTypeName(executor);
            if (typeName != null) {
                FuzzyQueryByExampleDataFetcher.Builder<?, ?> builder = customize(executor, builder(executor)
                        .cursorStrategy(cursorStrategy)
                        .defaultScrollSubrange(defaultScrollSubrange));

                factories.put(typeName, new AutoRegistrationRuntimeWiringConfigurer.DataFetcherFactory() {
                    @Override
                    public DataFetcher<?> single() {
                        return builder.single();
                    }

                    @Override
                    public DataFetcher<?> many() {
                        return builder.many();
                    }

                    @Override
                    public DataFetcher<?> scrollable() {
                        return builder.scrollable();
                    }
                });
            }
        }

        for (ReactiveQueryByExampleExecutor<?> executor : reactiveExecutors) {
            String typeName = RepositoryUtils.getGraphQlTypeName(executor);
            if (typeName != null) {
                FuzzyQueryByExampleDataFetcher.ReactiveBuilder<?, ?> builder = customize(executor, builder(executor)
                        .cursorStrategy(cursorStrategy)
                        .defaultScrollSubrange(defaultScrollSubrange));

                factories.put(typeName, new AutoRegistrationRuntimeWiringConfigurer.DataFetcherFactory() {
                    @Override
                    public DataFetcher<?> single() {
                        return builder.single();
                    }

                    @Override
                    public DataFetcher<?> many() {
                        return builder.many();
                    }

                    @Override
                    public DataFetcher<?> scrollable() {
                        return builder.scrollable();
                    }
                });
            }
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Auto-registration candidate typeNames " + factories.keySet());
        }

        return new AutoRegistrationRuntimeWiringConfigurer(factories);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static FuzzyQueryByExampleDataFetcher.Builder customize(QueryByExampleExecutor<?> executor, FuzzyQueryByExampleDataFetcher.Builder builder) {
        if (executor instanceof FuzzyQueryByExampleDataFetcher.QueryByExampleBuilderCustomizer<?> customizer) {
            return customizer.customize(builder);
        }
        return builder;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static FuzzyQueryByExampleDataFetcher.ReactiveBuilder customize(ReactiveQueryByExampleExecutor<?> executor, FuzzyQueryByExampleDataFetcher.ReactiveBuilder builder) {
        if (executor instanceof FuzzyQueryByExampleDataFetcher.ReactiveQueryByExampleBuilderCustomizer<?> customizer) {
            return customizer.customize(builder);
        }
        return builder;
    }


    /**
     * Builder for a Query by Example-based {@link DataFetcher}. Note that builder
     * instances are immutable and return a new instance of the builder
     * when calling configuration methods.
     *
     * @param <T> domain type
     * @param <R> result type
     */
    public static class Builder<T, R> {

        private final QueryByExampleExecutor<T> executor;

        private final TypeInformation<T> domainType;

        private final Class<R> resultType;

        @Nullable
        private final CursorStrategy<ScrollPosition> cursorStrategy;

        @Nullable
        private final Integer defaultScrollCount;

        @Nullable
        private final Function<Boolean, ScrollPosition> defaultScrollPosition;

        private final Sort sort;

        @SuppressWarnings("unchecked")
        Builder(QueryByExampleExecutor<T> executor, Class<R> domainType) {
            this(executor, TypeInformation.of((Class<T>) domainType), domainType, null, null, null, Sort.unsorted());
        }

        Builder(QueryByExampleExecutor<T> executor, TypeInformation<T> domainType, Class<R> resultType,
                @Nullable CursorStrategy<ScrollPosition> cursorStrategy,
                @Nullable Integer defaultScrollCount, @Nullable Function<Boolean, ScrollPosition> defaultScrollPosition,
                Sort sort) {

            this.executor = executor;
            this.domainType = domainType;
            this.resultType = resultType;
            this.cursorStrategy = cursorStrategy;
            this.defaultScrollCount = defaultScrollCount;
            this.defaultScrollPosition = defaultScrollPosition;
            this.sort = sort;
        }

        /**
         * Project results returned from the {@link QueryByExampleExecutor}
         * into the target {@code projectionType}. Projection types can be
         * either interfaces with property getters to expose or regular classes
         * outside the entity type hierarchy for DTO projections.
         * @param <P> the projection type
         * @param projectionType projection type
         * @return a new {@link FuzzyQueryByExampleDataFetcher.Builder} instance with all previously
         * configured options and {@code projectionType} applied
         */
        public <P> FuzzyQueryByExampleDataFetcher.Builder<T, P> projectAs(Class<P> projectionType) {
            Assert.notNull(projectionType, "Projection type must not be null");
            return new FuzzyQueryByExampleDataFetcher.Builder<>(this.executor, this.domainType, projectionType,
                    this.cursorStrategy, this.defaultScrollCount, this.defaultScrollPosition, this.sort);
        }

        /**
         * Configure strategy for decoding a cursor from a paginated request.
         * <p>By default, this is {@link ScrollPositionCursorStrategy} with
         * {@link CursorEncoder#base64()} encoding.
         * @param cursorStrategy the strategy to use
         * @return a new {@link FuzzyQueryByExampleDataFetcher.Builder} instance with all previously configured
         * options and {@code Sort} applied
         * @since 1.2.0
         */
        public FuzzyQueryByExampleDataFetcher.Builder<T, R> cursorStrategy(@Nullable CursorStrategy<ScrollPosition> cursorStrategy) {
            return new FuzzyQueryByExampleDataFetcher.Builder<>(this.executor, this.domainType, this.resultType,
                    cursorStrategy, this.defaultScrollCount, this.defaultScrollPosition, this.sort);
        }

        /**
         * Configure a default scroll count to use, and function to return a default
         * {@link ScrollPosition} for forward vs backward pagination.
         * <p>For offset scrolling, use {@link ScrollPosition#offset()} to scroll
         * from the beginning. Currently, it is not possible to go back from the end.
         * <p>For keyset scrolling, use {@link ScrollPosition#keyset()} to scroll
         * from the beginning, or {@link KeysetScrollPosition#reverse()} the same
         * to go back from the end.
         * <p>By default a count of 20 and {@link ScrollPosition#offset()} are used.
         * @param defaultCount the default count of elements in the subrange
         * @param defaultPosition function that returns a default {@code ScrollPosition}
         * @since 1.2.5
         */
        public FuzzyQueryByExampleDataFetcher.Builder<T, R> defaultScrollSubrange(
                int defaultCount, Function<Boolean, ScrollPosition> defaultPosition) {

            return new FuzzyQueryByExampleDataFetcher.Builder<>(this.executor, this.domainType,
                    this.resultType, this.cursorStrategy, defaultCount, defaultPosition, this.sort);
        }

        /**
         * Configure a {@link ScrollSubrange} to use when a paginated request does
         * not specify a cursor and/or a count of items.
         * <p>By default, this is {@link OffsetScrollPosition#offset()} with a
         * count of 20.
         * @param defaultSubrange the default scroll subrange
         * @return a new {@link FuzzyQueryByExampleDataFetcher.Builder} instance with all previously configured
         * options and {@code Sort} applied
         * @deprecated in favor of {@link #defaultScrollSubrange(int, Function)}
         */
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        @Deprecated(since = "1.2.5", forRemoval = true)
        public FuzzyQueryByExampleDataFetcher.Builder<T, R> defaultScrollSubrange(@Nullable ScrollSubrange defaultSubrange) {
            return new FuzzyQueryByExampleDataFetcher.Builder<>(this.executor, this.domainType,
                    this.resultType, this.cursorStrategy,
                    (defaultSubrange != null) ? defaultSubrange.count().getAsInt() : null,
                    (defaultSubrange != null) ? (forward) -> defaultSubrange.position().get() : null,
                    this.sort);
        }

        /**
         * Apply a {@link Sort} order.
         * @param sort the default sort order
         * @return a new {@link FuzzyQueryByExampleDataFetcher.Builder} instance with all previously configured
         * options and {@code Sort} applied
         */
        public FuzzyQueryByExampleDataFetcher.Builder<T, R> sortBy(Sort sort) {
            Assert.notNull(sort, "Sort must not be null");
            return new FuzzyQueryByExampleDataFetcher.Builder<>(this.executor, this.domainType, this.resultType,
                    this.cursorStrategy, this.defaultScrollCount, this.defaultScrollPosition, sort);
        }

        /**
         * Build a {@link DataFetcher} to fetch single object instances.
         */
        public DataFetcher<R> single() {
            return new FuzzyQueryByExampleDataFetcher.SingleEntityFetcher<>(this.executor, this.domainType, this.resultType, this.sort);
        }

        /**
         * Build a {@link DataFetcher} to fetch many object instances.
         */
        public DataFetcher<Iterable<R>> many() {
            return new FuzzyQueryByExampleDataFetcher.ManyEntityFetcher<>(this.executor, this.domainType, this.resultType, this.sort);
        }

        /**
         * Build a {@link DataFetcher} that scrolls and returns
         * {@link org.springframework.data.domain.Window}.
         * @since 1.2.0
         */
        public DataFetcher<Iterable<R>> scrollable() {
            return new FuzzyQueryByExampleDataFetcher.ScrollableEntityFetcher<>(
                    this.executor, this.domainType, this.resultType,
                    (this.cursorStrategy != null) ? this.cursorStrategy : RepositoryUtils.defaultCursorStrategy(),
                    (this.defaultScrollCount != null) ? this.defaultScrollCount : RepositoryUtils.defaultScrollCount(),
                    (this.defaultScrollPosition != null) ? this.defaultScrollPosition : RepositoryUtils.defaultScrollPosition(),
                    this.sort);
        }

    }

    /**
     * Callback interface that can be used to customize FuzzyQueryByExampleDataFetcher
     * {@link FuzzyQueryByExampleDataFetcher.Builder} to change its configuration.
     * <p>This is supported by {@link #autoRegistrationConfigurer(List, List)
     * Auto-registration}, which detects if a repository implements this
     * interface and applies it accordingly.
     *
     * @param <T> the domain type
     * @since 1.1.1
     */
    public interface QueryByExampleBuilderCustomizer<T> {

        /**
         * Callback to customize a {@link FuzzyQueryByExampleDataFetcher.Builder} instance.
         * @param builder builder to customize
         */
        FuzzyQueryByExampleDataFetcher.Builder<T, ?> customize(FuzzyQueryByExampleDataFetcher.Builder<T, ?> builder);

    }


    /**
     * Builder for a reactive Query by Example-based {@link DataFetcher}.
     * Note that builder instances are immutable and return a new instance of
     * the builder when calling configuration methods.
     *
     * @param <T> domain type
     * @param <R> result type
     */
    public static class ReactiveBuilder<T, R> {

        private final ReactiveQueryByExampleExecutor<T> executor;

        private final TypeInformation<T> domainType;

        private final Class<R> resultType;

        @Nullable
        private final CursorStrategy<ScrollPosition> cursorStrategy;

        @Nullable
        private final Integer defaultScrollCount;

        @Nullable
        private final Function<Boolean, ScrollPosition> defaultScrollPosition;

        private final Sort sort;

        @SuppressWarnings("unchecked")
        ReactiveBuilder(ReactiveQueryByExampleExecutor<T> executor, Class<R> domainType) {
            this(executor, TypeInformation.of((Class<T>) domainType), domainType, null, null, null, Sort.unsorted());
        }

        ReactiveBuilder(
                ReactiveQueryByExampleExecutor<T> executor, TypeInformation<T> domainType, Class<R> resultType,
                @Nullable CursorStrategy<ScrollPosition> cursorStrategy,
                @Nullable Integer defaultScrollCount, @Nullable Function<Boolean, ScrollPosition> defaultScrollPosition,
                Sort sort) {

            this.executor = executor;
            this.domainType = domainType;
            this.resultType = resultType;
            this.cursorStrategy = cursorStrategy;
            this.defaultScrollCount = defaultScrollCount;
            this.defaultScrollPosition = defaultScrollPosition;
            this.sort = sort;
        }

        /**
         * Project results returned from the {@link ReactiveQueryByExampleExecutor}
         * into the target {@code projectionType}. Projection types can be
         * either interfaces with property getters to expose or regular classes
         * outside the entity type hierarchy for DTO projections.
         * @param <P> projection type
         * @param projectionType projection type
         * @return a new {@link FuzzyQueryByExampleDataFetcher.ReactiveBuilder} instance with all previously
         * configured options and {@code projectionType} applied
         */
        public <P> FuzzyQueryByExampleDataFetcher.ReactiveBuilder<T, P> projectAs(Class<P> projectionType) {
            Assert.notNull(projectionType, "Projection type must not be null");
            return new FuzzyQueryByExampleDataFetcher.ReactiveBuilder<>(this.executor, this.domainType,
                    projectionType, this.cursorStrategy, this.defaultScrollCount, this.defaultScrollPosition, this.sort);
        }

        /**
         * Configure strategy for decoding a cursor from a paginated request.
         * <p>By default, this is {@link ScrollPositionCursorStrategy} with
         * {@link CursorEncoder#base64()} encoding.
         * @param cursorStrategy the strategy to use
         * @return a new {@link FuzzyQueryByExampleDataFetcher.Builder} instance with all previously configured
         * options and {@code Sort} applied
         * @since 1.2.0
         */
        public FuzzyQueryByExampleDataFetcher.ReactiveBuilder<T, R> cursorStrategy(@Nullable CursorStrategy<ScrollPosition> cursorStrategy) {
            return new FuzzyQueryByExampleDataFetcher.ReactiveBuilder<>(this.executor, this.domainType, this.resultType,
                    cursorStrategy, this.defaultScrollCount, this.defaultScrollPosition, this.sort);
        }

        /**
         * Configure a default scroll count to use, and function to return a default
         * {@link ScrollPosition} for forward vs backward pagination.
         * <p>For offset scrolling, use {@link ScrollPosition#offset()} to scroll
         * from the beginning. Currently, it is not possible to go back from the end.
         * <p>For keyset scrolling, use {@link ScrollPosition#keyset()} to scroll
         * from the beginning, or {@link KeysetScrollPosition#reverse()} the same
         * to go back from the end.
         * <p>By default a count of 20 and {@link ScrollPosition#offset()} are used.
         * @param defaultCount the default count of elements in the subrange
         * @param defaultPosition function that returns a default {@code ScrollPosition}
         * @since 1.2.5
         */
        public FuzzyQueryByExampleDataFetcher.ReactiveBuilder<T, R> defaultScrollSubrange(
                int defaultCount, Function<Boolean, ScrollPosition> defaultPosition) {

            return new FuzzyQueryByExampleDataFetcher.ReactiveBuilder<>(this.executor, this.domainType,
                    this.resultType, this.cursorStrategy, defaultCount, defaultPosition, this.sort);
        }

        /**
         * Configure a {@link ScrollSubrange} to use when a paginated request does
         * not specify a cursor and/or a count of items.
         * <p>By default, this is {@link OffsetScrollPosition#offset()} with a
         * count of 20.
         * @param defaultSubrange the default scroll subrange
         * @return a new {@link FuzzyQueryByExampleDataFetcher.Builder} instance with all previously configured
         * options and {@code Sort} applied
         * @deprecated in favor of {@link #defaultScrollSubrange(int, Function)}
         */
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        @Deprecated(since = "1.2.5", forRemoval = true)
        public FuzzyQueryByExampleDataFetcher.ReactiveBuilder<T, R> defaultScrollSubrange(@Nullable ScrollSubrange defaultSubrange) {
            return new FuzzyQueryByExampleDataFetcher.ReactiveBuilder<>(this.executor, this.domainType,
                    this.resultType, this.cursorStrategy,
                    (defaultSubrange != null) ? defaultSubrange.count().getAsInt() : null,
                    (defaultSubrange != null) ? (forward) -> defaultSubrange.position().get() : null,
                    this.sort);
        }

        /**
         * Apply a {@link Sort} order.
         * @param sort the default sort order
         * @return a new {@link FuzzyQueryByExampleDataFetcher.ReactiveBuilder} instance with all previously configured
         * options and {@code Sort} applied
         */
        public FuzzyQueryByExampleDataFetcher.ReactiveBuilder<T, R> sortBy(Sort sort) {
            Assert.notNull(sort, "Sort must not be null");
            return new FuzzyQueryByExampleDataFetcher.ReactiveBuilder<>(this.executor, this.domainType, this.resultType,
                    this.cursorStrategy, this.defaultScrollCount, this.defaultScrollPosition, sort);
        }

        /**
         * Build a {@link DataFetcher} to fetch single object instances.
         */
        public DataFetcher<Mono<R>> single() {
            return new FuzzyQueryByExampleDataFetcher.ReactiveSingleEntityFetcher<>(this.executor, this.domainType, this.resultType, this.sort);
        }

        /**
         * Build a {@link DataFetcher} to fetch many object instances.
         */
        public DataFetcher<Flux<R>> many() {
            return new FuzzyQueryByExampleDataFetcher.ReactiveManyEntityFetcher<>(this.executor, this.domainType, this.resultType, this.sort);
        }

        /**
         * Build a {@link DataFetcher} that scrolls and returns
         * {@link org.springframework.data.domain.Window}.
         * @since 1.2.0
         */
        public DataFetcher<Mono<Iterable<R>>> scrollable() {
            return new FuzzyQueryByExampleDataFetcher.ReactiveScrollableEntityFetcher<>(
                    this.executor, this.domainType, this.resultType,
                    (this.cursorStrategy != null) ? this.cursorStrategy : RepositoryUtils.defaultCursorStrategy(),
                    (this.defaultScrollCount != null) ? this.defaultScrollCount : RepositoryUtils.defaultScrollCount(),
                    (this.defaultScrollPosition != null) ? this.defaultScrollPosition : RepositoryUtils.defaultScrollPosition(),
                    this.sort);
        }

    }

    /**
     * Callback interface that can be used to customize FuzzyQueryByExampleDataFetcher
     * {@link FuzzyQueryByExampleDataFetcher.ReactiveBuilder} to change its configuration.
     * <p>This is supported by {@link #autoRegistrationConfigurer(List, List)
     * Auto-registration}, which detects if a repository implements this
     * interface and applies it accordingly.
     * @param <T> the domain type
     * @since 1.1.1
     */
    public interface ReactiveQueryByExampleBuilderCustomizer<T> {

        /**
         * Callback to customize a {@link FuzzyQueryByExampleDataFetcher.ReactiveBuilder} instance.
         * @param builder builder to customize
         */
        FuzzyQueryByExampleDataFetcher.ReactiveBuilder<T, ?> customize(FuzzyQueryByExampleDataFetcher.ReactiveBuilder<T, ?> builder);

    }


    private static class SingleEntityFetcher<T, R>
            extends FuzzyQueryByExampleDataFetcher<T> implements SelfDescribingDataFetcher<R> {

        private final QueryByExampleExecutor<T> executor;

        private final Class<R> resultType;

        private final Sort sort;

        SingleEntityFetcher(
                QueryByExampleExecutor<T> executor, TypeInformation<T> domainType, Class<R> resultType, Sort sort) {

            super(domainType);
            this.executor = executor;
            this.resultType = resultType;
            this.sort = sort;
        }

        @Override
        public ResolvableType getReturnType() {
            return ResolvableType.forClass(this.resultType);
        }

        @Override
        @SuppressWarnings({"ConstantConditions", "unchecked"})
        public R get(DataFetchingEnvironment env) throws BindException {
            return this.executor.findBy(buildExample(env), (query) -> {
                FluentQuery.FetchableFluentQuery<R> queryToUse = (FluentQuery.FetchableFluentQuery<R>) query;

                if (this.sort.isSorted()) {
                    queryToUse = queryToUse.sortBy(this.sort);
                }

                Class<R> resultType = this.resultType;
                if (requiresProjection(resultType)) {
                    queryToUse = queryToUse.as(resultType);
                }
                else {
                    queryToUse = queryToUse.project(buildPropertyPaths(env.getSelectionSet(), resultType));
                }

                return queryToUse.first();
            }).orElse(null);
        }

    }


    public static class ManyEntityFetcher<T, R>
            extends FuzzyQueryByExampleDataFetcher<T> implements SelfDescribingDataFetcher<Iterable<R>> {

        private final QueryByExampleExecutor<T> executor;

        private final Class<R> resultType;

        private final Sort sort;

        ManyEntityFetcher(
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


    private static class ScrollableEntityFetcher<T, R> extends FuzzyQueryByExampleDataFetcher.ManyEntityFetcher<T, R> {

        private final CursorStrategy<ScrollPosition> cursorStrategy;

        private final int defaultCount;

        private final Function<Boolean, ScrollPosition> defaultPosition;

        private final ResolvableType scrollableResultType;

        ScrollableEntityFetcher(
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
            ScrollSubrange range = RepositoryUtils.getScrollSubrange(env, this.cursorStrategy);
            int count = range.count().orElse(this.defaultCount);
            ScrollPosition position = (range.position().isPresent() ?
                    range.position().get() : this.defaultPosition.apply(range.forward()));
            return queryToUse.limit(count).scroll(position);
        }

    }


    private static class ReactiveSingleEntityFetcher<T, R>
            extends FuzzyQueryByExampleDataFetcher<T> implements SelfDescribingDataFetcher<Mono<R>> {

        private final ReactiveQueryByExampleExecutor<T> executor;

        private final Class<R> resultType;

        private final Sort sort;

        ReactiveSingleEntityFetcher(
                ReactiveQueryByExampleExecutor<T> executor, TypeInformation<T> domainType,
                Class<R> resultType, Sort sort) {

            super(domainType);
            this.executor = executor;
            this.resultType = resultType;
            this.sort = sort;
        }

        @Override
        public ResolvableType getReturnType() {
            return ResolvableType.forClassWithGenerics(Mono.class, this.resultType);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Mono<R> get(DataFetchingEnvironment env) throws BindException {
            return this.executor.findBy(buildExample(env), (query) -> {
                FluentQuery.ReactiveFluentQuery<R> queryToUse = (FluentQuery.ReactiveFluentQuery<R>) query;

                if (this.sort.isSorted()) {
                    queryToUse = queryToUse.sortBy(this.sort);
                }

                if (requiresProjection(this.resultType)) {
                    queryToUse = queryToUse.as(this.resultType);
                }
                else {
                    queryToUse = queryToUse.project(buildPropertyPaths(env.getSelectionSet(), this.resultType));
                }

                return queryToUse.first();
            });
        }

    }


    private static class ReactiveManyEntityFetcher<T, R>
            extends FuzzyQueryByExampleDataFetcher<T> implements SelfDescribingDataFetcher<Flux<R>> {

        private final ReactiveQueryByExampleExecutor<T> executor;

        private final Class<R> resultType;

        private final Sort sort;

        ReactiveManyEntityFetcher(
                ReactiveQueryByExampleExecutor<T> executor, TypeInformation<T> domainType,
                Class<R> resultType, Sort sort) {

            super(domainType);
            this.executor = executor;
            this.resultType = resultType;
            this.sort = sort;
        }

        @Override
        public ResolvableType getReturnType() {
            return ResolvableType.forClassWithGenerics(Flux.class, this.resultType);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Flux<R> get(DataFetchingEnvironment env) throws BindException {
            return this.executor.findBy(buildExample(env), (query) -> {
                FluentQuery.ReactiveFluentQuery<R> queryToUse = (FluentQuery.ReactiveFluentQuery<R>) query;

                if (this.sort.isSorted()) {
                    queryToUse = queryToUse.sortBy(this.sort);
                }

                if (requiresProjection(this.resultType)) {
                    queryToUse = queryToUse.as(this.resultType);
                }
                else {
                    queryToUse = queryToUse.project(buildPropertyPaths(env.getSelectionSet(), this.resultType));
                }

                return queryToUse.all();
            });
        }

    }


    private static class ReactiveScrollableEntityFetcher<T, R>
            extends FuzzyQueryByExampleDataFetcher<T> implements SelfDescribingDataFetcher<Mono<Iterable<R>>> {

        private final ReactiveQueryByExampleExecutor<T> executor;

        private final Class<R> resultType;

        private final ResolvableType scrollableResultType;

        private final CursorStrategy<ScrollPosition> cursorStrategy;

        private final int defaultCount;

        private final Function<Boolean, ScrollPosition> defaultPosition;

        private final Sort sort;

        ReactiveScrollableEntityFetcher(
                ReactiveQueryByExampleExecutor<T> executor, TypeInformation<T> domainType, Class<R> resultType,
                CursorStrategy<ScrollPosition> cursorStrategy,
                int defaultCount,
                Function<Boolean, ScrollPosition> defaultPosition,
                Sort sort) {

            super(domainType);

            Assert.notNull(cursorStrategy, "CursorStrategy is required");
            Assert.notNull(defaultPosition, "'defaultPosition' is required");

            this.executor = executor;
            this.resultType = resultType;
            this.scrollableResultType = ResolvableType.forClassWithGenerics(Iterable.class, resultType);
            this.cursorStrategy = cursorStrategy;
            this.defaultCount = defaultCount;
            this.defaultPosition = defaultPosition;
            this.sort = sort;
        }

        @Override
        public ResolvableType getReturnType() {
            return ResolvableType.forClassWithGenerics(Mono.class, this.scrollableResultType);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Mono<Iterable<R>> get(DataFetchingEnvironment env) throws BindException {
            return this.executor.findBy(buildExample(env), (query) -> {
                FluentQuery.ReactiveFluentQuery<R> queryToUse = (FluentQuery.ReactiveFluentQuery<R>) query;

                if (this.sort.isSorted()) {
                    queryToUse = queryToUse.sortBy(this.sort);
                }

                if (requiresProjection(this.resultType)) {
                    queryToUse = queryToUse.as(this.resultType);
                }
                else {
                    queryToUse = queryToUse.project(buildPropertyPaths(env.getSelectionSet(), this.resultType));
                }

                ScrollSubrange range = RepositoryUtils.getScrollSubrange(env, this.cursorStrategy);
                int count = range.count().orElse(this.defaultCount);
                ScrollPosition position = (range.position().isPresent() ?
                        range.position().get() : this.defaultPosition.apply(range.forward()));
                return queryToUse.limit(count).scroll(position).map(Function.identity());
            });
        }

    }

}

