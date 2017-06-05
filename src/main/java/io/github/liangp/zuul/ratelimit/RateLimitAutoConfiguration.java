package io.github.liangp.zuul.ratelimit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import io.github.liangp.zuul.ratelimit.filters.RateLimitFilter;
import io.github.liangp.zuul.ratelimit.limiter.RateLimiter;
import io.github.liangp.zuul.ratelimit.limiter.redis.RedisRateLimiter;

@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
@ConditionalOnProperty(prefix = RateLimitProperties.PREFIX, name = "enabled", havingValue = "true")
public class RateLimitAutoConfiguration {

    @Bean
    public RateLimitFilter rateLimiterFilter(RateLimiter rateLimiter, RateLimitProperties rateLimitProperties,
                                             RouteLocator routeLocator) {
        return new RateLimitFilter(rateLimiter, rateLimitProperties, routeLocator);
    }

    @ConditionalOnClass(RedisTemplate.class)
    public static class RedisConfiguration {
        @Bean
        public StringRedisTemplate redisTemplate(RedisConnectionFactory cf) {
            return new StringRedisTemplate(cf);
        }

        @Bean
        public RateLimiter redisRateLimiter(RedisTemplate redisTemplate) {
            return new RedisRateLimiter(redisTemplate);
        }
    }

}
