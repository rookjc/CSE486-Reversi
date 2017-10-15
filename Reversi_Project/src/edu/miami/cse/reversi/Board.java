package edu.miami.cse.reversi;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.pcollections.HashTreePMap;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSequence;
import org.pcollections.PSet;
import org.pcollections.TreePVector;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

/**
 * A single state of a Reversi board. It records which pieces have been played
 * so far by which players, which pieces have been captured, and who the next
 * player to play should be.
 */
public class Board {
	private int size;
	private Player player;
	private PMap<Square, Player> owners;
	private PSequence<Move> moves;
	private PMap<Square, PSet<Square>> possibleSquares;
	private PMap<Player, Integer> playerSquareCounts;

	/**
	 * Creates an 8x8 Reversi board with the standard initial configuration of
	 * {@link Player#BLACK} and {@link Player#WHITE} pieces.
	 */
	public Board() {
		this(8, Player.BLACK, TreePVector.<Move> empty(), getInitialOwners(8), getInitialPlayerSquareCounts());
	}


	/**
	 * Low-level constructor. Intended only for internal use.
	 */
	private Board(int size, Player player, PSequence<Move> moves, PMap<Square, Player> owners, PMap<Player, Integer> playerSquareCounts) {
		this.size = size;
		this.player = player;
		this.moves = moves;
		this.owners = owners;
		this.possibleSquares = HashTreePMap.empty();
		this.playerSquareCounts = playerSquareCounts;
		// Determine the possible moves
		Player opponent = this.player.opponent();
		int[][] directions =
				new int[][] { { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 }, { -1, 0 },
			{ -1, 1 } };
			for (int row = 0; row < this.size; ++row) {
				for (int column = 0; column < this.size; ++column) {
					if (this.owners.get(new Square(row, column)) == null) {
						PSet<Square> allCaptures = HashTreePSet.empty();
						for (int[] direction : directions) {
							int rowStep = direction[0];
							int columnStep = direction[1];
							int r = row + rowStep;
							int c = column + columnStep;
							PSet<Square> captures = HashTreePSet.empty();
							if (this.hasOwner(r, c, opponent)) {
								captures = captures.plus(new Square(r, c));
								r += rowStep;
								c += columnStep;
								while (this.hasOwner(r, c, opponent)) {
									captures = captures.plus(new Square(r, c));
									r += rowStep;
									c += columnStep;
								}
								if (this.hasOwner(r, c, this.player)) {
									allCaptures = allCaptures.plusAll(captures);
								}
							}
						}
						if (!allCaptures.isEmpty()) {
							this.possibleSquares = this.possibleSquares.plus(new Square(row, column), allCaptures);
						}
					}
				}
			}
	}

	/**
	 * Utility method for simultaneously checking bounds and owner. Intended only
	 * for internal use.
	 */
	private boolean hasOwner(int row, int column, Player owner) {
		return 0 <= row && row < this.size && 0 <= column && column < this.size
				&& this.owners.get(new Square(row, column)) == owner;
	}

	/**
	 * Utility method for generating the initial board configuration. Intended only
	 * for internal use.
	 */
	private static PMap<Square, Player> getInitialOwners(int size) {
		PMap<Square, Player> owners = HashTreePMap.empty();
		int mid = size / 2;
		owners = owners.plus(new Square(mid - 1, mid - 1), Player.WHITE);
		owners = owners.plus(new Square(mid - 1, mid), Player.BLACK);
		owners = owners.plus(new Square(mid, mid - 1), Player.BLACK);
		owners = owners.plus(new Square(mid, mid), Player.WHITE);
		return owners;
	}

