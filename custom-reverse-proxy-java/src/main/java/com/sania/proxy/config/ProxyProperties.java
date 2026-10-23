package com.sania.proxy.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "proxy")
public class ProxyProperties {
    private int timeoutSeconds = 10;
    private List<Route> routes = new ArrayList<>();

    public int getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }
    public List<Route> getRoutes() { return routes; }
    public void setRoutes(List<Route> routes) { this.routes = routes; }

    public static class Route {
        private String id;
        private String pathPrefix;
        private List<String> upstreams = new ArrayList<>();

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getPathPrefix() { return pathPrefix; }
        public void setPathPrefix(String pathPrefix) { this.pathPrefix = pathPrefix; }
        public List<String> getUpstreams() { return upstreams; }
        public void setUpstreams(List<String> upstreams) { this.upstreams = upstreams; }
    }
}
