package com.example.datasource.entity;

import com.example.domain.model.GameStatus;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "games")
public class GameEntity {

	@Id
	private UUID id;


	@Version
	private Long version = 0L;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String boardData;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GameStatus status;

	// Для режима против компьютера одно из полей будет null
	@Column(nullable = true)
	private UUID playerXId;

	@Column(nullable = true)
	private UUID playerOId;

	@Column(name = "current_player_mark")
	private int currentPlayerMark;

	@Column(name = "winner_mark")
	private Integer winnerMark;

	public GameEntity() {}

	public GameEntity(UUID id, String boardData, GameStatus status,
					  UUID playerXId, UUID playerOId,
					  int currentPlayerMark, Integer winnerMark) {
		this.id = id;
		this.boardData = boardData;
		this.status = status;
		this.playerXId = playerXId;
		this.playerOId = playerOId;
		this.currentPlayerMark = currentPlayerMark;
		this.winnerMark = winnerMark;
	}

	// Геттеры и сеттеры (обязательны для JPA)

	public Long getVersion() { return version; }
	public void setVersion(Long version) { this.version = version; }

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }

	public String getBoardData() { return boardData; }
	public void setBoardData(String boardData) { this.boardData = boardData; }

	public GameStatus getStatus() { return status; }
	public void setStatus(GameStatus status) { this.status = status; }

	public UUID getPlayerXId() { return playerXId; }
	public void setPlayerXId(UUID playerXId) { this.playerXId = playerXId; }

	public UUID getPlayerOId() { return playerOId; }
	public void setPlayerOId(UUID playerOId) { this.playerOId = playerOId; }

	public int getCurrentPlayerMark() { return currentPlayerMark; }
	public void setCurrentPlayerMark(int currentPlayerMark) {
		this.currentPlayerMark = currentPlayerMark;
	}

	public Integer getWinnerMark() { return winnerMark; }
	public void setWinnerMark(Integer winnerMark) { this.winnerMark = winnerMark; }
}