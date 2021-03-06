package io.github.liangp.zuul.ratelimit.filters;


import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UrlPathHelper;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import io.github.liangp.zuul.ratelimit.Rate;
import io.github.liangp.zuul.ratelimit.RateLimitProperties;
import io.github.liangp.zuul.ratelimit.RateLimitProperties.Policy.Type;
import io.github.liangp.zuul.ratelimit.limiter.RateLimiter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RateLimitFilter extends ZuulFilter {

    private static final UrlPathHelper URL_PATH_HELPER = new UrlPathHelper();
    private static final String X_FORWARDED_FOR = "X-FORWARDED-FOR";
    private static final String ANONYMOUS = "anonymous";

    private final RateLimiter limiter;
    private final RateLimitProperties properties;
    private final RouteLocator routeLocator;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return -1;
    }

    @Override
    public boolean shouldFilter() {
        return this.properties.isEnabled() && policy() != null;
    }

    public Object run() {
        final RequestContext ctx = RequestContext.getCurrentContext();
        final HttpServletResponse response = ctx.getResponse();
        final HttpServletRequest request = ctx.getRequest();
        Optional.ofNullable(policy()).ifPresent(policy -> {

            final Rate rate = this.limiter.consume(policy, key(request, policy.getType()));
            response.setHeader(Headers.LIMIT, rate.getLimit().toString());
            response.setHeader(Headers.REMAINING, String.valueOf(Math.max(rate.getRemaining(), 0)));
            response.setHeader(Headers.RESET, rate.getReset().toString());
            if (rate.getRemaining() < 0) {
                ctx.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
                ctx.put("rateLimitExceeded", "true");
                throw new RuntimeException(HttpStatus.TOO_MANY_REQUESTS.toString());
            }
        });
        return null;
    }

    /**
     * Get the requestURI from request.
     *
     * @return The request URI
     */
    private String requestURI() {
        return URL_PATH_HELPER.getPathWithinApplication(RequestContext.getCurrentContext().getRequest());
    }

    /**
     * Return the requestedContext from request.
     *
     * @return The requestedContext
     */
    private Route route() {
        return this.routeLocator.getMatchingRoute(this.requestURI());
    }

    private RateLimitProperties.Policy policy() {
        return (route() != null) ? this.properties.getPolicies().get(route().getId()) : null;
    }

    private String key(final HttpServletRequest request, final Type type) {
        final Route route = route();
        final StringBuilder builder = new StringBuilder(route.getId());
        if (type == Type.URL) {
            builder.append(":").append(route.getPath());
        }
        if (type == Type.ORIGIN) {
            builder.append(":").append(getRemoteAddr(request));
        }
        if (type == Type.USER) {
            builder.append(":").append((request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() :
                    ANONYMOUS);
        }
        return builder.toString();
    }

    private String getRemoteAddr(final HttpServletRequest request) {
        final String remoteAddr;
        if (this.properties.isBehindProxy() && request.getHeader(X_FORWARDED_FOR) != null) {
            remoteAddr = request.getHeader(X_FORWARDED_FOR);
        } else {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }

    interface Headers {
        String LIMIT = "X-RateLimit-Limit";
        String REMAINING = "X-RateLimit-Remaining";
        String RESET = "X-RateLimit-Reset";
    }
}
