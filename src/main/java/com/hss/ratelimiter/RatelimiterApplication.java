package com.hss.ratelimiter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@SpringBootApplication
public class RatelimiterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RatelimiterApplication.class, args);
    }


    /**
     * 解析lua脚本
     * @return
     */
    @Bean("slidingWindowLimiter")
    public DefaultRedisScript<Long> slidingWindowLimiter() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/slidingWindow.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    /**
     * 解析lua脚本
     * @return
     */
    @Bean("fixedWindowLimiter")
    public DefaultRedisScript<Long> fixedWindowLimiter() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/fixedWindow.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }
}
