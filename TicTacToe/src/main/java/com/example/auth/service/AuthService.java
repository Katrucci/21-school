package com.example.auth.service;

import com.example.auth.model.SignUpRequest;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

	private final UserService userService;

	public AuthService(UserService userService) {
		this.userService = userService;
	}

	/** Регистрация: делегирует UserService */
	public boolean register(SignUpRequest request) {
		return userService.register(request);
	}

	/** Авторизация: парсит Basic Auth и вызывает UserService */
	public Optional<UUID> authorize(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Basic ")) {
			return Optional.empty();
		}

		try {
			String base64Credentials = authHeader.substring("Basic ".length()).trim();
			String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
			String[] parts = credentials.split(":", 2);

			if (parts.length != 2) return Optional.empty();

			String login = parts[0].trim();
			String password = parts[1];

			return userService.authenticate(login, password);
		} catch (IllegalArgumentException e) {
			return Optional.empty(); // Базовая строка не валидна
		}
	}
}