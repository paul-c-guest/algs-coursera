import java.util.Map;
import java.util.TreeMap;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;

/**
 * <h1>Shortest Ancestral Path</h1>
 * <p>
 * An object which calculates relevant values of <b>S</b>hortest
 * <b>A</b>ncestral <b>P</b>aths, between two vertices (or two sets of
 * vertices). Object can return either the <i>length</i> or the
 * <i>shortest/closest common ancestor</i> for the given arguments.
 * <p>
 */
public class SAP {

	private final int[] nullResult = new int[] { -1, -1, -1 };
	private int limit;
	private Digraph dg;
//	private int[][] stepsTo;
//	private boolean[][] marked;
	private Map<Integer, Integer> vmap, wmap;
	private Queue<Integer> vQ, wQ, swap;

	/*
	 * Constructor takes <u>any</u> valid digraph. Wordnet objects will pass a
	 * rooted DAG - however note that the SAP should return valid length and
	 * ancestor results for <i>any </i>form of directional graph.
	 */
	public SAP(Digraph G) {

		if (G == null)
			throw new IllegalArgumentException("incoming digraph can not be null");

//		Stopwatch time = new Stopwatch();

		dg = new Digraph(G);
		vQ = new Queue<Integer>();
		wQ = new Queue<Integer>();
		swap = new Queue<Integer>();

		vmap = new TreeMap<Integer, Integer>();
		wmap = new TreeMap<Integer, Integer>();

//		System.out.println(time.elapsedTime());

	}

	/**
	 * length of shortest directed ancestral path between v and w; i.e. number of
	 * steps from v to w through their closest common ancestor
	 * <p>
	 * shortest path will necessarily go through the closest common ancestor. the
	 * ancestor can be one of the given arguments (i.e. a vertex can stand as its
	 * own ancestor)
	 * 
	 * @return the sum of the lengths between the common ancestor and the given
	 *         vertices, or -1 if there is no such path
	 * @param v int representing a vertex
	 * @param w int representing a vertex
	 */
	public int length(int v, int w) {
		validIntCheck("length", v, w);

		// first case, length is zero if args are equal
		if (v == w)
			return 0;

		limit = dg.V();

		resetFor(v, w);

		return getValues(v, w)[1];

	}

	/**
	 * the closest / shortest common ancestor for v and w that also participates in
	 * the shortest ancestral path; -1 if no such path the ancestor can be one of
	 * the given vertices (i.e. a vertex can stand as its own ancestor)
	 * 
	 * @return the integral value of the common ancestor
	 */
	public int ancestor(int v, int w) {
		validIntCheck("ancestor", v, w);

		// first case, if args are equal no need to search
		if (v == w)
			return v;

		limit = dg.V();

		resetFor(v, w);

		return getValues(v, w)[0];
	}

	/**
	 * do breadth first path searches in lockstep from the two argument vertices
	 * 
	 * @param v
	 * @param w
	 * @return the ancestor and length in an int[] { ancestor, length }
	 */
	private int[] getValues(int v, int w) {

//		System.out.println("searching for values for [" + v + ", " + w + "]");

		int[] result = nullResult.clone();

//		Bag<Integer> vBag = new Bag<Integer>();
//		vBag.add(v);

		int steps = 1, current = -1;

		// main loop - TODO move limiting cases to true clause
		while (true) {

			// limiting case 1
			if (steps > limit)
				break;

			// limiting case 2, test unit speeds with and without
			if (vQ.isEmpty() && wQ.isEmpty())
				break;

			// v loop: process next adjacent vertices
			while (!vQ.isEmpty()) {
				current = vQ.dequeue();
				for (int adj : dg.adj(current)) {
					if (!vmap.containsKey(adj)) {
						vmap.put(adj, steps);
						swap.enqueue(adj);
//						vBag.add(adj);
					}
//					if (!marked[0][adj]) {
//						marked[0][adj] = true;
//						stepsTo[0][adj] = steps;
//						swap.enqueue(adj);
//						vBag.add(adj);
//					}
				}
			}
			while (!swap.isEmpty()) {
				vQ.enqueue(swap.dequeue());
			}

			// w loop
			while (!wQ.isEmpty()) {
				current = wQ.dequeue();
				for (int adj : dg.adj(current)) {
					if (!wmap.containsKey(adj)) {
						wmap.put(adj,steps);
						swap.enqueue(adj);
					}
//					if (!marked[1][adj]) {
//						marked[1][adj] = true;
//						stepsTo[1][adj] = steps;
//						swap.enqueue(adj);
//					}
				}
			}
			while (!swap.isEmpty()) {
				wQ.enqueue(swap.dequeue());
			}

			for (int sca : vmap.keySet()) {
				if (wmap.containsKey(sca)) {
//					int vdist = vmap.get(sca), wdist = wmap.get(sca);
					int length = vmap.get(sca) + wmap.get(sca);
					if (result[0] == -1 || length < result[1]) {
						result[0] = sca;
						result[1] = length;
						limit = length;
					}
				}
			}
			
			// test vBag against marked array w for matches
			// and for any hits set result fields & lower the limit
//			for (int ancestor : vBag) {
//				if (marked[1][ancestor]) {
//					int vDistance = stepsTo[0][ancestor], wDistance = stepsTo[1][ancestor];
//					int totalDist = vDistance + wDistance;
//					if (result[0] == -1 || totalDist < result[1]) {
////						System.out.println("setting the distance to: " + totalDist + " [" + stepsTo[0][ancestor] + " + "
////								+ stepsTo[1][ancestor] + "] from ancestor " + ancestor);
//						result[0] = ancestor;
//						result[1] = totalDist;
//						result[2] = Math.abs(wDistance - vDistance);
//						limit = totalDist;
//					}
//				}
//			}
			steps++;
		}

		// empty the queues before returning
		while (!vQ.isEmpty()) {
			vQ.dequeue();
		}
		while (!wQ.isEmpty()) {
			wQ.dequeue();
		}
		while (!swap.isEmpty()) {
			swap.dequeue();
		}

		return result;
	}

