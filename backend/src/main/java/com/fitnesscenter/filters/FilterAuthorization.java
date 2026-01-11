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
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getServletPath();

		if ("/api/stripe/webhook".equals(path) || "/api/member/login".equals(path)
				|| "/api/member/registration".equals(path) || path.startsWith("/swagger-ui")
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

			Claim roleClaim = jwt.getClaim("uloga");

			List<String> roles = new ArrayList<>();

			if (roleClaim != null && !roleClaim.isNull()) {

				String single = null;
				try {
					single = roleClaim.asString();
				} catch (Exception ignored) {
				}

				if (single != null && !single.trim().isEmpty()) {
					roles.add(single.trim());
				} else {

					List<String> list = null;
					try {
						list = roleClaim.asList(String.class);
					} catch (Exception ignored) {
					}

					if (list != null) {
						for (String r : list) {
							if (r != null && !r.trim().isEmpty())
								roles.add(r.trim());
						}
					}
				}
			}

			List<GrantedAuthority> authorities = new ArrayList<>();
			for (String r : roles) {
				String role = r.trim();
				if (!role.startsWith("ROLE_"))
					role = "ROLE_" + role;
				authorities.add(new SimpleGrantedAuthority(role));
			}

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null,
					authorities);

			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);

			filterChain.doFilter(request, response);

		} catch (Exception e) {
			SecurityContextHolder.clearContext();
			e.printStackTrace(); // ✅ vidi tačno zašto pada (expired? signature? etc.)

			SecurityContextHolder.clearContext();
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");

			response.getWriter()
					.write("{\"error\":\"Unauthorized\",\"message\":\"" + e.getMessage().replace("\"", "'") + "\"}");

			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"Invalid/expired token\"}");
		}
	}

}
