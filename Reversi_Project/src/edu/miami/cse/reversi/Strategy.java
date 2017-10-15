package edu.miami.cse.reversi;

/**
 * A strategy for choosing where a player should play their next piece.
 */
public interface Strategy {
  /**
   * Determines where the current player should play their next piece. Some
   * methods that may be useful for defining such a strategy:
   * <ul>
   * <li>{@link Board#getCurrentPossibleSquares()}</li>
   * <li>{@link Board#getCurrentPlayer()}</li>
   * <li>{@link Board#getSquareOwners()}</li>
   * <li>{@link Board#getPlayerSquareCounts()}</li>
   * </ul>
   * 
   * @param board
   *          The current state of the Reversi board.
   * @return The square where the current player should play their next piece.
   */
  public Square chooseSquare(Board board);
}
