package com.example.domain.model;

public final class GameBoard { // 15 usages
	private final int[][] cells; // игровое поле 3x3, 5 usages

	public GameBoard(int[][] cells) { // 4 usages
		this.cells = deepCopy3x3(cells);
	}

	public static GameBoard empty() { // 2 usages
		return new GameBoard(new int[][] {
				{0, 0, 0},
				{0, 0, 0},
				{0, 0, 0}
		});
	}

	public int[][] cells() { // no usages
		return deepCopy3x3(cells);
	}

	public int get(int r, int c) { // no usages
		return cells[r][c];
	}

	public GameBoard withMove(int r, int c, Player p) { // 1 usage
		int[][] copy = deepCopy3x3(cells);
		copy[r][c] = p.mark();
		return new GameBoard(copy);
	}

	public int countMark(int mark) { // 2 usages
		int cnt = 0;
		for (int[] row : cells) {
			for (int v : row) {
				if (v == mark) cnt++;
			}
		}
		return cnt;
	}

	private static int[][] deepCopy3x3(int[][] src) { // 3 usages
		if(src == null || src.length != 3 || src[0].length != 3 || src[1].length != 3 || src[2].length != 3 ){
			throw new IllegalArgumentException("Board must be 3x3 matrix");
		}
		int[][] dst = new int[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				dst[i][j] = src[i][j];
			}
		}
		return  dst;
	}

	public boolean isFull() {
		for (int[] row : cells) {
			for (int v : row) if (v == 0) return false;
		}
		return true;
	}

	public int getWinnerMark() {
		for (int i = 0; i < 3; i++) {

			//столбцы и строки
			if (cells[i][0] != 0 && cells[i][0] == cells[i][1] && cells[i][1] == cells[i][2]) return cells[i][0];
			if (cells[0][i] != 0 && cells[0][i] == cells[1][i] && cells[1][i] == cells[2][i]) return cells[0][i];
		}
		//диагонали
		if (cells[0][0] != 0 && cells[0][0] == cells[1][1] && cells[1][1] == cells[2][2]) return cells[0][0];
		if (cells[0][2] != 0 && cells[0][2] == cells[1][1] && cells[1][1] == cells[2][0]) return cells[0][2];
		return 0;
	}
}