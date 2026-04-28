package com.example.auth.controller;

import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
	private final UserRepository userRepository;

	public UserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getUserById(@PathVariable UUID id) {
		return userRepository.findById(id)
				.map(user -> ResponseEntity.ok(Map.of(
						"id", user.getId().toString(),
						"login", user.getLogin()
				)))
				.orElse(ResponseEntity.notFound().build());
	}
}