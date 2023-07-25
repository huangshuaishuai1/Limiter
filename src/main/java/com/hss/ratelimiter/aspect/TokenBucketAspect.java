package com.hss.ratelimiter.aspect;


import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;
import com.hss.ratelimiter.anno.Limiter;
import com.hss.ratelimiter.anno.TokenBucket;
import com.hss.ratelimiter.exception.LimiterException;
import com.hss.ratelimiter.utils.LimiterUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class TokenBucketAspect {


    private final Map<String, RateLimiter> limitMap = Maps.newConcurrentMap();

    @Before("@annotation(com.hss.ratelimiter.anno.TokenBucket)")
    public void before(JoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        TokenBucket tokenBucket = method.getAnnotation(TokenBucket.class);
        Limiter limiter = tokenBucket.limiter();

        // 接口
        String res = limiter.res();
        // 需要限流的单位（用户/ip）
        String key = LimiterUtils.getRedisKey(res);
        long len = tokenBucket.len();  // 时间
        TimeUnit unit = tokenBucket.unit(); // 单位
        // 转换为s
        if (tokenBucket.unit() == TimeUnit.MILLISECONDS) {
            len /= 1000;
        }
        double permitPreSecond = (double) limiter.permits() / len;
        RateLimiter rateLimiter;

        if (!limitMap.containsKey(key)) {
            rateLimiter = RateLimiter.create(permitPreSecond);
            limitMap.put(key,rateLimiter);
            log.info("创建令牌桶限流，key:{},令盘产生速率：每秒{}个",key,permitPreSecond);
        }
        rateLimiter = limitMap.get(key);

        boolean acquire = rateLimiter.tryAcquire(1, 100, TimeUnit.MILLISECONDS);
        if (!acquire) {
            log.debug(limiter.msg());
            throw new LimiterException(limiter.msg(), "123001");
        }else {
            log.info("放行....");
        }
    }
}
