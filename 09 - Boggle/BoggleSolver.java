import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

/**
 * Finds all valid words on a Boggle board as quickly as possible.
 * 
 *
 */
public class BoggleSolver {

	// radix = 26 = english uppercase letters
	// shift = 65: allows shifting all returned charAt integer values by this value
	// to put their values between 0--25, to allow correct indexing into the Node
	// array 'next' e.g. 'A' => (65 - SHIFT) => 0
	private static final int RADIX = 26, SHIFT = 65;
	// init root of internal trie structure for cases of null dictionary
	private Node root = new Node();
	private SET<String> words;
	private boolean[][] marked;
	private BoggleBoard board;
	private int rows, cols;

	/**
	 * Node only needs to return true when the node under examination represents the
	 * last letter of a valid word. the Node array represents letters that this node
	 * can lead to.
	 */
	private static class Node {
		boolean hasWord;
		String word;
		int score;
		Node[] next = new Node[RADIX];
	}

	/**
	 * Initializes the data structure using the given array of strings as the
	 * dictionary.
	 * <p>
	 * (Assume each word in the dictionary contains only the uppercase letters A
	 * through Z)
	 * 
	 * @param dictionary a .txt file of valid words
	 */
	public BoggleSolver(String[] dictionary) {
		for (String word : dictionary) {
			if (word.length() > 2 && qcheck(word))
				put(word);
		}
	}

	// if the word has 'Q' and not 'Qu', return false
	private boolean qcheck(String word) {
		if (word.contains("Q"))
			return word.contains("QU");
		return true;
	}

	// kick off the recursive put routine
	private void put(String key) {
		root = put(root, key, 0);
	}

	// the recursive routine for placing characters in the trie
	private Node put(Node x, String key, int d) {

		// when this put encounters untouched ground
		if (x == null)
			x = new Node();

		// when d is the length of the word, fill the node data
		if (d == key.length()) {
			x.hasWord = true;
			x.word = key;
			x.score = score(key, d);
			return x;
		}

		// otherwise continue placing characters down the trie
		int c = key.charAt(d) - SHIFT;
		x.next[c] = put(x.next[c], key, d + 1);
		return x;
	}

	// uses the basic parts of the trie get() method and exploits this to move
	// through the boggle board and populate the results
	private void search(int row, int col, Node node) {
		// return if search has found a null link
		if (node == null)
			return;

		// when this character is 'Q', move to the 'U' on the next node down the trie
		if (board.getLetter(row, col) == 'Q') {
			node = node.next['U' - SHIFT];
			if (node == null)
				return;
		}

		// if this node has a valid word, collect it
		if (node.hasWord)
			words.add(node.word);

		// mark this position as visited before recursing search
		marked[row][col] = true;

		for (Integer[] coord : adjacents(row, col)) {
			int nextRow = coord[0], nextCol = coord[1];
			if (!marked[nextRow][nextCol]) {
				search(nextRow, nextCol, node.next[board.getLetter(nextRow, nextCol) - SHIFT]);
			}
		}

		// mark this position as unvisited when leaving this path, for correct search
		// results from different future recursive paths that touch this position again.
		marked[row][col] = false;

	}
	// unecessary array copying method, replaced by marking and unmarking positions
	// in saearch()
//	private boolean[][] arrayClone(boolean[][] input) {
//		boolean[][] copied = new boolean[input.length][input[0].length];
//		for (int row = 0; row < input.length; row++) {
//			for (int col = 0; col < input[0].length; col++) {
//				copied[row][col] = input[row][col];
//			}
//		}
//		return copied;
//	}

	private Bag<Integer[]> adjacents(int row, int col) {
		Bag<Integer[]> adj = new Bag<Integer[]>();

		// get any coords for all positions above
		if (row > 0) {
			adj.add(new Integer[] { row - 1, col });
			if (col > 0)
				adj.add(new Integer[] { row - 1, col - 1 });
			if (col < cols - 1)
				adj.add(new Integer[] { row - 1, col + 1 });
		}

		// coords below
		if (row < rows - 1) {
			adj.add(new Integer[] { row + 1, col });
			if (col > 0)
				adj.add(new Integer[] { row + 1, col - 1 });
			if (col < cols - 1)
				adj.add(new Integer[] { row + 1, col + 1 });
		}

		// directly left and right
		if (col > 0)
			adj.add(new Integer[] { row, col - 1 });
		if (col < cols - 1)
			adj.add(new Integer[] { row, col + 1 });

		return adj;
	}

