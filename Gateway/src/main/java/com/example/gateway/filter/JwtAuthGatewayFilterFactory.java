package com.example.gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthGatewayFilterFactory
        extends AbstractGatewayFilterFactory<JwtAuthGatewayFilterFactory.Config> {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    public JwtAuthGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return jwtFilter;
    }

    public static class Config {}
}