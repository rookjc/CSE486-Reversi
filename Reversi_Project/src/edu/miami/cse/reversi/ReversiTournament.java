package edu.miami.cse.reversi;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import edu.miami.cse.reversi.strategy.*;

public class ReversiTournament {
	/**
	 * Runs a round-robin tournament over Reversi strategies.
	 */
	public static void main(String[] args) throws Exception {

		int nGames = 1; 
		long timeout = 10;
		TimeUnit timeoutUnit = TimeUnit.SECONDS;

		// List of the strategies in the tournament 
		List<Strategy> strategies = Lists.newArrayList();

		strategies.add(new RandomStrategy());
		strategies.add(new Human());

		// The number of wins of each strategy 
		Map<Strategy, Integer> wins = Maps.newHashMap();
		for (Strategy strategy : strategies) {
			wins.put(strategy, 0);
		}

		// Run N rounds, pairing each strategy with each other strategy. There will
		// actually be 2N games since each strategy gets to be both black and white
		Board board = new Board();
		for (int game = 0; game < nGames; ++game) {
			for (int i = 0; i < strategies.size(); ++i) {
				for (int j = i + 1; j < strategies.size(); ++j) {
					Strategy strategy1 = strategies.get(i);
					Strategy strategy2 = strategies.get(j);
					Reversi reversi;
					Strategy winner;

					// first game: strategy1=BLACK, strategy2=WHITE
					reversi = new Reversi(strategy1, strategy2, timeout, timeoutUnit);
					try {
						winner = reversi.getWinner(reversi.play(board));
					} catch (StrategyTimedOutException e) {
						winner = e.getOpponentStrategy();
					}
					// If one of the strategies timed out, the opponent is considered the winner 
					if (winner != null) {
						wins.put(winner, wins.get(winner) + 1);
					}

					// second game: strategy2=BLACK, strategy1=WHITE
					reversi = new Reversi(strategy2, strategy1, timeout, timeoutUnit);
					try {
						winner = reversi.getWinner(reversi.play(board));
					} catch (StrategyTimedOutException e) {
						winner = e.getOpponentStrategy();
					}
					if (winner != null) {
						wins.put(winner, wins.get(winner) + 1);
					}
				}
			}
		}

		// rank strategies by number of wins
		Ordering<Strategy> byWins = Ordering.natural().onResultOf(Functions.forMap(wins)).reverse();
		for (Strategy strategy : byWins.sortedCopy(wins.keySet())) {
			System.out.printf("%4d\t%s\n", wins.get(strategy), strategy.getClass().getName());
		}
	}



	
}
