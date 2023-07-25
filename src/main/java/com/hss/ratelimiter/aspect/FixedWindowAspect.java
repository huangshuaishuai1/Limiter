package com.hss.ratelimiter.aspect;

import com.hss.ratelimiter.anno.FixedWindow;
import com.hss.ratelimiter.anno.Limiter;
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

@Aspect
@Component
@Slf4j
public class FixedWindowAspect {

    private StringRedisTemplate stringRedisTemplate;
    @Resource(name = "fixedWindowLimiter")
    private RedisScript<Long> fixedWindowLimiter;

    @Autowired
    public FixedWindowAspect(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Before("@annotation(com.hss.ratelimiter.anno.FixedWindow)")
    public void before(JoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        FixedWindow fixedWindow = method.getAnnotation(FixedWindow.class);
        Limiter limiter = fixedWindow.limiter();
        // 接口
        String res = limiter.res();
        // 需要限流的单位（用户/ip）
        String redisKey = LimiterUtils.getRedisKey(res);
        Long execute = stringRedisTemplate.execute(fixedWindowLimiter, Collections.singletonList(redisKey), String.valueOf(limiter.permits()), String.valueOf(fixedWindow.len()));
        // 如果存在这个key
        if (execute == 0) {
            log.debug(limiter.msg());
            throw new LimiterException(limiter.msg(), "123001");
        }else if (execute == 1) {
            log.info("创建固定窗口限流，key:{},当前请求次数:{},最高请求次数:{}",redisKey,1,limiter.permits());

        }else {
            log.info("当前请求次数:{},最高请求次数:{}",execute,limiter.permits());
        }

    }
}
