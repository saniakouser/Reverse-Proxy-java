package com.sania.proxy.controller;

import com.sania.proxy.service.ProxyForwarder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ProxyController {
    private final ProxyForwarder proxyForwarder;

    public ProxyController(ProxyForwarder proxyForwarder) {
        this.proxyForwarder = proxyForwarder;
    }

    @RequestMapping("/**")
    public ResponseEntity<byte[]> proxy(
            HttpServletRequest request,
            @RequestBody(required = false) byte[] body) throws IOException {
        return proxyForwarder.forward(request, body);
    }
}
