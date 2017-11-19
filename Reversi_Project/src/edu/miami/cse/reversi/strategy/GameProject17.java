package edu.miami.cse.reversi.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.miami.cse.reversi.*;

public class GameProject17 implements Strategy {
	private final int dlimit = 3;
	private Player us;

	@Override
	public Square chooseSquare(Board board) {
		Node current = new Node(board, true); // isMax = true because we are
												// maximizer
		us = board.getCurrentPlayer();
		alphaBetaPruningSearch(current, dlimit, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		for (Node child : current.children) {
			if (child.value == current.value) {
				return child.action;
			}
		}
		return null; // no solution
	}

	private int alphaBetaPruningSearch(Node node, int depth, int alpha, int beta, boolean isMax) {
		boolean terminal = node.board.getCurrentPossibleSquares().size() == 0;
		if (depth == 0 || terminal) {
			node.value = evaluation(node.board, node);
			return node.value; // evaluation function
		}

		// generate a list of child nodes in current node
		node.generateChildren();

		// working with the current node
		if (isMax) { // if current level is maximizer
			int value = Integer.MIN_VALUE; // negative infinity for the value of
											// current node

			for (Node child : node.children) {
				value = Math.max(value, alphaBetaPruningSearch(child, depth - 1, alpha, beta, false));
				alpha = Math.max(alpha, value); // replacing alpha with better
												// value
				if (beta <= alpha) {
					break; // pruning the sub-tree
				}
			}
			node.value = value;
			return value;
		} else { // current level is minimizer
			int value = Integer.MAX_VALUE; // positive infinity for the value of
											// current node

			for (Node child : node.children) {
				value = Math.min(value, alphaBetaPruningSearch(child, depth - 1, alpha, beta, true));
				beta = Math.min(beta, value); // replacing alpha with better
												// value
				if (beta <= alpha) {
					break; // pruning the sub-tree
				}
			}
			node.value = value;
			return value;
		}
	}

	/**
	 * The main evaluation function called on a particular board, to estimate
	 * likelihood of winning from it
	 * 
	 * @param board
	 *            the board to evaluation
	 * @return the evaluation value, higher meaning the AI should consider more
	 *         strongly
	 */
	private int evaluation(Board board, Node node) {
		return (int)(winScore(board) + cornersControlled(board) * 30 + tilesScore(board)
				+ 30 * availableMoveScore(board, node) + 20 * cornersAndStablePieces(board));
	}

	/**
	 * Weigh wins much heavier than anything else
	 * 
	 * @param board
	 *            the board to analyze
	 * @return a very high value if we win, a very low value if we lose, or
	 *         otherwise 0
	 */
	private int winScore(Board board) {
		if (board.isComplete()) {
			Player winner = board.getWinner();
			if (winner == us)
				return Integer.MAX_VALUE / 2; // win
			else if (winner == us.opponent())
				return Integer.MIN_VALUE / 2; // loss
			else
				return 0; // tie
		} else {
			// Game isn't over, return 0
			return 0;
		}
	}

	private static final int STABLE_EDGE_WEIGHT = 20;
	private static final int TWO_AWAY_FROM_CORNER_WEIGHT = 10;
	private static final int ONE_AWAY_FROM_CORNER_WEIGHT = -10; // negative, we want to avoid these spaces

	/**
	 * Give preference to corners and pieces 2 away from them, for open corners.
	 * For corners that are taken, increase evaluation for "stable" pieces along
	 * each edge, where "stable" is defined to mean a piece bordering a corner
	 * or another stable piece
	 * 
	 * @param board
	 *            the board to analyze
	 * @return a term for the overall evaluation function
	 */
	private int cornersAndStablePieces(Board board) {
		boolean topEdgeFull = false, rightEdgeFull = false, bottomEdgeFull = false, leftEdgeFull = false;
		Map<Square, Player> mappings = board.getSquareOwners();
		int value = 0;

		// Check top left corner
		Player topLeftPlayer = mappings.get(new Square(0, 0));
		int topLeftStable = 0;
		if (topLeftPlayer != null) {
			// Count stable pieces along the upper edge
			int col = 0;
			while (col < 8 && mappings.get(new Square(0, col)) == topLeftPlayer) {
				topLeftStable++;
				col++;
			}
			if (col == 8)
				topEdgeFull = true;

			// Count stable pieces along the left edge
			int row = 0;
			while (row < 8 && mappings.get(new Square(row, 0)) == topLeftPlayer) {
				topLeftStable++;
				row++;
			}
			if (row == 8)
				leftEdgeFull = true;

			// Factor these stable pieces into the result value
			value += topLeftStable * (topLeftPlayer == us ? STABLE_EDGE_WEIGHT : -STABLE_EDGE_WEIGHT);
		} else {
			// No piece in top left corner, so consider pieces near it
			for (int row = 0; row < 3; row++) {
				for (int col = 0; col < 3; col++) {
					Player playerHere = mappings.get(new Square(row, col));
					if (playerHere == null)
						continue;
					if (Math.max(row, col) == 1 && Math.min(row, col) <= 1)
						value += ONE_AWAY_FROM_CORNER_WEIGHT * (playerHere == us ? 1 : -1);
					else if (row != 1 && col != 1)
						value += TWO_AWAY_FROM_CORNER_WEIGHT * (playerHere == us ? 1 : -1);
				}
			}
		}

		// Check top right corner
		Player topRightPlayer = mappings.get(new Square(0, 7));
		int topRightStable = 0;
		if (topRightPlayer != null) {
			// Count stable pieces along the top edge
			if (!topEdgeFull) {
				int col = 7;
				while (col > -1 && mappings.get(new Square(0, col)) == topRightPlayer) {
					topRightStable++;
					col--;
				}
			}

			// Count stable pieces along the right edge
			int row = 0;
			while (row < 8 && mappings.get(new Square(row, 7)) == topRightPlayer) {
				topRightStable++;
				row++;
			}
			if (row == 8)
				rightEdgeFull = true;

			// Factor these stable pieces into the result value
			value += topRightStable * (topRightPlayer == us ? STABLE_EDGE_WEIGHT : -STABLE_EDGE_WEIGHT);
		} else {
			// No piece in top right corner, so consider pieces near it
			for (int row = 0; row < 3; row++) {
				for (int col = 5; col < 8; col++) {
					Player playerHere = mappings.get(new Square(row, col));
					if (playerHere == null)
						continue;
					if (Math.max(row, 7 - col) == 1 && Math.min(row, 7 - col) <= 1)
						value += ONE_AWAY_FROM_CORNER_WEIGHT * (playerHere == us ? 1 : -1);
					else if (row != 1 && col != 6)
						value += TWO_AWAY_FROM_CORNER_WEIGHT * (playerHere == us ? 1 : -1);
				}
			}
		}

		// Check bottom right corner
		Player bottomRightPlayer = mappings.get(new Square(7, 7));
		int bottomRightStable = 0;
		if (bottomRightPlayer != null) {
			// Count stable pieces along the bottom edge
			int col = 7;
			while (col > -1 && mappings.get(new Square(7, col)) == bottomRightPlayer) {
				bottomRightStable++;
				col--;
			}

			// Count stable pieces along the right edge
			if (!rightEdgeFull) {
				int row = 7;
				while (row > -1 && mappings.get(new Square(row, 7)) == bottomRightPlayer) {
					bottomRightStable++;
					row--;
				}
			}

			// Factor these stable pieces into the result value
			value += bottomRightStable * (bottomRightPlayer == us ? STABLE_EDGE_WEIGHT : -STABLE_EDGE_WEIGHT);
		} else {
			// No piece in bottom right corner, so consider pieces near it
			for (int row = 5; row < 8; row++) {
				for (int col = 5; col < 8; col++) {
					Player playerHere = mappings.get(new Square(row, col));
					if (playerHere == null)
						continue;
					if (Math.max(7 - row, 7 - col) == 1 && Math.min(7 - row, 7 - col) <= 1)
						value += ONE_AWAY_FROM_CORNER_WEIGHT * (playerHere == us ? 1 : -1);
					else if (row != 6 && col != 6)
						value += TWO_AWAY_FROM_CORNER_WEIGHT * (playerHere == us ? 1 : -1);
				}
			}
		}

		// Check bottom left corner
		Player bottomLeftPlayer = mappings.get(new Square(7, 0));
		int bottomLeftStable = 0;
		if (bottomLeftPlayer != null) {
			// Count stable pieces along the bottom edge
			if (!bottomEdgeFull) {
				int col = 0;
				while (col < 8 && mappings.get(new Square(7, col)) == bottomLeftPlayer) {
					bottomLeftStable++;
					col++;
				}
			}

			// Count stable pieces along the left edge
			if (!leftEdgeFull) {
				int row = 7;
				while (row > -1 && mappings.get(new Square(row, 0)) == bottomLeftPlayer) {
					bottomLeftStable++;
					row--;
				}
			}

			// Factor these stable pieces into the result value
			value += bottomLeftStable * (bottomLeftPlayer == us ? STABLE_EDGE_WEIGHT : -STABLE_EDGE_WEIGHT);
		} else {
			// No piece in bottom left corner, so consider pieces near it
			for (int row = 5; row < 8; row++) {
				for (int col = 0; col < 3; col++) {
					Player playerHere = mappings.get(new Square(row, col));
					if (playerHere == null)
						continue;
					if (Math.max(7 - row, col) == 1 && Math.min(7 - row, col) <= 1)
						value += ONE_AWAY_FROM_CORNER_WEIGHT * (playerHere == us ? 1 : -1);
					else if (row != 6 && col != 1)
						value += TWO_AWAY_FROM_CORNER_WEIGHT * (playerHere == us ? 1 : -1);
				}
			}
		}

		return value;
	}

	private int tilesScore(Board board) {
		return board.getPlayerSquareCounts().get(us) - board.getPlayerSquareCounts().get(us.opponent());
	}

	private double availableMoveScore(Board board, Node node) {
		int ourMoves, theirMoves;
		if (board.getCurrentPlayer() == us) {
			ourMoves = board.getCurrentPossibleSquares().size();
			theirMoves = node.parent.board.getCurrentPossibleSquares().size();
		} else {
			theirMoves = board.getCurrentPossibleSquares().size();
			ourMoves = node.parent.board.getCurrentPossibleSquares().size();
		}

		if (ourMoves + theirMoves == 0)
			return 0;

		return 1.0 * (ourMoves - theirMoves) / (ourMoves + theirMoves);
		// This maps the score to the interval [-1,1]
		// -1 if the opponent controls all the moves
		// 0 if the available moves are even
		// 1 if we control all the moves

	}

	// Returns |our corners| - |their corners|
	private int cornersControlled(Board board) {
		Map<Square, Player> mappings = board.getSquareOwners();
		int count = 0;

		for (int x = 0; x <= 7; x += 7) {
			for (int y = 0; y <= 7; y += 7) {
				Player owner = mappings.get(new Square(x, y));
				if (owner == us)
					count++;
				else if (owner == us.opponent())
					count--;
			}
		}

		// // Top left
		// if(mappings.get(new Square(0,0)) == us)
		// count++;
		// else if(mappings.get(new Square(0,0)) == them)
		// count --;
		//
		// // Bottom left
		// if(mappings.get(new Square(7,0)) == us)
		// count++;
		// else if(mappings.get(new Square(7,0)) == them)
		// count --;
		//
		// // Top right
		// if(mappings.get(new Square(0,7)) == us)
		// count++;
		// else if(mappings.get(new Square(0,7)) == them)
		// count --;
		//
		// // Bottom right
		// if(mappings.get(new Square(7,7)) == us)
		// count++;
		// else if(mappings.get(new Square(7,7)) == them)
		// count --;
		//
		return count;
	}

	private class Node {
		private Node parent;
		// private int alpha;
		// private int beta;
		private int value; // minimax value
		private Board board;
		private List<Node> children;
		private Square action; // the square chosen to reach this node
		private boolean isMax; // indicate if current node is maximizer or
								// minimizer

		public Node(Board board, boolean isMax) {
			// this.parent = parent;
			// this.alpha = alpha;
			// this.beta = beta;
			this.board = board;
			this.value = 0; // to be generated later
			this.children = new ArrayList<>();
			this.isMax = isMax;
		}

		@Override
		public String toString() {
			return super.toString();
		}

		public void generateChildren() {
			for (Square sq : board.getCurrentPossibleSquares()) {
				Node node = new Node(board.play(sq), !this.isMax);
				node.action = sq;
				node.parent = this;
				this.children.add(node);
			}
		}
	}
}