package com.example.auth.filter;

import com.example.auth.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class AuthFilter extends GenericFilterBean {

	private final AuthService authService;

	public AuthFilter(AuthService authService) {
		this.authService = authService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String authHeader = httpRequest.getHeader("Authorization");

		// Если заголовок есть — пытаемся авторизовать
		if (authHeader != null && !authHeader.isBlank()) {
			Optional<UUID> userId = authService.authorize(authHeader);

			if (userId.isPresent()) {
				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(
								userId.get(),
								null,
								AuthorityUtils.createAuthorityList("ROLE_USER")
						);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} else {
				// Заголовок есть, но неверный — возвращаем 401
				httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				httpResponse.getWriter().write("Invalid credentials");
				return;
			}
		}
		// Если заголовка нет — просто пропускаем дальше
		// SecurityConfig сам решит, нужен ли доступ (permitAll или authenticated)

		chain.doFilter(request, response);
	}
}