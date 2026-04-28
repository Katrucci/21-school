package com.example.domain.model;

public enum GameStatus {
	WAITING,        // Ожидает второго игрока
	IN_PROGRESS,    // Игра идёт
	DRAW,           // Ничья
	X_WON,          // Победил игрок с крестиками
	O_WON;          // Победил игрок с ноликами

	public boolean isTerminal() {
		return this == DRAW || this == X_WON || this == O_WON;
	}
}