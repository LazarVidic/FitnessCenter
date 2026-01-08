package com.fitnesscenter.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
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

        // preskoči auth + swagger rute
        if ("/api/member/login".equals(path)
                || "/api/member/registration".equals(path)
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring("Bearer ".length());
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);

            String email = jwt.getSubject();

            // claim "uloga" može biti lista ili string
            Claim roleClaim = jwt.getClaim("uloga");

            List<String> roles = Collections.emptyList();
            if (roleClaim != null && !roleClaim.isNull()) {
                roles = roleClaim.asList(String.class);

                // ako nije lista nego string
                if (roles == null || roles.isEmpty()) {
                    String single = roleClaim.asString();
                    if (single != null && !single.trim().isEmpty()) {
                        roles = new ArrayList<>();
                        roles.add(single.trim());
                    } else {
                        roles = Collections.emptyList();
                    }
                }
            }

            List<GrantedAuthority> authorities = new ArrayList<>();
            for (String r : roles) {
                if (r == null) continue;
                String role = r.trim();
                if (role.isEmpty()) continue;

                // normalizuj: ADMIN -> ROLE_ADMIN; ROLE_ADMIN ostaje ROLE_ADMIN
                if (!role.startsWith("ROLE_")) {
                    role = "ROLE_" + role;
                }
                authorities.add(new SimpleGrantedAuthority(role));
            }

            // debug (možeš obrisati kasnije)
            System.out.println("JWT roles claim uloga = " + roles);
            System.out.println("Granted authorities = " + authorities);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
