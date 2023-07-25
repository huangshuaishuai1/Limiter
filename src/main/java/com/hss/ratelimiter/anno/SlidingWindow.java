package com.hss.ratelimiter.anno;


import com.sun.istack.internal.NotNull;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 滑动窗口限流
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface SlidingWindow {
    @NotNull
    Limiter limiter();

    // 窗口的时间长度
    long len();

    // 时间单位
    TimeUnit unit() default TimeUnit.SECONDS;
}
