package com.example.Web.controller;

import com.example.domain.model.Game;
import com.example.domain.model.GameStatus;
import com.example.domain.service.GameService;
import com.example.domain.service.Move;
import com.example.Web.mapper.DomainWebMapper;
import com.example.Web.model.WebGame;
import com.example.Web.model.WebMoveRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

	private final GameService gameService;
	private final DomainWebMapper webMapper;

	public GameController(GameService gameService, DomainWebMapper webMapper) {
		this.gameService = gameService;
		this.webMapper = webMapper;
	}

	private UUID getCurrentUserId() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof UUID userId) {
			return userId;
		}
		throw new IllegalStateException("Пользователь не аутентифицирован");
	}

	// Эндпоинт для создания новой игры с пользователем или компьютером
	@PostMapping("/create")
	public ResponseEntity<?> create(@RequestParam(required = false, defaultValue = "PVE") String mode) {
		try {
			UUID userId = getCurrentUserId();
			boolean isPve = !"PVP".equalsIgnoreCase(mode);

			Game game;
			if (isPve) {
				// PvE: создаем И инициализируем ход компьютера в одной транзакции
				game = gameService.createAndInitializeGame(userId, true);
			} else {
				// PvP: только создаем игру, ждем второго игрока
				game = gameService.createNewGame(userId, false);
			}

			return ResponseEntity.ok(webMapper.toWeb(game));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", e.getMessage()));
		}
	}

	// Эндпоинт для получения доступных текущих игр (только PvP игры в статусе WAITING)
	@GetMapping("/available")
	public List<WebGame> getAvailableGames() {
		return gameService.findAvailableGames().stream()
				.map(webMapper::toWeb)
				.toList();
	}

	// Эндпоинт для присоединения пользователя к игре
	@PostMapping("/{id}/join")
	public WebGame joinGame(@PathVariable UUID id) {
		UUID playerId = getCurrentUserId();
		Game game = gameService.joinGame(id, playerId);
		return webMapper.toWeb(game);
	}

	// Эндпоинт для получения текущей игры
	@GetMapping("/{id}")
	public WebGame getGame(@PathVariable UUID id) {
		Game game = gameService.get(id);
		return webMapper.toWeb(game);
	}

	// Улучшенный эндпоинт обновления текущей игры с учетом игры с пользователем или компьютером
	@PostMapping("/{id}/move")
	public WebGame makeMove(@PathVariable UUID id, @RequestBody WebMoveRequest moveRequest) {
		UUID userId = getCurrentUserId();
		Move move = new Move(moveRequest.getRow(), moveRequest.getCol());

		Game updatedGame = gameService.makePlayerMove(id, userId, move);

		// Если игра с компьютером (playerOId == null) и игра не завершена, компьютер делает ход
		if (!updatedGame.status().isTerminal() && updatedGame.playerOId() == null) {
			updatedGame = gameService.makeComputerMove(id);
		}

		return webMapper.toWeb(updatedGame);
	}

	// Дополнительный эндпоинт для рестарта игры
	@PostMapping("/{id}/restart")
	public WebGame restartGame(@PathVariable UUID id) {
		Game restarted = gameService.restartGame(id);
		return webMapper.toWeb(restarted);
	}

	// Эндпоинт для получения информации о пользователе по UUID
	@GetMapping("/user/{userId}")
	public Map<String, Object> getUserInfo(@PathVariable UUID userId) {
		// Здесь нужно вызывать UserService для получения информации
		return Map.of(
				"userId", userId.toString(),
				"message", "User info endpoint - integrate with UserService"
		);
	}
}