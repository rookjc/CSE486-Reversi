package edu.miami.cse.reversi;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AI_Rook_Ritchie implements Strategy {
	private final int dlimit = 3;
	private Player us;

	@Override
	public Square chooseSquare(Board board) {
		Node current = new Node(board, true); // isMax = true because we are maximizer
		us = board.getCurrentPlayer();
		int value = alphaBetaPruningSearch(current, dlimit, Integer.MIN_VALUE, Integer.MAX_VALUE, true); 
		for(Node child : current.children) {
			if(child.value == current.value) {
				return child.action; 
			}
		}
		return null;  // no solution 
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

	// TODO write this evaluation function later

	/**
	 * 
	 * @param board
	 * @return
	 */
	private int evaluation(Board board, Node node) {
		return cornersControlled(board)*30 + tilesScore(board) +
				30*availableMoveScore(board, node);
	} 
	
	private int tilesScore(Board board){
		return board.getPlayerSquareCounts().get(us) - board.getPlayerSquareCounts().get(us.opponent());
	}
	
	private int availableMoveScore(Board board, Node node){
		int ourMoves, theirMoves;
		if(board.getCurrentPlayer() == us){
			ourMoves = board.getCurrentPossibleSquares().size();
			theirMoves = node.parent.board.getCurrentPossibleSquares().size();
		}
		else{
			theirMoves = board.getCurrentPossibleSquares().size();
			ourMoves = node.parent.board.getCurrentPossibleSquares().size();
		}
		
		if(ourMoves + theirMoves == 0)
			return 0;
		
		return (ourMoves - theirMoves)/(ourMoves + theirMoves);
		// This maps the score to the interval [-1,1]
		// -1 if the opponent controls all the moves
		// 0 if the available moves are even
		// 1 if we control all the moves
			
	}
	
	// Returns |our corners| - |their corners|
	private int cornersControlled(Board board)
	{
		Map<Square, Player> mappings = board.getSquareOwners();
		int count = 0;

		
		for(int x = 0; x <= 7; x += 7)
		{
			for(int y = 0; y <= 7; y += 7)
			{
				Player owner = mappings.get(new Square(x,y));
				if(owner == us)
					count++;
				else if(owner == us.opponent())
					count --;
			}
		}
		
		
//		// Top left
//		if(mappings.get(new Square(0,0)) == us)
//			count++;
//		else if(mappings.get(new Square(0,0)) == them)
//			count --;
//		
//		// Bottom left
//		if(mappings.get(new Square(7,0)) == us)
//			count++;
//		else if(mappings.get(new Square(7,0)) == them)
//			count --;
//		
//		// Top right
//		if(mappings.get(new Square(0,7)) == us)
//			count++;
//		else if(mappings.get(new Square(0,7)) == them)
//			count --;
//		
//		// Bottom right
//		if(mappings.get(new Square(7,7)) == us)
//			count++;
//		else if(mappings.get(new Square(7,7)) == them)
//			count --;
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
