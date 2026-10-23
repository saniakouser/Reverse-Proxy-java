package com.sania.proxy.routing;

import com.sania.proxy.config.ProxyProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpstreamSelectorTest {
    @Test
    void shouldSelectUpstreamsInRoundRobinOrder() {
        ProxyProperties.Route route = new ProxyProperties.Route();
        route.setId("users");
        route.setUpstreams(List.of("http://localhost:8081", "http://localhost:8082"));

        UpstreamSelector selector = new UpstreamSelector();
        assertEquals("http://localhost:8081", selector.select(route));
        assertEquals("http://localhost:8082", selector.select(route));
        assertEquals("http://localhost:8081", selector.select(route));
    }

    @Test
    void shouldRejectEmptyUpstreams() {
        ProxyProperties.Route route = new ProxyProperties.Route();
        route.setId("empty");
        route.setUpstreams(List.of());
        assertThrows(IllegalStateException.class,
                () -> new UpstreamSelector().select(route));
    }
}
