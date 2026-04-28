package com.example.Web.mapper;

import com.example.domain.model.Game;
import com.example.Web.model.WebGame;
import com.example.Web.model.WebGameBoard;
import org.springframework.stereotype.Component;

@Component
public final class DomainWebMapper {

	public WebGame toWeb(Game domainGame) {
		WebGameBoard webBoard = new WebGameBoard(domainGame.board().cells());


		String winnerName = null;
		if (domainGame.winnerId() != null) {
			if (domainGame.winnerId().equals(domainGame.playerXId())) {
				winnerName = "X";
			} else if (domainGame.winnerId().equals(domainGame.playerOId())) {
				winnerName = "O";
			}
		}


		return new WebGame(
				domainGame.id(),
				webBoard,
				domainGame.status(),
				winnerName,                // winner (String)
				domainGame.winnerId(),      // winnerId (UUID)
				domainGame.currentTurnId(), // currentTurnId
				domainGame.playerXId(),     // playerXId
				domainGame.playerOId()
		);
	}
}