package com.zjj.graphstudy.interceptor;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphQLError;
import graphql.Internal;

import java.util.*;
import java.util.function.Consumer;

import static graphql.collect.ImmutableKit.map;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月15日 21:37
 * @version 1.0
 */
@Internal
public class CustomizeGraphqlResult extends ExecutionResultImpl {


    public CustomizeGraphqlResult(GraphQLError error) {
        super(error);
    }

    public CustomizeGraphqlResult(List<? extends GraphQLError> errors) {
        super(errors);
    }

    public CustomizeGraphqlResult(Object data, List<? extends GraphQLError> errors) {
        super(data, errors);
    }

    public CustomizeGraphqlResult(Object data, List<? extends GraphQLError> errors, Map<Object, Object> extensions) {
        super(data, errors, extensions);
    }

    public CustomizeGraphqlResult(ExecutionResultImpl other) {
        super(other);
    }



    public Map<String, Object> toSpecification() {
        Map<String, Object> result = new LinkedHashMap<>();
        if (getErrors() != null && !getErrors().isEmpty()) {
            result.put("message", map(getErrors(), GraphQLError::toSpecification));
            result.put("code", 500);
        }
        if (isDataPresent()) {
            result.put("data", getData());
            result.put("code", 200);
        }
        if (getExtensions() != null) {
            result.put("extensions", getExtensions());
        }
        return result;
    }


    public ExecutionResult transform(Consumer<ExecutionResult.Builder<?>> builderConsumer) {
        ExecutionResult.Builder<?> builder = new Builder<>().from(this);
        builderConsumer.accept(builder);
        return builder.build();
    }

    public static class Builder<T extends Builder<T>> implements ExecutionResult.Builder<T> {
        private boolean dataPresent;
        private Object data;
        private List<GraphQLError> errors = new ArrayList<>();
        private Map<Object, Object> extensions;

        @Override
        public T from(ExecutionResult executionResult) {
            dataPresent = executionResult.isDataPresent();
            data = executionResult.getData();
            errors = new ArrayList<>(executionResult.getErrors());
            extensions = executionResult.getExtensions();
            return (T) this;
        }

        @Override
        public T data(Object data) {
            dataPresent = true;
            this.data = data;
            return (T) this;
        }

        @Override
        public T errors(List<GraphQLError> errors) {
            this.errors = errors;
            return (T) this;
        }

        @Override
        public T addErrors(List<GraphQLError> errors) {
            this.errors.addAll(errors);
            return (T) this;
        }

        @Override
        public T addError(GraphQLError error) {
            this.errors.add(error);
            return (T) this;
        }

        @Override
        public T extensions(Map<Object, Object> extensions) {
            this.extensions = extensions;
            return (T) this;
        }

        @Override
        public T addExtension(String key, Object value) {
            this.extensions = (this.extensions == null ? new LinkedHashMap<>() : this.extensions);
            this.extensions.put(key, value);
            return (T) this;
        }

        @Override
        public ExecutionResult build() {
            CustomizeGraphqlResult customizeGraphqlResult = new CustomizeGraphqlResult(data, errors, extensions);

            return customizeGraphqlResult;
        }
    }
}
