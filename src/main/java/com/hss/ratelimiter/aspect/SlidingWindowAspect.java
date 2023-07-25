package com.hss.ratelimiter.aspect;

import com.hss.ratelimiter.anno.Limiter;
import com.hss.ratelimiter.anno.SlidingWindow;
import com.hss.ratelimiter.exception.LimiterException;
import com.hss.ratelimiter.utils.LimiterUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Slf4j
public class SlidingWindowAspect {

    private StringRedisTemplate stringRedisTemplate;

    @Resource(name = "slidingWindowLimiter")
    private RedisScript<Long> slidingWindowLimiter;

    @Autowired
    public SlidingWindowAspect(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Before("@annotation(com.hss.ratelimiter.anno.SlidingWindow)")
    public void before(JoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SlidingWindow slidingWindow = method.getAnnotation(SlidingWindow.class);
        Limiter limiter = slidingWindow.limiter();

        // 接口
        String res = limiter.res();
        // 需要限流的单位（用户/ip）
        String redisKey = LimiterUtils.getRedisKey(res);
        int permits = limiter.permits();
        long len = slidingWindow.len();
        TimeUnit unit = slidingWindow.unit();
        long now = System.currentTimeMillis();
        if (unit == TimeUnit.SECONDS) {
            now = now / 1000;
        }
        Long isSuccess = (Long) stringRedisTemplate.execute(slidingWindowLimiter, Collections.singletonList(redisKey), String.valueOf(permits), String.valueOf(len), String.valueOf(now));
        if (isSuccess == 1) {
            log.info("创建固定窗口限流，key:{},当前请求次数:{},最高请求次数:{}", redisKey, 1, limiter.permits());

        } else if (isSuccess > 1) {
            log.info("当前请求次数:{},最高请求次数:{}",isSuccess,limiter.permits());
        } else {
            throw new LimiterException(limiter.msg(), "123001");
        }
    }
}
