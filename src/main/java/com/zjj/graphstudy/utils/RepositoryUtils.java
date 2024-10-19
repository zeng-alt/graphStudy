package com.zjj.graphstudy.utils;

import com.zjj.graphstudy.dao.BaseRepository;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.DefaultRepositoryMetadata;
import org.springframework.graphql.data.GraphQlRepository;
import org.springframework.graphql.data.pagination.CursorEncoder;
import org.springframework.graphql.data.pagination.CursorStrategy;
import org.springframework.graphql.data.query.ScrollPositionCursorStrategy;
import org.springframework.graphql.data.query.ScrollSubrange;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年08月30日 09:37
 */
public class RepositoryUtils {

    private RepositoryUtils() {

    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getDomainType(Object executor) {
        return (Class<T>) getRepositoryMetadata(executor).getDomainType();
    }

    public static RepositoryMetadata getRepositoryMetadata(Object executor) {
        Assert.isInstanceOf(Repository.class, executor);

        Type[] genericInterfaces = executor.getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            Class<?> rawClass = ResolvableType.forType(genericInterface).getRawClass();
            if (rawClass == null || MergedAnnotations.from(rawClass).isPresent(NoRepositoryBean.class)) {
                continue;
            }
            if (Repository.class.isAssignableFrom(rawClass)) {
                return new DefaultRepositoryMetadata(rawClass);
            }
        }

        throw new IllegalArgumentException(
                String.format("Cannot resolve repository interface from %s", executor));
    }

    @Nullable
    public static String getGraphQlTypeName(Object repository) {
        GraphQlRepository annotation =
                AnnotatedElementUtils.findMergedAnnotation(repository.getClass(), GraphQlRepository.class);

        if (annotation == null) {
            return null;
        }

        return (StringUtils.hasText(annotation.typeName()) ?
                annotation.typeName() : RepositoryUtils.getDomainType(repository).getSimpleName());
    }

    @Nullable
    public static Class<?> getEntityType(Object repository) {
        ResolvableType resolvableType = ResolvableType.forInstance(repository);
        return resolvableType.as(BaseRepository.class).resolveGeneric(0);
    }


    public static CursorStrategy<ScrollPosition> defaultCursorStrategy() {
        return CursorStrategy.withEncoder(new ScrollPositionCursorStrategy(), CursorEncoder.base64());
    }

    public static int defaultScrollCount() {
        return 20;
    }

    public static Function<Boolean, ScrollPosition> defaultScrollPosition() {
        return (forward) -> ScrollPosition.offset();
    }

    public static ScrollSubrange getScrollSubrange(
            DataFetchingEnvironment env, CursorStrategy<ScrollPosition> cursorStrategy) {

        boolean forward = true;
        String cursor = env.getArgument("after");
        Integer count = env.getArgument("first");
        if (cursor == null && count == null) {
            cursor = env.getArgument("before");
            count = env.getArgument("last");
            if (cursor != null || count != null) {
                forward = false;
            }
        }
        ScrollPosition pos = (cursor != null) ? cursorStrategy.fromCursor(cursor) : null;
        return ScrollSubrange.create(pos, count, forward);
    }


}
