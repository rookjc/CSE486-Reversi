package edu.miami.cse.reversi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import edu.miami.cse.reversi.strategy.*;

/**
 * A game of Reversi, played by two strategies.
 */
public class Reversi {

	private Map<Player, Strategy> strategies; 
	private long timeout; //The maximum time allowed to a strategy for choosing a square.
	private TimeUnit timeoutUnit; //The unit of the timeout


	/**
	 * Creates a new Reversi game.
	 * 
	 * @param blackStrategy
	 *          The strategy used to play the black pieces.
	 * @param whiteStrategy
	 *          The strategy used to play the white pieces.
	 * @param timeout
	 *          The maximum time allowed to a strategy for choosing a square.
	 * @param timeoutUnit
	 *          The unit of the timeout
	 */
	public Reversi(
			Strategy blackStrategy,
			Strategy whiteStrategy,
			long timeout,
			TimeUnit timeoutUnit) {
		this.strategies = new HashMap<>();
		this.strategies.put(Player.BLACK, blackStrategy);
		this.strategies.put(Player.WHITE, whiteStrategy);
		this.timeout = timeout;
		this.timeoutUnit = timeoutUnit;
	}

	/**
	 * Plays the strategies on the given Reversi board.
	 * 
	 * @param board
	 *          The board in its initial state.
	 * @return The board after play is complete.
	 * @throws StrategyTimedOutException
	 *           If a strategy exceeds the alloted time to choose a square.
	 */
	public Board play(Board board) throws StrategyTimedOutException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Board curr = board;
		while (!curr.isComplete()) {
			if (curr.getCurrentPossibleSquares().isEmpty()) {
				curr = curr.pass();
			} else {
				Player player = curr.getCurrentPlayer();
				final Strategy strategy = this.strategies.get(player);
				final Board boardForFuture = curr;
				Future<Square> future = executor.submit(new Callable<Square>() {
					@Override
					public Square call() throws Exception {
						return strategy.chooseSquare(boardForFuture);
					}
				});
				Square square;
				try {
					square = future.get(this.timeout, this.timeoutUnit);
				} catch (InterruptedException | ExecutionException | TimeoutException e) {
					future.cancel(true);
					throw new StrategyTimedOutException(strategy, this.strategies.get(player.opponent()));
				}
				curr = curr.play(square);
			}
		}
		executor.shutdownNow();
		return curr;
	}

	/**
	 * Gets the winning strategy from a board.
	 * 
	 * @param board
	 *          A Reversi board, after the game is complete.
	 * @return The winning strategy.
	 */
	public Strategy getWinner(Board board) {
		return this.strategies.get(board.getWinner());
	}


	
}
