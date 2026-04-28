package com.example.domain.service;

import com.example.datasource.entity.GameEntity;
import com.example.datasource.mapper.DomainDatasourceMapper;
import com.example.datasource.repository.GameRepository;
import com.example.domain.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@Transactional
public class GameServiceImpl implements GameService {

	private final GameRepository repo;
	private final DomainDatasourceMapper dsMapper;

	public GameServiceImpl(GameRepository repo, DomainDatasourceMapper dsMapper) {
		this.repo = repo;
		this.dsMapper = dsMapper;
	}

	@Override
	public Game createNewGame(UUID creatorId, boolean isPve) {
		GameStatus initialStatus = isPve ? GameStatus.IN_PROGRESS : GameStatus.WAITING;
		UUID firstTurn = isPve ? creatorId : null;

		Game game = new Game(
				UUID.randomUUID(),
				GameBoard.empty(),
				creatorId,
				null,
				firstTurn,
				initialStatus,
				null
		);
		GameEntity savedEntity = repo.save(dsMapper.toEntity(game));
		return dsMapper.toDomain(savedEntity);
	}

	@Override
	public Game createAndInitializeGame(UUID creatorId, boolean isPve) {
		GameStatus initialStatus = isPve ? GameStatus.IN_PROGRESS : GameStatus.WAITING;
		UUID firstTurn = isPve ? creatorId : null;

		Game game = new Game(
				UUID.randomUUID(),
				GameBoard.empty(),
				creatorId,
				null,
				firstTurn,
				initialStatus,
				null
		);

		// Если PvE - компьютер делает первый ход в той же транзакции
		if (isPve) {
			Move compMove = nextComputerMove(game);
			if (compMove != null) {
				GameBoard newBoard = game.board().withMove(compMove.row(), compMove.col(), Player.COMPUTER);
				game = game.withBoard(newBoard)
						.withCurrentTurn(creatorId);
			}
		}

		GameEntity savedEntity = repo.save(dsMapper.toEntity(game));
		return dsMapper.toDomain(savedEntity);
	}

	@Override
	public Game joinGame(UUID gameId, UUID playerId) {
		Game game = get(gameId);

		if (game.status() != GameStatus.WAITING) {
			throw new IllegalStateException("К этой игре нельзя присоединиться (статус: " + game.status() + ")");
		}

		if (game.playerOId() != null) {
			throw new IllegalStateException("В игре уже есть второй игрок");
		}

		if (game.playerXId().equals(playerId)) {
			throw new IllegalStateException("Нельзя присоединиться к своей же игре");
		}

		Game updated = game.joinPlayerO(playerId)
				.withStatus(GameStatus.IN_PROGRESS);
		save(updated);
		return updated;
	}

	@Override
	public Game makePlayerMove(UUID gameId, UUID userId, Move move) {
		Game game = get(gameId);
		GameBoard oldBoard = game.board(); // Сохраняем ДО изменений

		if (game.status().isTerminal()) {
			throw new IllegalStateException("Игра уже завершена!");
		}

		if (game.currentTurnId() == null || !game.currentTurnId().equals(userId)) {
			throw new IllegalArgumentException("Сейчас не ваш ход");
		}

		int mark;
		if (userId.equals(game.playerXId())) {
			mark = 1;
		} else if (userId.equals(game.playerOId())) {
			mark = 2;
		} else {
			throw new IllegalArgumentException("Вы не участвуете в этой игре");
		}

		// Проверяем, что клетка пуста
		if (oldBoard.cells()[move.row()][move.col()] != 0) {
			throw new IllegalArgumentException("Клетка уже занята");
		}

		GameBoard newBoard = oldBoard.withMove(move.row(), move.col(),
				mark == 1 ? Player.HUMAN : Player.COMPUTER);

		// Валидируем ТОЛЬКО ОДИН ход (без запроса к БД!)
		validateSingleMove(oldBoard, newBoard);

		Game updatedGame = game.withBoard(newBoard);

		UUID nextTurn = (userId.equals(game.playerXId())) ? game.playerOId() : game.playerXId();
		updatedGame = updatedGame.withCurrentTurn(nextTurn);

		int winnerMark = updatedGame.board().getWinnerMark();
		if (winnerMark == 1) {
			updatedGame = updatedGame.withStatus(GameStatus.X_WON)
					.withWinnerId(game.playerXId())
					.withCurrentTurn(null);
		} else if (winnerMark == 2) {
			updatedGame = updatedGame.withStatus(GameStatus.O_WON)
					.withWinnerId(game.playerOId())
					.withCurrentTurn(null);
		} else if (updatedGame.board().isFull()) {
			updatedGame = updatedGame.withStatus(GameStatus.DRAW)
					.withCurrentTurn(null);
		}

		save(updatedGame);
		return updatedGame;
	}

