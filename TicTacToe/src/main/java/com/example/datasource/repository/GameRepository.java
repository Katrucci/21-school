package com.example.datasource.repository;

import com.example.datasource.entity.GameEntity;
import com.example.domain.model.GameStatus;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.UUID;

public interface GameRepository extends CrudRepository<GameEntity, UUID> {
	// Для эндпоинта "получить доступные текущие игры"
	List<GameEntity> findByStatus(GameStatus status);

	// Для будущей поддержки мультиплеера
	List<GameEntity> findByStatusAndPlayerXIdOrPlayerOId(GameStatus status, UUID xId, UUID oId);
}