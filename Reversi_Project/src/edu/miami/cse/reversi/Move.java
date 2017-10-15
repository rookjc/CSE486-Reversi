package edu.miami.cse.reversi;

import java.util.Objects;

/**
 * Represents the placement of a piece by a player at a square.
 */
public class Move {

  private Square square;
  private Player player;

  /**
   * Creates a new Move.
   * 
   * @param square
   *          The square where a piece was placed.
   * @param player
   *          The player placing the piece.
   */
  public Move(Square square, Player player) {
    this.square = square;
    this.player = player;
  }

  /**
   * @return The square where the piece was placed.
   */
  public Square getSquare() {
    return this.square;
  }

  /**
   * @return The player who placed the piece.
   */
  public Player getPlayer() {
    return this.player;
  }

  @Override
  public String toString() {
    return String.format("%s(%s, %s)", this.getClass().getSimpleName(), this.square, this.player);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.square, this.player);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Move) {
      Move that = (Move) obj;
      return Objects.equals(this.square, that.square) && Objects.equals(this.player, that.player);
    }
    return false;
  }

}
