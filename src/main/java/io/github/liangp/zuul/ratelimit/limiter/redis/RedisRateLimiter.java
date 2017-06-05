package io.github.liangp.zuul.ratelimit.limiter.redis;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

import io.github.liangp.zuul.ratelimit.Rate;
import io.github.liangp.zuul.ratelimit.RateLimitProperties.Policy;
import io.github.liangp.zuul.ratelimit.limiter.RateLimiter;

public class RedisRateLimiter implements RateLimiter {
    private final RedisTemplate template;

    public RedisRateLimiter(final RedisTemplate template) {
        Assert.notNull(template, "RedisTemplate cannot be null");
        this.template = template;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Rate consume(final Policy policy, final String key) {
        final Long limit = policy.getLimit();
        final Long refreshInterval = policy.getRefreshInterval();
        final Long current = this.template.boundValueOps(key).increment(1L);
        Long expire = this.template.getExpire(key);
        if (expire == null || expire == -1) {
            this.template.expire(key, refreshInterval, SECONDS);
            expire = refreshInterval;
        }
        return new Rate(limit, Math.max(-1, limit - current), SECONDS.toMillis(expire));
    }
}