	private static PMap<Player, Integer> getInitialPlayerSquareCounts() {
		return HashTreePMap.<Player, Integer> empty().plus(Player.BLACK, 2).plus(Player.WHITE, 2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.size, this.owners);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Board) {
			Board that = (Board) obj;
			return this.size == that.size && Objects.equals(this.owners, that.owners);
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int row = 0; row < this.size; ++row) {
			for (int col = 0; col < this.size; ++col) {
				Player owner = this.owners.get(new Square(row, col));
				if (owner == Player.WHITE) {
					builder.append('W');
				} else if (owner == Player.BLACK) {
					builder.append('B');
				} else {
					builder.append('_');
				}
			}
			builder.append('\n');
		}
		return builder.toString();
	}

	/**
	 * @return The number of rows (= the number of columns) in this Reversi board.
	 */
	public int size() {
		return this.size;
	}

	/**
	 * @return The moves made by the players so far. That is, each square where a
	 *         piece has been placed along with the player who played the piece
	 *         there.
	 */
	public List<Move> getMoves() {
		return this.moves;
	}

	/**
	 * @return A mapping from squares to the players currently occupying them.
	 */
	public Map<Square, Player> getSquareOwners() {
		return this.owners;
	}

	/**
	 * @return The count of squares currently occupied by each player.
	 */
	public Map<Player, Integer> getPlayerSquareCounts() {
		return this.playerSquareCounts;
	}

	/**
	 * @return True if no squares remain that can be played by either player, false
	 *         otherwise.
	 */
	public boolean isComplete() {
		return this.possibleSquares.isEmpty() && this.pass().possibleSquares.isEmpty();
	}

	/**
	 * @return The winner on this Reversi board. Only valid after
	 *         {@link #isComplete()} returns true.
	 */
	public Player getWinner() {
		if (!this.isComplete()) {
			throw new IllegalStateException("getWinner cannot be called until the game is complete");
		}
		Ordering<Player> bySquares = Ordering.natural().onResultOf(new Function<Player, Integer>() {
			@Override
			public Integer apply(Player input) {
				return getPlayerSquareCounts().get(input);
			}
		});
		Player winner = bySquares.max(Arrays.asList(Player.values()));
		return this.playerSquareCounts.get(winner) == this.playerSquareCounts.get(winner.opponent())
				? null
						: winner;
	}

	/**
	 * @return The player that gets to choose a square next.
	 */
	public Player getCurrentPlayer() {
		return this.player;
	}

	/**
	 * @return The possible valid moves that the current player may choose from.
	 */
	public Set<Square> getCurrentPossibleSquares() {
		return this.possibleSquares.keySet();
	}

	/**
	 * Places a game piece for the current player at the given square.
	 * 
	 * @param square
	 *          The square where the current player would like to place their piece.
	 *          Must be a valid play: there must not already be a piece there, and
	 *          placing a piece there must capture at least one new piece for the
	 *          current player.
	 * @return The new board with the given piece played, with all pieces that were
	 *         captured by this play now occupied by the player, and with the
	 *         current player now set to the opponent.
	 */
	public Board play(Square square) {
		Player existingPlayer = this.owners.get(square);
		if (existingPlayer != null) {
			String message = "A %s piece already exists at %s";
			throw new IllegalArgumentException(String.format(message, existingPlayer, square));
		}
		PSet<Square> captures = this.possibleSquares.get(square);
		if (captures == null) {
			String message = "%s will not capture any pieces if placed at (%d,%d)";
			throw new IllegalArgumentException(String.format(message, this.player, square));
		}
		PSequence<Move> newMoves = this.moves.plus(new Move(square, this.player));
		PMap<Square, Player> newOwners = this.owners.plus(square, this.player);
		for (Square capture : captures) {
			newOwners = newOwners.plus(capture, this.player);
		}
		Player opponent = this.player.opponent();
		int playerSquareCount = this.playerSquareCounts.get(this.player) + captures.size() + 1;
		int opponentSquareCount = this.playerSquareCounts.get(opponent) - captures.size();
		PMap<Player, Integer> newPlayerSquareCounts = this.playerSquareCounts;
		newPlayerSquareCounts = newPlayerSquareCounts.plus(this.player, playerSquareCount);
		newPlayerSquareCounts = newPlayerSquareCounts.plus(opponent, opponentSquareCount);
		return new Board(this.size, opponent, newMoves, newOwners, newPlayerSquareCounts);
	}

	/**
	 * Passes the current player's turn. Only valid when there are no possible
	 * capturing moves for the current player.
	 * 
	 * @return A new board with the same layout as the current one, but with the
	 *         current player now set to the opponent.
	 */
	public Board pass() {
		Set<Square> validNextMoves = this.getCurrentPossibleSquares();
		if (!validNextMoves.isEmpty()) {
			String message = "%s cannot pass since there are valid moves: %s";
			throw new IllegalArgumentException(String.format(message, this.player, validNextMoves));
		}
		Player opponent = this.player.opponent();
		PSequence<Move> newMoves = this.moves.plus(new Move(Square.PASS, this.player));
		return new Board(this.size, opponent, newMoves, this.owners, this.playerSquareCounts);
	}

}
