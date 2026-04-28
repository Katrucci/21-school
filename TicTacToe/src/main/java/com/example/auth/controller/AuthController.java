package com.example.auth.controller;

import com.example.auth.model.SignUpRequest;
import com.example.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}


	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody @Valid SignUpRequest request) {
		if (authService.register(request)) {
			return ResponseEntity.ok(Map.of("message", "Registration successful"));
		}
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Login already exists"));
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestHeader("Authorization") String authHeader) {
		return authService.authorize(authHeader)
				.map(uuid -> ResponseEntity.ok(Map.of("userId", uuid.toString())))
				.orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
	}

	@ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationErrors(
			org.springframework.web.bind.MethodArgumentNotValidException ex) {

		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getDefaultMessage())
				.findFirst()
				.orElse("Ошибка валидации");

		return ResponseEntity.badRequest()
				.body(Map.of("error", errorMessage, "type", "validation"));
	}
}