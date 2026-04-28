package com.example.auth.service;

import com.example.auth.model.SignUpRequest;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Optional<User> findByLogin(String login) {
		return userRepository.findByLogin(login.trim());
	}

	@Override
	public boolean register(SignUpRequest request) {
		String cleanLogin = request.login().trim();
		if (userRepository.existsByLogin(cleanLogin)) {
			return false;
		}
		User newUser = new User(cleanLogin, passwordEncoder.encode(request.password()));
		userRepository.save(newUser);
		return true;
	}

	@Override
	public Optional<UUID> authenticate(String login, String rawPassword) {
		return findByLogin(login)
				.filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
				.map(User::getId);
	}
}
