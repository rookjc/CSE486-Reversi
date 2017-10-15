package edu.miami.cse.reversi;

/**
 * Identifies a strategy that exceeded the allotted time to choose a square.
 */
public class StrategyTimedOutException extends Exception {

	
	private static final long serialVersionUID = 1L;

	private Strategy opponentStrategy;
	private Strategy timedOutStrategy;
	
	public StrategyTimedOutException(Strategy timedOutStrategy, Strategy opponentStrategy) {
		this.timedOutStrategy = timedOutStrategy;
		this.opponentStrategy = opponentStrategy;
	}

	/**
	 * @return The strategy that exceeded the allotted time.
	 */
	public Strategy getTimedOutStrategy() {
		return this.timedOutStrategy;
	}

	/**
	 * @return The strategy that was playing against the strategy that exceeded the
	 *         allotted time.
	 */
	public Strategy getOpponentStrategy() {
		return this.opponentStrategy;
	}

	
}
