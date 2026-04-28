package com.example.auth.config;

import com.example.auth.filter.AuthFilter;
import com.example.auth.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public AuthFilter authFilter(AuthService authService) {
		return new AuthFilter(authService);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthFilter authFilter) throws Exception {
		http
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/",
								"/login.html",
								"/index.html",
								"/auth/**",
								"/css/**",
								"/script.js",
								"/image/**",
								"/fonts/**",
								"/sounds/**",
								"/favicon.ico",
								"/.well-known/**",
								"/error"
						).permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(form -> form.disable())
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/login.html")
						.permitAll()
				)
				.addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}