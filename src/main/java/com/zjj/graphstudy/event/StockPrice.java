package com.zjj.graphstudy.event;

import java.time.LocalDateTime;

/**
 * @author zengJiaJun
 * @crateTime 2024年07月17日 22:46
 * @version 1.0
 */
public record StockPrice(String symbol, double price, LocalDateTime timestamp) {}
