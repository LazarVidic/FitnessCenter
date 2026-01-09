package com.fitnesscenter.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fitnesscenter.filters.FilterAuthentication;
import com.fitnesscenter.filters.FilterAuthorization;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    // AuthenticationManager bean (potreban za login filter)
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 🔐 CORS konfiguracija (React: localhost:5173)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // LOGIN FILTER
        FilterAuthentication authFilter =
                new FilterAuthentication(authenticationManager());
        authFilter.setFilterProcessesUrl("/api/member/login");

        // JWT AUTHORIZATION FILTER
        FilterAuthorization authorizationFilter =
                new FilterAuthorization("secretKey");

        return http
                // ✅ CORS (koristi gornji CorsConfigurationSource bean)
                .cors(Customizer.withDefaults())

                // ❌ CSRF (ne treba za JWT)
                .csrf(csrf -> csrf.disable())

                // ❌ Session (JWT = stateless)
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🔐 Pravila pristupa
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/member/login",
                                "/api/member/registration",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // JWT Authorization filter (pre UsernamePasswordAuthenticationFilter)
                .addFilterBefore(
                        authorizationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                // JWT Login filter (tačno na UsernamePasswordAuthenticationFilter)
                .addFilterAt(
                        authFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }
}
