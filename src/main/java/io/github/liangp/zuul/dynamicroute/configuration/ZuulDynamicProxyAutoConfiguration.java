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

package io.github.liangp.zuul.dynamicroute.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;

import io.github.liangp.zuul.dynamicroute.store.JdbcZuulRouteStore;
import io.github.liangp.zuul.dynamicroute.store.ZuulRouteStore;

@Configuration
@ConditionalOnProperty(value = "zuul.store.jdbc.enabled", havingValue = "true", matchIfMissing = false)
public class ZuulDynamicProxyAutoConfiguration {
	
	@Bean
	@ConditionalOnMissingBean
	public ZuulRouteStore mysqlZuulRouteStore(JdbcOperations jdbcOperations) {
		return new JdbcZuulRouteStore(jdbcOperations);
	}
	
}