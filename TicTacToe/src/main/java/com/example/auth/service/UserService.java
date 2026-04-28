package com.example.auth.service;

import com.example.auth.model.SignUpRequest;
import com.example.auth.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
	/** Поиск пользователя по логину */
	Optional<User> findByLogin(String login);

	/** Регистрация нового пользователя */
	boolean register(SignUpRequest request);

	/** Проверка логина и пароля. Возвращает UUID при успехе. */
	Optional<UUID> authenticate(String login, String rawPassword);
}