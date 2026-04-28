package com.example.domain.model;

public enum Player {

	HUMAN(1),
	COMPUTER(2);

	private final int mark;
	Player(int mark) {
		this.mark = mark;
	}

	public int mark(){
		return mark;
	}

}
