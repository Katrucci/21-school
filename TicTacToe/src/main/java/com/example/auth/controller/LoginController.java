package com.example.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String loginPage() {
		return "redirect:/login.html";
	}

	@GetMapping("/")
	public String root(Authentication authentication) {
		// Если пользователь НЕ аутентифицирован — редирект на страницу входа
		if (authentication == null || !authentication.isAuthenticated()) {
			return "redirect:/login.html";
		}
		return "forward:/index.html";
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		// Серверная логика выхода (если нужно)
		return ResponseEntity.ok().build();
	}
}