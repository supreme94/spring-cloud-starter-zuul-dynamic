/**
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.liangp.zuul;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcOperations;

import io.github.liangp.zuul.dynamicroute.routelocator.StoreRefreshableRouteLocator;
import io.github.liangp.zuul.dynamicroute.store.ZuulRouteStore;
import io.github.liangp.zuul.dynamicroute.store.jdbc.JdbcZuulRouteStore;
import io.github.liangp.zuul.ratelimit.RateLimitProperties;
import io.github.liangp.zuul.ratelimit.filters.RateLimitFilter;
import io.github.liangp.zuul.ratelimit.limiter.RateLimiter;
import io.github.liangp.zuul.ratelimit.limiter.redis.RedisRateLimiter;

@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
public class ZuulDynamicProxyAutoConfiguration {

	@Bean
	@ConditionalOnProperty(value = "zuul.store.jdbc.enabled", havingValue = "true", matchIfMissing = false)
	public ZuulRouteStore mysqlZuulRouteStore(JdbcOperations jdbcOperations) {
		return new JdbcZuulRouteStore(jdbcOperations);
	}

	@Bean
	@ConditionalOnProperty(value = "zuul.store.jdbc.enabled", havingValue = "true", matchIfMissing = false)
	public DiscoveryClientRouteLocator discoveryRouteLocator(ZuulRouteStore zuulRouteStore,DiscoveryClient discovery,ZuulProperties zuulProperties,ServerProperties server) {
		return new StoreRefreshableRouteLocator(server.getServletPath(), discovery, zuulProperties, zuulRouteStore);
	}

	@Bean
	@ConditionalOnProperty(prefix = RateLimitProperties.PREFIX, name = "enabled", havingValue = "true")
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