
import java.util.Iterator;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

/**
 * Implementation of <b>A*</b> algorithm with a minimum priority queue. Attempts
 * to solve a sliding-block puzzle game board, represented by the Board class.
 * Returns either: a number representing the moves required to solve the puzzle,
 * or will state that the given board cannot be solved.
 *
 */
public class Solver {

	private Board board;
	private Board mutant;
	private Stack<Board> solutionStack;
	private Node solutionNode;
	private int moves;
	private boolean solvable;

	/**
	 * Attempts to find a solution to the board which is given as parameter.
	 *
	 * @param a Board object
	 */
	public Solver(Board initial) {
		// Throw an IllegalArgumentException in the constructor if the argument is null
		if (initial == null) {
			throw new IllegalArgumentException("given board is null");
		}
		// point local variable to input Board
		board = initial;

		// create mutant Board to satisfy solvability problem. returned board has one
		// extra inversion to highlight which board can be solved
		mutant = board.twin();

		/**
		 * set up for side-by-side testing: for given board and the mutant, initially
		 * place each to their own minPQueue, as the main loop for each board will
		 * always need to remove the top object for inspection.
		 */
		SolutionPQ proof = new SolutionPQ();
		Node initialBoard = new Node(board, null, 0);
		proof.insert(initialBoard);

		SolutionPQ disproof = new SolutionPQ();
		Node initialMutant = new Node(mutant, null, 0);
		disproof.insert(initialMutant);

		/**
		 * do the magic: make one step each with initial and twin until one is solved.
		 */
		while (true) {
			if (doOneCycle(proof)) {
				solvable = true;
				break;
			}
			if (doOneCycle(disproof)) {
				solvable = false;
				moves = -1;
				break;
			}
		}
	}

	private boolean doOneCycle(SolutionPQ pq) {
		// get top priority Node from queue
		Node current = pq.removeTop();
//		System.out.println(current.state);
		// if it is in the goal state, return true
		if (current.state.isGoal()) {
			solutionNode = current; // set local variable to found solution
			doSuccessTasks();
			return true;
		} // otherwise place children on queue to be prioritised
		if (current.generation == 0) { // initial case where there are no grandparents to check against
			for (Board child : current.state.neighbors()) {
				pq.insert(new Node(child, current, current.generation + 1));
			}
		} else { // only add children that aren't like their grandparent
			for (Board child : current.state.neighbors()) {
				if (!child.equals(current.parent.state)) {
					pq.insert(new Node(child, current, current.generation + 1));
				}
			}
		}
		return false;
	}

	// is the initial board solvable?
	public boolean isSolvable() {
		return solvable;
	}

	// min number of moves to solve initial board
	public int moves() {
		return moves;
	}

//     sequence of boards in a shortest solution
	public Iterable<Board> solution() {
		if (solvable) {
			Queue<Board> solutionQueue = new Queue<>();
			Iterator<Board> iter = solutionStack.iterator();
			while (iter.hasNext()) {
				solutionQueue.enqueue(iter.next());
			}
			return solutionQueue;
		} else
			return null;
	}

	// tasks to do if initial puzzle is solved
	private void doSuccessTasks() {
		// set 'moves' local variable
		moves = solutionNode.generation;
		// populate a collection of boards. collection must be iterable such that each
		// board in collection's order illustrates each step towards solution
		solutionStack = new Stack<>();
		Node current = solutionNode;
		while (current != null) {
			solutionStack.push(current.state);
			current = current.parent;
		}
	}

	private class SolutionPQ {
		private MinPQ<Node> pq;

		public SolutionPQ() {
			pq = new MinPQ<Node>();
		}

		public void insert(Node node) {
			pq.insert(node);
		}

		public Node removeTop() {
			return pq.delMin();
		}

	}

	/**
	 * Nested class for use with SolutionPQ priority queue.
	 * 
	 * If we reach a state where the board equals the goal, we can then determine
	 * the solution by tracking back through each previous/parent to the initial
	 * state.
	 *
	 */
	private class Node implements Comparable<Node> {

		public Board state; // this state
		public Node parent; // previous state
		public int generation; // number of generations away from initial state
		private int manhattan; // store manhattan value to reduce method calls

		public Node(Board board, Node parent, int generation) {
			this.state = board;
			this.parent = parent;
			this.generation = generation;
			this.manhattan = state.manhattan();
		}

		/**
		 * the all-important priority function!
		 * 
		 * Return value determines how SolutionPQ will sort/prioritise each received
		 * Node. Consider: weighting of manhattan against internal 'moves' counter.
		 */
		@Override
		public int compareTo(Node other) {
			return (manhattan + generation) - (other.manhattan + other.generation);
		}
	}

	/**
	 * The following test client takes the name of an input file as a command-line
	 * argument and prints the minimum number of moves to solve the puzzle and a
	 * corresponding solution.
	 *
	 * @param args input file containing an initial board state
	 */
	public static void main(String[] args) {
		// create initial board from file
		In in = new In(args[0]);
		int n = in.readInt();
		int[][] tiles = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				tiles[i][j] = in.readInt();
			}
		}
		Board initial = new Board(tiles);

		// solve the puzzle
		Solver solver = new Solver(initial);

		// print solution to standard output
		if (!solver.isSolvable()) {
			StdOut.println("No solution possible");
		} else {
			StdOut.println("Minimum number of moves = " + solver.moves());
			for (Board board : solver.solution()) {
				StdOut.println(board);
			}
		}
	}

}
