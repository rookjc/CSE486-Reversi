package edu.miami.cse.reversi;

import java.util.ArrayList;
import java.util.List;

public class AI_Rook_Ritchie implements Strategy {

	@Override
	public Square chooseSquare(Board board) {
		// TODO Auto-generated method stub
		return null;
	}

	private static class Node {
		private Node parent;
		private int alpha;
		private int beta;
		private int value; // minimax value
		private Board board;
		private List<Node> children;
		
		public Node(Node parent, int alpha, int beta, Board board) {
			this.parent = parent;
			this.alpha = alpha;
			this.beta = beta;
			this.board = board;
			this.value = 0; // to be generated later
			this.children = new ArrayList<>();
		}
	}
}
