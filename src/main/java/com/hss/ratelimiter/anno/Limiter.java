package com.hss.ratelimiter.anno;

import com.sun.istack.internal.NotNull;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Limiter {

    // 接口路径
    @NotNull
    String res() default "";

    // 最大访问次数
    @NotNull
    int permits();

    // 提示信息
    String msg() default "请不要频繁点击";
}
