package com.sania.proxy.routing;

import com.sania.proxy.config.ProxyProperties;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Optional;

@Component
public class RouteResolver {
    private final ProxyProperties properties;

    public RouteResolver(ProxyProperties properties) {
        this.properties = properties;
    }

    public Optional<ProxyProperties.Route> resolve(String path) {
        return properties.getRoutes().stream()
                .filter(route -> {
                    String prefix = route.getPathPrefix();
                    return prefix != null
                            && (path.equals(prefix) || path.startsWith(prefix + "/"));
                })
                .max(Comparator.comparingInt(route -> route.getPathPrefix().length()));
    }
}
