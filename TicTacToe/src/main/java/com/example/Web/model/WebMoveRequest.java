package com.example.Web.model;

public class WebMoveRequest {
	private int row;
	private int col;

	// Геттеры и сеттеры обязательны для десериализации Spring
	public int getRow() { return row; }
	public void setRow(int row) { this.row = row; }
	public int getCol() { return col; }
	public void setCol(int col) { this.col = col; }
}