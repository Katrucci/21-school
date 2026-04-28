package com.example.auth.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
		@NotBlank(message = "Логин не может быть пустым")
		@Size(min = 3, max = 10, message = "Логин должен быть от 3 до 10 символов")
		String login,

		@NotBlank(message = "Пароль не может быть пустым")
		@Size(min = 5, message = "Пароль должен содержать минимум 5 символов")
		String password
) {}