	@Override
	public Game makeComputerMove(UUID gameId) {
		Game game = get(gameId);

		if (game.status().isTerminal() || game.playerOId() != null) {
			return game;
		}

		Move compMove = nextComputerMove(game);
		if (compMove == null) return game;

		GameBoard newBoard = game.board().withMove(compMove.row(), compMove.col(), Player.COMPUTER);
		Game updatedGame = game.withBoard(newBoard)
				.withCurrentTurn(game.playerXId());

		int winnerMark = updatedGame.board().getWinnerMark();
		if (winnerMark == 1) {
			updatedGame = updatedGame.withStatus(GameStatus.X_WON)
					.withWinnerId(game.playerXId())
					.withCurrentTurn(null);
		} else if (winnerMark == 2) {
			updatedGame = updatedGame.withStatus(GameStatus.O_WON)
					.withWinnerId(null)
					.withCurrentTurn(null);
		} else if (updatedGame.board().isFull()) {
			updatedGame = updatedGame.withStatus(GameStatus.DRAW)
					.withCurrentTurn(null);
		}

		save(updatedGame);
		return updatedGame;
	}

	@Override
	public Game restartGame(UUID gameId) {
		Game existing = get(gameId);
		boolean isPve = (existing.playerOId() == null);

		Game restarted = existing
				.withBoard(GameBoard.empty())
				.withCurrentTurn(existing.playerXId()) // X всегда ходит первым
				.withStatus(isPve ? GameStatus.IN_PROGRESS : GameStatus.WAITING)
				.withWinnerId(null);
		save(restarted);
		return restarted;
	}

	@Override
	public List<Game> findAvailableGames() {
		return StreamSupport.stream(repo.findAll().spliterator(), false)
				.map(dsMapper::toDomain)
				.filter(g -> g.status() == GameStatus.WAITING && g.playerOId() == null)
				.toList();
	}

	@Override
	public Game get(UUID gameId) {
		return repo.findById(gameId)
				.map(dsMapper::toDomain)
				.orElseThrow(() -> new IllegalArgumentException("Игра не найдена: " + gameId));
	}

	@Override
	public void save(Game game) {
		// Важно: нужно получить существующую версию перед сохранением
		GameEntity existingEntity = repo.findById(game.id()).orElse(null);
		GameEntity entity = dsMapper.toEntity(game);

		if (existingEntity != null) {
			// Восстанавливаем версию для оптимистической блокировки
			entity.setVersion(existingEntity.getVersion());
		}

		repo.save(entity);
	}

	// ИСПРАВЛЕНО: валидация без запроса к БД
	private void validateSingleMove(GameBoard oldBoard, GameBoard newBoard) {
		int[][] prev = oldBoard.cells();
		int[][] now = newBoard.cells();
		int diff = 0;
		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 3; c++)
				if (prev[r][c] != now[r][c]) diff++;
		if (diff != 1) throw new IllegalArgumentException("Допустим только один ход за раз");
	}

	@Override
	public Move nextComputerMove(Game game) {
		int[][] board = game.board().cells();
		List<Move> moves = availableMoves(board);
		if (moves.isEmpty()) return null;

		Move bestMove = moves.get(0);
		int bestVal = Integer.MIN_VALUE;

		for (Move m : moves) {
			board[m.row()][m.col()] = Player.COMPUTER.mark();
			int val = minimax(board, false, 0);
			board[m.row()][m.col()] = 0;
			if (val > bestVal) {
				bestVal = val;
				bestMove = m;
			}
		}
		return bestMove;
	}

	private int minimax(int[][] board, boolean isMax, int depth) {
		GameBoard gb = new GameBoard(board);
		int w = gb.getWinnerMark();
		if (w == Player.COMPUTER.mark()) return 10 - depth;
		if (w == Player.HUMAN.mark()) return depth - 10;
		if (gb.isFull()) return 0;

		if (isMax) {
			int best = Integer.MIN_VALUE;
			for (Move m : availableMoves(board)) {
				board[m.row()][m.col()] = Player.COMPUTER.mark();
				best = Math.max(best, minimax(board, false, depth + 1));
				board[m.row()][m.col()] = 0;
			}
			return best;
		} else {
			int best = Integer.MAX_VALUE;
			for (Move m : availableMoves(board)) {
				board[m.row()][m.col()] = Player.HUMAN.mark();
				best = Math.min(best, minimax(board, true, depth + 1));
				board[m.row()][m.col()] = 0;
			}
			return best;
		}
	}

	private static List<Move> availableMoves(int[][] board) {
		List<Move> moves = new ArrayList<>();
		for (int r = 0; r < 3; r++)
			for (int c = 0; c < 3; c++)
				if (board[r][c] == 0) moves.add(new Move(r, c));
		return moves;
	}
}