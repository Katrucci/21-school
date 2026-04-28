package com.example.datasource.mapper;

import com.example.datasource.entity.GameEntity;
import com.example.domain.model.*;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public final class DomainDatasourceMapper {

	public GameEntity toEntity(Game game) {
		String boardString = Arrays.stream(game.board().cells())
				.flatMapToInt(Arrays::stream)
				.mapToObj(String::valueOf)
				.collect(Collectors.joining(","));

		int currentPlayerMark = 0;
		if (game.currentTurnId() != null) {
			if (game.currentTurnId().equals(game.playerXId())) currentPlayerMark = 1;
			else if (game.currentTurnId().equals(game.playerOId())) currentPlayerMark = 2;
		}

		Integer winnerMark = null;
		if (game.winnerId() != null) {
			if (game.winnerId().equals(game.playerXId())) {
				winnerMark = 1;
			} else if (game.winnerId().equals(game.playerOId())) {
				winnerMark = 2;
			}
		}

		GameEntity entity = new GameEntity();
		entity.setId(game.id());
		entity.setBoardData(boardString);
		entity.setStatus(game.status());
		entity.setPlayerXId(game.playerXId());  // ← Сохраняем!
		entity.setPlayerOId(game.playerOId());  // ← Может быть null
		entity.setCurrentPlayerMark(currentPlayerMark);
		entity.setWinnerMark(winnerMark);

		return entity;
	}

	public GameEntity toEntity(Game game, Long existingVersion) {
		GameEntity entity = toEntity(game);
		entity.setVersion(existingVersion);
		return entity;
	}


	public Game toDomain(GameEntity entity) {
		int[][] cells = new int[3][3];
		String[] values = entity.getBoardData().split(",");
		for (int i = 0; i < 9; i++) {
			cells[i / 3][i % 3] = Integer.parseInt(values[i]);
		}

		UUID currentTurnId = null;
		if (entity.getCurrentPlayerMark() == 1) currentTurnId = entity.getPlayerXId();
		else if (entity.getCurrentPlayerMark() == 2) currentTurnId = entity.getPlayerOId();

		UUID winnerId = null;
		if (entity.getWinnerMark() != null) {
			if (entity.getWinnerMark() == 1) winnerId = entity.getPlayerXId();
			else if (entity.getWinnerMark() == 2) winnerId = entity.getPlayerOId();
		}

		return new Game(
				entity.getId(),
				new GameBoard(cells),
				entity.getPlayerXId(),
				entity.getPlayerOId(),
				currentTurnId,
				entity.getStatus(),
				winnerId
		);
	}
}