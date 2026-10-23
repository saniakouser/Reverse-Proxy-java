package com.sania.proxy.routing;

import com.sania.proxy.config.ProxyProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class UpstreamSelector {
    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public String select(ProxyProperties.Route route) {
        List<String> upstreams = route.getUpstreams();
        if (upstreams == null || upstreams.isEmpty()) {
            throw new IllegalStateException(
                    "No upstreams configured for route: " + route.getId());
        }

        AtomicInteger counter = counters.computeIfAbsent(
                route.getId(), key -> new AtomicInteger(0));
        int index = Math.floorMod(counter.getAndIncrement(), upstreams.size());
        return upstreams.get(index);
    }
}
