package com.fitnesscenter.filters;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FilterAuthorization extends OncePerRequestFilter {

    private final Algorithm algorithm;

    public FilterAuthorization(String jwtSecret) {
        this.algorithm = Algorithm.HMAC256(jwtSecret.getBytes());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        // Preskoči login/registration (prilagodi rute svojim endpoint-ima)
        if (path.equals("/api/member/login") || path.equals("/api/member/registration")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(7);
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);

            String email = jwt.getSubject();

            // BITNO: FilterAuthentication upisuje claim "uloga"
            List<String> roles = jwt.getClaim("uloga").asList(String.class);
            if (roles == null) roles = Collections.emptyList();

            var authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            var authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            // opciono: možeš vratiti 403 odmah, ali često je ok samo pustiti dalje pa će security blokirati
            // response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            // return;
        }

        filterChain.doFilter(request, response);
    }
}
