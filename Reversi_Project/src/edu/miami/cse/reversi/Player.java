package edu.miami.cse.reversi;

/**
 * A Reversi player, either {@link #BLACK} or {@link #WHITE}.
 * 
 */
public enum Player {
  /**
   * The "black" or "dark" player. Typically, this player plays first.
   */
  BLACK,
  /**
   * The "white" or "light" player. Typically, this player plays second.
   */
  WHITE;

  /**
   * @return The opponent of this player: {@link #BLACK} for the {@link #WHITE}
   *         player and vice versa.
   */
  public Player opponent() {
    switch (this) {
      case WHITE:
        return BLACK;
      case BLACK:
        return WHITE;
    }
    throw new IllegalStateException("unexpected Player: " + this);
  }
}
