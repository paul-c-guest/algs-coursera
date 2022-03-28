import java.util.Iterator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

/**
 * A sliding-puzzle board object for use by Solver class. Part of Princeton
 * algorithms course part 1 week 4.
 *
 */
public class Board {

	private final int n;
	private int[][] board;
//	private int[][] goal; redundant, isGoal() utilises hamming value
//	private int[] hole; unused due to memory constraints
	private int erow; // row of empty space
	private int ecol; // column of empty space

	/**
	 * the constructor receives an n-by-n array containing the n2 integers between 0
	 * and n2 − 1, where 0 represents the blank square; and tiles[row][col] = tile
	 * at (row, col)
	 *
	 * Assume for ease of testing that 2 ≤ n < 128
	 *
	 * @param tiles
	 */
	public Board(int[][] tiles) {
		// instantiate elements
		n = tiles[0].length;
		board = new int[n][n];

		// deep copy each element to local board
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				board[i][j] = tiles[i][j];
				// test for 0 position and record it as the hole
				if (board[i][j] == 0) {
					erow = i;
					ecol = j;
				}
			}
		}
		// populate the goal for reference
//		setGoalBoard();
	}

//	/**
//	 * create an int[][] to use as a reference to the goal solution. position 0,0 is
//	 * 1, position n-1,n is last tile. position n,n is the empty slot.
//	 */
//	private void setGoalBoard() {
//		goal = new int[n][n];
//		int i = 1;
//		while (i < (n * n)) {
//			for (int row = 0; row < n; row++) {
//				for (int col = 0; col < n; col++) {
//					goal[row][col] = i++;
//				}
//			}
//		}
//	}

	/**
	 * returns a string composed of n + 1 lines. The first line contains the board
	 * size n; the remaining n lines contains the n-by-n grid of tiles in row-major
	 * order, using 0 to designate the blank square
	 */
	public String toString() {
		return toString(board);
	}

	private String toString(int[][] board) {
		StringBuilder s = new StringBuilder();
		s.append(n + "\n");
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				s.append(" " + board[i][j] + " ");
			}
			s.append("\n");
		}
//		s.append("mh: " + manhattan() + " hm: " + hamming());
//		s.append("\n");
		return s.toString();
	}

	// board dimension n
	public int dimension() {
		return n;
	}

	private Board copyBoard() {
		return new Board(board);
	}

	/**
	 *  hamming distance is the number of tiles not in their correct position
	 * @return integer 
	 */
	public int hamming() {
		int hamming = 0;
		int expected = 1;
		for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				int current = board[row][col];
				if (current != expected && current != 0) {
					hamming++;
				}
				expected++;
			}
		}
		return hamming;
	}

	/**
	 * sum of Manhattan distances between tiles and goal. calculated by summing
	 * absolute x and y differences between current positions and ideal positions of
	 * each position of the board.
	 * 
	 * @return the total manhattan distance
	 */
	public int manhattan() {
		int mDist = 0, expected = 1;
		for (int row = 0; row < n; row++) {
			for (int col = 0; col < n; col++) {
				int current = board[row][col];
				if (current != expected && current != 0) {
					mDist += (Math.abs(((current - 1) / n) - row)) // row distance out of position
							+ (Math.abs(((current - 1) % n) - col)); // col distance out of position
				}
				expected++;
			}
		}
		return mDist;
	}

	// is this board the goal board?
	public boolean isGoal() {
		return hamming() == 0;
	}

	/**
	 * Two boards are equal if they are have the same size and their corresponding
	 * tiles are in the same positions. The equals() method is inherited from
	 * java.lang.Object, so it must obey all of Java’s requirements
	 */
	public boolean equals(Object in) {
		if (in == null || in.getClass() != this.getClass())
			return false;

		Board that = (Board) in;

		return this.toString().equals(that.toString());
	}

	/**
	 * Returns a collection of all possible neighbouring boards - returned iterable
	 * can only possibly contain 2, 3 or 4 boards.
	 * 
	 * A neighbour is one valid single step into the future.
	 * 
	 * Place each of the four future Board objects on a Minimum PQ if that board is
	 * possible. Each of four exclusive cases will add a modified board if the hole
	 * is not at the side being checked.
	 * 
	 */
	public Iterable<Board> neighbors() {
		FutureNeighbours pque = new FutureNeighbours();
		if (erow > 0) { // there exists a tile above the hole which will slide
			Board down = this.copyBoard();
			down.move(erow - 1, ecol);
			pque.inviteToParty(down);
		}
		if (erow < n - 1) { // as above, for each direction
			Board up = this.copyBoard();
			up.move(erow + 1, ecol);
			pque.inviteToParty(up);
		}
		if (ecol > 0) {
			Board left = this.copyBoard();
			left.move(erow, ecol - 1);
			pque.inviteToParty(left);
		}
		if (ecol < n - 1) {
			Board right = this.copyBoard();
			right.move(erow, ecol + 1);
			pque.inviteToParty(right);
		}
		return pque;
	}

	/**
	 * a standard fifo queue wrapped in an iterable class used as temporary storage
	 * of boards when determining valid neighbours of a board
	 * 
	 */
	private class FutureNeighbours implements Iterable<Board> {
		private Queue<Board> queue;

		public FutureNeighbours() {
			queue = new Queue<Board>();
		}

		public void inviteToParty(Board b) {
			queue.enqueue(b);
		}

		@Override
		public Iterator<Board> iterator() {
			return queue.iterator();
		}

	}

	/**
	 * calculates an int to use as comparator of priority over neighbouring Boards
	 * 
	 * @param other
	 * @return priority function
	 */
//	public int compareTo(Board other) {
//		return (this.manhattan() + this.hamming()) - (other.manhattan() + other.hamming());
//	}

	// a board that is obtained by exchanging any pair of tiles
	public Board twin() {
		// copy the Board and swap first two non-empty tile positions
		Board twin = this.copyBoard();
		if (twin.erow > 0)
			twin.swap(0, 0, 0, 1); // swap first two tiles on row 0
		else
			twin.swap(1, 0, 1, 1); // swap first two tiles on row 1
		return twin;

	}

	/**
	 * Swap ANY two entries on the board. Will not check validity of swap - take
	 * care!
	 * 
	 * (use move() for sliding a tile to empty hole)
	 * 
	 */
	private void swap(int rowA, int colA, int rowB, int colB) {
		// swap the argument tiles
		int swap = board[rowA][colA];
		board[rowA][colA] = board[rowB][colB];
		board[rowB][colB] = swap;
		// update hole position if necessary
		if (erow == rowA && ecol == colA) {
			erow = rowB;
			ecol = colB;
		} else if (erow == rowB && ecol == colB) {
			erow = rowA;
			ecol = colA;
		}
	}

	// slide a real tile to the empty position
	private void move(int row, int col) {
		swap(row, col, erow, ecol);
		toString();
	}

	/**
	 * unit testing (not graded): Your main() method should call each public method
	 * directly and help verify that they works as prescribed (e.g., by printing
	 * results to standard output).
	 */
	public static void main(String[] args) {

		In in = new In(args[0]);
		int n = in.readInt();
		int[][] tiles = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				tiles[i][j] = in.readInt();
			}
		}
		Board b = new Board(tiles);
		System.out.println(b.toString());
		pretend(b, 0, 2);
		pretend(b, 1, 2);
		for (Board brd : b.neighbors()) {
			System.out.println("iteration:\n" + brd);
		}
	}

	private static void pretend(Board b, int row, int col) {
		b.move(row, col);
		System.out.println(b.toString());
	}

}