	/**
	 * Returns the set of all valid words in the given Boggle board, as an Iterable.
	 * 
	 * @param board a set of BoggleBoard dice results
	 * @return a collection of words
	 */
	public Iterable<String> getAllValidWords(BoggleBoard board) {

		// set internal board & dimensions
		this.board = board;
		this.rows = board.rows();
		this.cols = board.cols();

		// init new results collection
		words = new SET<String>();

		// for every coordinate x,y ( row, col ) on the boggleboard, do a search
		for (int row = 0; row < rows; row++) {
			for (int col = 0; col < cols; col++) {

				// array recording searches
				marked = new boolean[rows][cols];
//				marked[row][col] = true;

				// recursive magic for each die position:
				search(row, col, root.next[board.getLetter(row, col) - SHIFT]);
			}
		}
		return words;
	}

	/**
	 * If the dictionary has the word, returns the score.
	 * 
	 * @param word the word to score
	 * @return an integer for the score
	 */
	public int scoreOf(String word) {
		Node x = get(word);
		if (x != null && x.hasWord)
			return x.score;
		else
			return 0;
	}

	// for setting internal dictionary word scores during construction
	private int score(String word, int length) {
		if (length < 5)
			return 1;
		if (length == 5)
			return 2;
		if (length == 6)
			return 3;
		if (length == 7)
			return 5;
		else
			return 11;
	}

	private Node get(String word) {
		return get(root, word, 0);
	}

	private Node get(Node node, String word, int i) {
		if (node == null)
			return null;
		if (word.length() == i)
			return node;
		else
			return get(node.next[word.charAt(i) - SHIFT], word, i + 1);
	}

	// supplied main
	public static void main(String[] args) {
		In in = new In(args[0]);
		String[] dictionary = in.readAllStrings();
		BoggleSolver solver = new BoggleSolver(dictionary);
		BoggleBoard board = new BoggleBoard(args[1]);
		int score = 0;
		for (String word : solver.getAllValidWords(board)) {
			StdOut.println(word);
			score += solver.scoreOf(word);
		}
		StdOut.println("Score = " + score);
	}

	// final bug squash testing to get 100%
//	public static void main(String[] args) {
//		In in = new In("project/dictionary-2letters.txt");
//		String[] dictionary = in.readAllStrings();
//		BoggleSolver solver = new BoggleSolver(dictionary);
//		BoggleBoard board = new BoggleBoard("project/board-points4410.txt");
//		int score = 0;
//		for (String word : solver.getAllValidWords(board)) {
//			StdOut.println(word);
//			score += solver.scoreOf(word);
//		}
//		StdOut.println("Score = " + score);
//	}

	// testing main
//	public static void main(String[] args) {
//		Stopwatch timer = new Stopwatch();
//
//		In in = new In(args[0]);
//		String[] dictionary = in.readAllStrings();
//		BoggleSolver bs = new BoggleSolver(dictionary);
//
//		int record = 83, score = 0, count = 0;
//		while (score < record) {
//
//			score = 0;
//			BoggleBoard board = new BoggleBoard("project/board-q.txt");
//			for (String word : bs.getAllValidWords(board)) {
////				if (word.contains("QU"))
////					System.out.println(word);
//				score += bs.scoreOf(word);
//			}
//			count++;
//		}
//
//		System.out.println(score == 84);
//		System.out.println(timer.elapsedTime());
////		System.out.println(score + "\n" + timer.elapsedTime() + " / " + count);
//	}
//	    BoggleBoard board = new BoggleBoard(args[1]);
//	    int score = 0;
//	    for (String word : solver.getAllValidWords(board)) {
//	        StdOut.println(word);
//	        score += solver.scoreOf(word);
//	    }
//	    StdOut.println("Score = " + score);
//	}

}
