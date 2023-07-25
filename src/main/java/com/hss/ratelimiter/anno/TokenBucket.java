package com.hss.ratelimiter.anno;

import com.sun.istack.internal.NotNull;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface TokenBucket {

    @NotNull
    Limiter limiter();

    // 最大访问次数的时间长度
    long len();

    // 时间单位
    TimeUnit unit() default TimeUnit.SECONDS;
}
