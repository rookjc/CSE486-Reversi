package edu.miami.cse.reversi;

import java.util.Objects;

/**
 * A square on a Reversi board, identified by a row and a column. Rows and
 * columns typically start counting at 0.
 */
public class Square {

  /**
   * A special Square for indicating that no piece was placed.
   */
  public static final Square PASS = new Square(-1, -1);

  private int row, column;

  /**
   * Identifies a square on the Reversi board via the given row and column.
   * 
   * @param row
   *          The row on the Reversi board.
   * @param column
   *          The column on the Reversi board.
   */
  public Square(int row, int column) {
    this.row = row;
    this.column = column;
  }

  /**
   * @return The row of this square on the Reversi board.
   */
  public int getRow() {
    return this.row;
  }

  /**
   * @return The column of this square on the Reversi board.
   */
  public int getColumn() {
    return this.column;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.row, this.column);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Square) {
      Square that = (Square) obj;
      return this.row == that.row && this.column == that.column;
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format("(%d,%d)", this.row, this.column);
  }

}
