package com.example.carservice.config;

import com.example.carservice.security.JwtAccessDeniedHandler;
import com.example.carservice.security.JwtAuthenticationEntryPoint;
import com.example.carservice.security.JwtAuthenticationFilter;
import jakarta.ws.rs.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;
    @Autowired private JwtAuthenticationEntryPoint entryPoint;
    @Autowired private JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,    "/api/cars/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/cars").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/cars/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cars/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,  "/api/cars/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
