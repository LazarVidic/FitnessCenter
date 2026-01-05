package com.fitnesscenter.security;

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

import com.fitnesscenter.filters.FilterAuthentication;
import com.fitnesscenter.filters.FilterAuthorization;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // LOGIN FILTER
        FilterAuthentication authFilter =
                new FilterAuthentication(authenticationManager());
        authFilter.setFilterProcessesUrl("/api/member/login");

        // JWT AUTHORIZATION FILTER (MORA ISTI SECRET KAO U AUTH FILTERU)
        FilterAuthorization authorizationFilter =
                new FilterAuthorization("secretKey");

        return http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/member/login",
                                "/api/member/registration",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilter(authFilter) // LOGIN
                .addFilterBefore(
                        authorizationFilter,
                        UsernamePasswordAuthenticationFilter.class
                ) // JWT
                .build();
    }
}
