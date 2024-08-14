package com.zjj.graphstudy.filter;

import com.alibaba.fastjson2.JSON;
import com.zjj.graphstudy.cache.CacheConfig;
import com.zjj.graphstudy.dto.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author zengJiaJun
 * @version 1.0
 * @crateTime 2024年08月08日 08:16
 */
@Component
public class JwtRenewFilter extends OncePerRequestFilter {
    @Resource
    private CacheManager cacheManager;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("token");
        if (StringUtils.isNotBlank(token)) {
            Claims mysecret = Jwts.parser().setSigningKey("mysecret").parseClaimsJws(token).getBody();
            String uuid = (String) mysecret.get("user");
            Cache cache = cacheManager.getCache(CacheConfig.Caches.USER.name());
            UserDetails userCache = cache.get(uuid, UserDetails.class);
            if (userCache != null) {
                cache.put(uuid, userCache);
            }
        }
        filterChain.doFilter(request, response);
    }
}
