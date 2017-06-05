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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.zuul.ZuulProxyConfiguration;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.context.annotation.Configuration;

import io.github.liangp.zuul.dynamicroute.routelocator.StoreRefreshableRouteLocator;
import io.github.liangp.zuul.dynamicroute.store.ZuulRouteStore;

/**
 * Registers a {@link org.springframework.cloud.netflix.zuul.filters.RouteLocator} that is being populated through
 * external store.
 *
 * @author Jakub Narloch
 */
@Configuration
public class ZuulDynamicProxyConfiguration extends ZuulProxyConfiguration {

  @Autowired
  private ZuulRouteStore zuulRouteStore;

  @Autowired
  private DiscoveryClient discovery;

  @Autowired
  private ZuulProperties zuulProperties;

  @Autowired
  private ServerProperties server;

  @Override
  public DiscoveryClientRouteLocator discoveryRouteLocator() {
    return new StoreRefreshableRouteLocator(server.getServletPath(), discovery, zuulProperties, zuulRouteStore);
  }
}
