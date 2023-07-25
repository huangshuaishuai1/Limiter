package com.hss.ratelimiter.controller;

import com.hss.ratelimiter.anno.Limiter;
import com.hss.ratelimiter.anno.TokenBucket;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/do")
    @TokenBucket(limiter = @Limiter(res = "/do",permits = 10), len = 10000, unit = TimeUnit.MILLISECONDS)
    public String doSomething() {
        return "do someting...";
    }
}
