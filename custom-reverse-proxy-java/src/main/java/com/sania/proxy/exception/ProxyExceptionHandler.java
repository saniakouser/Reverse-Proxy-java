package com.sania.proxy.exception;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestControllerAdvice
public class ProxyExceptionHandler {
    @ExceptionHandler(IOException.class)
    public ResponseEntity<byte[]> handleProxyError(IOException ex) {
        byte[] body = "Bad Gateway: upstream request failed".getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.status(502)
                .contentType(MediaType.TEXT_PLAIN)
                .body(body);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<byte[]> handleConfigurationError(IllegalStateException ex) {
        byte[] body = "Proxy configuration error".getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.status(503)
                .contentType(MediaType.TEXT_PLAIN)
                .body(body);
    }
}
