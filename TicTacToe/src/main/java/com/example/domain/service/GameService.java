package com.example.domain.service;

import com.example.domain.model.Game;
import java.util.List;
import java.util.UUID;

public interface GameService {
	// Создание игры с указанием создателя (он ходит крестиками)
	Game createNewGame(UUID creatorId,  boolean isPve);

	Game createAndInitializeGame(UUID creatorId, boolean isPve);

	// Присоединение второго игрока к игре (мультиплеер)
	Game joinGame(UUID gameId, UUID playerId);

	// Ход игрока: теперь с проверкой, что ходит именно этот пользователь
	Game makePlayerMove(UUID gameId, UUID userId, Move move);

	// Ход компьютера (для режима против ИИ)
	Game makeComputerMove(UUID gameId);

	// Перезапуск игры
	Game restartGame(UUID gameId);

	// Получение игры по ID
	Game get(UUID gameId);

	// Сохранение игры
	void save(Game game);

	// Список доступных игр для присоединения (для эндпоинта "получить доступные игры")
	List<Game> findAvailableGames();

	// Вспомогательные методы (оставляем, но адаптируем)
	Move nextComputerMove(Game game);
}