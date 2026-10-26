package com.example.rentalservice.config;

import com.example.rentalservice.security.JwtAccessDeniedHandler;
import com.example.rentalservice.security.JwtAuthenticationEntryPoint;
import com.example.rentalservice.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

    @Autowired
    private JwtAuthenticationEntryPoint entryPoint;

    @Autowired
    private JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth

                        // ─── Swagger ──────────────────────────────────────────
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // ─── Appels internes Feign (auth-service → rental-service)
                        .requestMatchers("/api/customers/**").permitAll()
                        .requestMatchers("/api/scheduler/**").permitAll()

                        // ─── Rentals : GET autorisé USER + ADMIN ──────────────
                        .requestMatchers(HttpMethod.GET,
                                "/api/rentals/**").hasAnyRole("USER", "ADMIN")

                        // ─── Rentals : POST, PUT, DELETE, PATCH → ADMIN ───────
                        .requestMatchers(HttpMethod.POST,
                                "/api/rentals/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/rentals/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/rentals/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH,
                                "/api/rentals/**").hasRole("ADMIN")

                        // ─── Tout le reste → authentifié ──────────────────────
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}