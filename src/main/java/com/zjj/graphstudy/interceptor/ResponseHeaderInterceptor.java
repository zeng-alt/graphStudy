package com.zjj.graphstudy.interceptor;

import com.zjj.graphstudy.dto.Result;
import graphql.ExecutionResult;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月15日 22:51
 * @version 1.0
 */
@Component
public class ResponseHeaderInterceptor implements WebGraphQlInterceptor {

    @Override
    public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
        return chain.next(request).doOnNext((response) -> {
            String value = response.getExecutionInput().getGraphQLContext().get("cookieName");
            ResponseCookie cookie = ResponseCookie.from("cookieName", value).build();
            HttpHeaders responseHeaders = response.getResponseHeaders();
            ExecutionResult executionResult = response.getExecutionResult();
            executionResult.transform(new Consumer<ExecutionResult.Builder<?>>() {
                @Override
                public void accept(ExecutionResult.Builder<?> builder) {
                    ExecutionResult build = builder.build();
                    Result result = new Result();
                    result.setCode(200);
                    result.setData(build.getData());
                    builder.data(result);
                }
            });
            response.getResponseHeaders().add(HttpHeaders.SET_COOKIE, cookie.toString());
        });
    }
}
