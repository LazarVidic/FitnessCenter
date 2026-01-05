package com.fitnesscenter.filters;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FilterAuthentication extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Algorithm algorithm = Algorithm.HMAC256("secretKey".getBytes());

    public FilterAuthentication(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Login mora POST (GET sa body-jem je nepouzdano i često daje NONE_PROVIDED)
            if (!"POST".equalsIgnoreCase(request.getMethod())) {
                throw new RuntimeException("Login must be POST with JSON body {email,password}.");
            }

            String email = null;
            String password = null;

            // 1) Probaj JSON body uvek (ne oslanjaj se na Content-Type)
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = objectMapper.readValue(request.getInputStream(), Map.class);

                Object emailObj = data.get("email");
                if (emailObj == null) emailObj = data.get("username"); // fallback ako neko šalje username
                if (emailObj != null) email = emailObj.toString();

                Object passObj = data.get("password");
                if (passObj == null) passObj = data.get("lozinka"); // podrži oba naziva
                if (passObj != null) password = passObj.toString();
            } catch (Exception ignored) {
                // ako nije JSON ili je prazan body, nastavi na parametre
            }

            // 2) Fallback na form-data / x-www-form-urlencoded
            if (email == null || email.isBlank()) {
                email = request.getParameter("email");
                if (email == null) email = request.getParameter("username");
            }
            if (password == null || password.isBlank()) {
                password = request.getParameter("password");
                if (password == null) password = request.getParameter("lozinka");
            }

            // 3) Validacija - da ne dobijaš NONE_PROVIDED i zbunjujuće poruke
            if (email == null || email.isBlank() || password == null || password.isBlank()) {
                throw new RuntimeException(
                        "Missing credentials. Send POST /api/member/login with JSON: {\"email\":\"...\",\"password\":\"...\"} " +
                        "and header Content-Type: application/json"
                );
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(email.trim(), password);

            return authenticationManager.authenticate(authToken);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), tokens);
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            org.springframework.security.core.AuthenticationException failed
    ) throws IOException, ServletException {

        // Lep JSON error umesto stack trace-a
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        Map<String, String> error = new HashMap<>();
        error.put("error", "Unauthorized");
        error.put("message", failed.getMessage());

        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
