package com.example.domain.model;

import java.util.UUID;

public final class Game {
	private final UUID id;
	private final GameBoard board;

	// Игроки привязаны к UUID. O-игрок может быть null (режим против ИИ).
	private final UUID playerXId;
	private final UUID playerOId;

	private final UUID currentTurnId; // Кто делает следующий ход
	private final GameStatus status;
	private final UUID winnerId;      // UUID победителя (null при ничьей/в процессе)

	public Game(UUID id, GameBoard board, UUID playerXId, UUID playerOId,
				UUID currentTurnId, GameStatus status, UUID winnerId) {
		this.id = id;
		this.board = board;
		this.playerXId = playerXId;
		this.playerOId = playerOId;
		this.currentTurnId = currentTurnId;
		this.status = status;
		this.winnerId = winnerId;
	}

	// Геттеры
	public UUID id() { return id; }
	public GameBoard board() { return board; }
	public UUID playerXId() { return playerXId; }
	public UUID playerOId() { return playerOId; }
	public UUID currentTurnId() { return currentTurnId; }
	public GameStatus status() { return status; }
	public UUID winnerId() { return winnerId; }

	// Получение символа на доске по UUID игрока
	public int getMarkByUserId(UUID userId) {
		if (userId != null && userId.equals(playerXId)) return Player.HUMAN.mark();
		if (userId != null && userId.equals(playerOId)) return Player.COMPUTER.mark();
		return 0; // Не является игроком в этой партии
	}

	// With-методы (иммутабельное обновление)
	public Game withBoard(GameBoard newBoard) {
		return new Game(id, newBoard, playerXId, playerOId, currentTurnId, status, winnerId);
	}
	public Game withCurrentTurn(UUID newTurnId) {
		return new Game(id, board, playerXId, playerOId, newTurnId, status, winnerId);
	}
	public Game withStatus(GameStatus newStatus) {
		return new Game(id, board, playerXId, playerOId, currentTurnId, newStatus, winnerId);
	}
	public Game withWinnerId(UUID newWinnerId) {
		return new Game(id, board, playerXId, playerOId, currentTurnId, status, newWinnerId);
	}
	public Game joinPlayerO(UUID userId) {
		return new Game(id, board, playerXId, userId, playerXId, GameStatus.IN_PROGRESS, null);
	}

}