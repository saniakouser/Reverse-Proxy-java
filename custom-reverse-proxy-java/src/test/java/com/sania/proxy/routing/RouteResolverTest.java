package com.sania.proxy.routing;

import com.sania.proxy.config.ProxyProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteResolverTest {
    @Test
    void shouldSelectLongestMatchingPrefix() {
        ProxyProperties properties = new ProxyProperties();

        ProxyProperties.Route general = new ProxyProperties.Route();
        general.setId("api");
        general.setPathPrefix("/api");
        general.setUpstreams(List.of("http://localhost:8081"));

        ProxyProperties.Route users = new ProxyProperties.Route();
        users.setId("users");
        users.setPathPrefix("/api/users");
        users.setUpstreams(List.of("http://localhost:8082"));

        properties.setRoutes(List.of(general, users));
        RouteResolver resolver = new RouteResolver(properties);

        assertEquals("users", resolver.resolve("/api/users/42").orElseThrow().getId());
    }

    @Test
    void shouldReturnEmptyForUnknownRoute() {
        RouteResolver resolver = new RouteResolver(new ProxyProperties());
        assertTrue(resolver.resolve("/unknown").isEmpty());
    }

    @Test
    void shouldNotMatchSimilarButDifferentPrefix() {
        ProxyProperties properties = new ProxyProperties();
        ProxyProperties.Route route = new ProxyProperties.Route();
        route.setId("users");
        route.setPathPrefix("/users");
        route.setUpstreams(List.of("http://localhost:8081"));
        properties.setRoutes(List.of(route));

        assertTrue(new RouteResolver(properties).resolve("/usersXYZ").isEmpty());
    }
}
