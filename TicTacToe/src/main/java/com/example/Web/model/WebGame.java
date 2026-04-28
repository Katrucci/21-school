package com.example.Web.model;

import com.example.domain.model.GameStatus;
import java.util.UUID;

public final class WebGame {
	private UUID id;
	private WebGameBoard board;
	private GameStatus status;
	private String winner;       // "X" или "O" или null (для простоты фронтенда)
	private UUID winnerId;       // UUID победителя (полезно для логики)
	private UUID currentTurnId;  // Чей сейчас ход
	private UUID playerXId;      // Кто играет за X
	private UUID playerOId;      // Кто играет за O (может быть null)

	// Пустой конструктор для Jackson
	public WebGame() {}

	public WebGame(UUID id, WebGameBoard board, GameStatus status, String winner,
				   UUID winnerId, UUID currentTurnId, UUID playerXId, UUID playerOId) {
		this.id = id;
		this.board = board;
		this.status = status;
		this.winner = winner;
		this.winnerId = winnerId;
		this.currentTurnId = currentTurnId;
		this.playerXId = playerXId;
		this.playerOId = playerOId;
	}

	// Геттеры и сеттеры (обязательны!)
	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	public WebGameBoard getBoard() { return board; }
	public void setBoard(WebGameBoard board) { this.board = board; }
	public GameStatus getStatus() { return status; }
	public void setStatus(GameStatus status) { this.status = status; }
	public String getWinner() { return winner; }
	public void setWinner(String winner) { this.winner = winner; }
	public UUID getWinnerId() { return winnerId; }
	public void setWinnerId(UUID winnerId) { this.winnerId = winnerId; }
	public UUID getCurrentTurnId() { return currentTurnId; }
	public void setCurrentTurnId(UUID currentTurnId) { this.currentTurnId = currentTurnId; }
	public UUID getPlayerXId() { return playerXId; }
	public void setPlayerXId(UUID playerXId) { this.playerXId = playerXId; }
	public UUID getPlayerOId() { return playerOId; }
	public void setPlayerOId(UUID playerOId) { this.playerOId = playerOId; }
}