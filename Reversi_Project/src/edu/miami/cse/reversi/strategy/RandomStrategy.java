package edu.miami.cse.reversi.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.miami.cse.reversi.Board;
import edu.miami.cse.reversi.Square;
import edu.miami.cse.reversi.Strategy;

/**
 * A simple strategy that just chooses randomly from the squares available to
 * the current player.
 */
public class RandomStrategy implements Strategy {

  @Override
  public Square chooseSquare(Board board) {
    return chooseOne(board.getCurrentPossibleSquares());
  }

  /**
   * A simple utility method for selecting a random item from a set. Not
   * particularly efficient.
   * 
   * @param itemSet
   *          The set of items from which to select.
   * @return A random item from the set.
   */
  public static <T> T chooseOne(Set<T> itemSet) {
    List<T> itemList = new ArrayList<>(itemSet);
    return itemList.get(new Random().nextInt(itemList.size()));
  }
}
