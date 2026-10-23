package com.sania.proxy.service;

import com.sania.proxy.config.ProxyProperties;
import com.sania.proxy.routing.RouteResolver;
import com.sania.proxy.routing.UpstreamSelector;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Service
public class ProxyForwarder {
    private static final Set<String> HOP_BY_HOP = Set.of(
            "connection", "keep-alive", "proxy-authenticate",
            "proxy-authorization", "te", "trailer",
            "transfer-encoding", "upgrade", "host",
            "content-length", "expect"
    );

    private final RouteResolver routeResolver;
    private final UpstreamSelector upstreamSelector;
    private final ProxyProperties properties;
    private final HttpClient httpClient;

    public ProxyForwarder(RouteResolver routeResolver,
                          UpstreamSelector upstreamSelector,
                          ProxyProperties properties) {
        this.routeResolver = routeResolver;
        this.upstreamSelector = upstreamSelector;
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();
    }

    public ResponseEntity<byte[]> forward(HttpServletRequest request, byte[] body)
            throws IOException {
        String path = request.getRequestURI();
        Optional<ProxyProperties.Route> matched = routeResolver.resolve(path);

        if (matched.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProxyProperties.Route route = matched.get();
        String upstream = upstreamSelector.select(route);
        String target = upstream + path;

        if (request.getQueryString() != null) {
            target += "?" + request.getQueryString();
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(target))
                .timeout(Duration.ofSeconds(properties.getTimeoutSeconds()));

        copyRequestHeaders(request, builder);
        byte[] requestBody = body == null ? new byte[0] : body;
        HttpRequest.BodyPublisher publisher = hasRequestBody(request.getMethod())
                ? HttpRequest.BodyPublishers.ofByteArray(requestBody)
                : HttpRequest.BodyPublishers.noBody();

        builder.method(request.getMethod(), publisher);

        try {
            HttpResponse<byte[]> response = httpClient.send(
                    builder.build(), HttpResponse.BodyHandlers.ofByteArray());

            HttpHeaders responseHeaders = new HttpHeaders();
            response.headers().map().forEach((name, values) -> {
                if (!isHopByHop(name)) {
                    values.forEach(value -> responseHeaders.add(name, value));
                }
            });

            return ResponseEntity.status(response.statusCode())
                    .headers(responseHeaders)
                    .body(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Proxy request interrupted", e);
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid upstream URI", e);
        }
    }

    private void copyRequestHeaders(HttpServletRequest request, HttpRequest.Builder builder) {
        Enumeration<String> names = request.getHeaderNames();
        while (names != null && names.hasMoreElements()) {
            String name = names.nextElement();
            if (isHopByHop(name)) continue;

            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                builder.header(name, values.nextElement());
            }
        }
    }

    private boolean isHopByHop(String name) {
        return HOP_BY_HOP.contains(name.toLowerCase(Locale.ROOT));
    }

    private boolean hasRequestBody(String method) {
        return switch (method.toUpperCase(Locale.ROOT)) {
            case "POST", "PUT", "PATCH" -> true;
            default -> false;
        };
    }
}
