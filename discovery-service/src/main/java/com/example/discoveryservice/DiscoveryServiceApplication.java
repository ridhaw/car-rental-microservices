package com.example.discoveryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class DiscoveryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServiceApplication.class, args);
        System.out.println("  ✅ Discovery Service démarré avec succès !");
        System.out.println("  🌐 Port    : 8761");
        System.out.println("  🔍 Eureka Dashboard : http://localhost:8761");
    }

}