	private void resetFor(int v, int w) {

		vmap.clear();
		wmap.clear();

		vmap.put(v, 0);
		wmap.put(w, 0);

//		marked = new boolean[2][dg.V()];
//		marked[0][v] = true;
//		marked[1][w] = true;
//
//		stepsTo = new int[2][dg.V()];
//		stepsTo[0][v] = 0;
//		stepsTo[1][w] = 0;

		vQ.enqueue(v);
		wQ.enqueue(w);

	}

	/**
	 * length of shortest ancestral path between any vertex in v and any vertex in
	 * w. -1 if no such path
	 */
	public int length(Iterable<Integer> v, Iterable<Integer> w) {

		iterableCheck(v, w);

		return getIterableValues(v, w)[1];
	}

	/**
	 * the common ancestor that participates in shortest ancestral path between any
	 * vertex in set v and any vertex in set w. returns -1 if there is no such path.
	 */
	public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {

		iterableCheck(v, w);

		return getIterableValues(v, w)[0];

	}

	private int[] getIterableValues(Iterable<Integer> v, Iterable<Integer> w) {

		MinPQ<Result> results = new MinPQ<Result>();
		limit = dg.V();

		for (int a : v) {
			for (int b : w) {

				resetFor(a, b);

				int[] result = getValues(a, b);

				if (result[0] != -1) {
					results.insert(new Result(result));
				}
			}
		}

		if (results.isEmpty())
			return nullResult;
		return results.delMin().getResult();
	}

	private class Result implements Comparable<Result> {
		int[] array;

		public Result(int[] results) {
			this.array = results;
		}

		int[] getResult() {
			return array;
		}

		@Override
		public String toString() {
			return "[" + array[0] + ", length " + array[1] + "]";
		}

		@Override
		public int compareTo(Result that) {
			if (this.array[1] < that.array[1])
				return -1;
			if (this.array[1] > that.array[1])
				return 1;
			else if (this.array[1] == that.array[1]) {
				if (this.array[2] < that.array[2])
					return -1;
				if (this.array[2] > that.array[2])
					return 1;
			}
			return 0;
		}
	}

	private void validIntCheck(String method, Integer... values) {
		for (Object val : values) {
			// check if the value is an integer
			int i = -1;
			try {
				i = (int) val;
			} catch (NullPointerException e) {
				throw new IllegalArgumentException("value " + val + " is not an integer");
			}
//			System.out.println("" + i + " + " + dg.V());
			// check the value is a valid reference for a vertex
			if (i < 0 || i >= dg.V())
				throw new IllegalArgumentException("the integer " + val + " is an invalid vertex index");
		}
	}

	private void iterableCheck(Iterable<Integer> v, Iterable<Integer> w) {
		if (v == null || w == null)
			throw new IllegalArgumentException();

		for (Integer i : v) {
			if (i == null || i < 0 || i >= dg.V())
				throw new IllegalArgumentException();
		}

		for (Integer i : w) {
			if (i == null || i < 0 || i >= dg.V())
				throw new IllegalArgumentException();
		}
	}

	// do unit testing of this class
	/**
	 * Corner cases. Throw an IllegalArgumentException in the following situations:
	 * 
	 * Any argument is null
	 * <p>
	 * Any vertex argument is outside its prescribed range
	 * <p>
	 * Any iterable argument contains a null item
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

//		edu.princeton.cs.algs4.In in = new edu.princeton.cs.algs4.In("digraph-wordnet.txt");
//		Digraph G = new Digraph(in);
//		Digraph rev = new Digraph(G).reverse();
//
//		SAP sap = new SAP(G);
//		Bag<Integer> bag = new Bag<Integer>();
//		bag.add(38003);
//		bag.add(22618);
////		bag.add();
//		BreadthFirstDirectedPaths bfs = new BreadthFirstDirectedPaths(rev, bag);
//		System.out.println(bfs.distTo(76352));

//		System.out.println(sap.length(76352, 22618));
//		System.out.println(sap.length(76352, 38003));
//		System.out.println(sap.length(38003, 22618));

	}
}