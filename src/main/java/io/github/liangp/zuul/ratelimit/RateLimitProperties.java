package io.github.liangp.zuul.ratelimit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ConfigurationProperties(RateLimitProperties.PREFIX)
public class RateLimitProperties {

    public static final String PREFIX = "zuul.ratelimit";

    private Map<String, Policy> policies = new LinkedHashMap<>();
    private boolean enabled;
    private boolean behindProxy;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Policy {
        private Long refreshInterval = 60L;
        private Long limit;
        private List<Type> type = new ArrayList<>();

        public enum Type {
            ORIGIN, USER, URL
        }

    }
}