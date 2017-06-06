package io.github.liangp.zuul.dynamicroute.store.redis;

import java.util.List;

import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.data.redis.core.RedisTemplate;

import io.github.liangp.zuul.dynamicroute.store.ZuulRouteStore;

public class RedisZuulRouteStore implements ZuulRouteStore{
	
	private RedisTemplate<String, String> redisTemplate;
	
	@Override
	public List<ZuulRoute> getRoutes() {
		// TODO Auto-generated method stub
		return null;
	}

}
