package com.example.Web.controller;

import com.example.Web.model.WebErrorResponse;
import jakarta.persistence.OptimisticLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<WebErrorResponse> handleBadRequest(IllegalArgumentException ex) {
		return ResponseEntity
				.badRequest()
				.body(new WebErrorResponse(ex.getMessage(), "INVALID_REQUEST"));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<WebErrorResponse> handleIllegalState(IllegalStateException ex) {
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(new WebErrorResponse(ex.getMessage(), "GAME_STATE_ERROR"));
	}

	@ExceptionHandler(OptimisticLockException.class)
	public ResponseEntity<WebErrorResponse> handleOptimisticLock(OptimisticLockException ex) {
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(new WebErrorResponse(
						"Игра была изменена другим запросом. Пожалуйста, обновите страницу.",
						"CONCURRENT_MODIFICATION"
				));
	}
}