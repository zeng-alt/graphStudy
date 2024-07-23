package com.zjj.graphstudy.resolver.subscription;

import com.zjj.graphstudy.event.StockPrice;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月17日 22:49
 * @version 1.0
 */
@Controller
public class StockPriceHandler {

    @SubscriptionMapping("stockPrice")
    public Flux<StockPrice> stockPrice(@Argument String symbol) {
        Random random = new Random();
        return Flux.interval(Duration.ofSeconds(1))
                .map(num -> new StockPrice(symbol,
                        random.nextDouble(), LocalDateTime.now()));
    }
}
