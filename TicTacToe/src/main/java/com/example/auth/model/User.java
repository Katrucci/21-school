package com.example.auth.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(unique = true, nullable = false)
	private String login;

	@Column(nullable = false)
	private String password; //  В реальном проекте хешируй пароль!

	// Конструкторы
	public User() {}

	public User(String login, String password) {
		this.login = login;
		this.password = password;
	}

	// Геттеры
	public UUID getId() { return id; }
	public String getLogin() { return login; }
	public String getPassword() { return password; }
}