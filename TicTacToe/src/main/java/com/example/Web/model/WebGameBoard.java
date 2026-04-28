package com.example.Web.model;

public record WebGameBoard(int[][] cells) {
	public WebGameBoard() {
		this(new int[3][3]);
	}
}


// Нужен для Jackson JSON deserialization