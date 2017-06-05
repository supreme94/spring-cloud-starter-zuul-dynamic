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

package io.github.liangp.zuul.dynamicroute.annotation;

import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import io.github.liangp.zuul.dynamicroute.configuration.ZuulDynamicProxyConfiguration;
import io.github.liangp.zuul.dynamicroute.store.ZuulRouteStore;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Enables the Zuul Proxy with list of routes being provided from configured
 * {@link ZuulRouteStore} instance, besides that this pretty much resembles the
 * standard {@link org.springframework.cloud.netflix.zuul.EnableZuulProxy}.
 *
 * @author Jakub Narloch
 */
@EnableCircuitBreaker
@EnableDiscoveryClient
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ZuulDynamicProxyConfiguration.class)
public @interface EnableZuulDynamicProxy {
}