package com.fitnesscenter.filters;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fitnesscenter.dtos.LoginRequestDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FilterAuthentication extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Algorithm algorithm;

    public FilterAuthentication(AuthenticationManager authenticationManager, String jwtSecret) {
        this.authenticationManager = authenticationManager;
        this.algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        String email = null;
        String password = null;

        try {
            String ct = request.getContentType();

            // JSON (tolerantno: JSON + charset)
            if (ct != null && ct.toLowerCase().contains("application/json")) {
                LoginRequestDto body = objectMapper.readValue(request.getInputStream(), LoginRequestDto.class);
                email = body.getEmail();
                password = body.getPassword();
            } else {
                // form-urlencoded fallback
                email = request.getParameter("email");
                if (email == null || email.isBlank()) {
                    // ako negde šalješ username umesto email
                    email = request.getParameter("username");
                }
                password = request.getParameter("password");
            }
        } catch (Exception e) {
            // nemoj gutati - vrati jasnu poruku umesto da puca server
            throw new AuthenticationServiceException("Invalid login request body. Expected JSON {email,password} or form params.", e);
        }

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new AuthenticationServiceException(
                "Missing credentials. Send POST /api/member/login with JSON: {\"email\":\"...\",\"password\":\"...\"} and header Content-Type: application/json"
            );
        }

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(email.trim(), password);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authentication
    ) throws IOException {

        User user = (User) authentication.getPrincipal();

        String jwtToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + 60 * 60 * 1000)) // 1h
                .withClaim("uloga", user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .sign(algorithm);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("jwtToken", jwtToken);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), tokens);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException failed
    ) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, String> error = new HashMap<>();
        error.put("error", "Unauthorized");
        error.put("message", failed.getMessage());

        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
