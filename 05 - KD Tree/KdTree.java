import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {

	private Node root;
	private int count;
	// direction integers increase clockwise:

	public KdTree() {
		// construct an empty set of points
		root = null;
		count = 0;
	}

	public boolean isEmpty() {
		// is the set empty?
		return root == null;
	}

	public int size() {
		// number of points in the set
		return count;
	}

	public void insert(Point2D newPoint) {
		if (newPoint == null) {
			throw new IllegalArgumentException();
		}
		/**
		 * insert method calls a recursive method, starting from the root. depth
		 * argument assists determination of each node's 'odd' or 'even' status, for use
		 * in Node's compareTo() method
		 */
		if (pointIsValid(newPoint)) {
			root = insert(newPoint, root, 0);
		}
	}

	private boolean pointIsValid(Point2D test) {
		return !(test.x() < 0.0 || test.x() > 1.0 || test.y() < 0.0 || test.y() > 1.0);
	}

	// recursive insertion method to solve all cases for adding new nodes to kdtree
	private Node insert(Point2D newPoint, Node currentNode, int depth) {
		// when null space found, place new node
		if (currentNode == null) {
			currentNode = new Node(newPoint, depth);
			count++;
//			System.out.println("\t" + point.toString());
			return currentNode;
		}
		// else compare points to determine tree path
		int compared = currentNode.compareTo(newPoint);
		// left and right link references are set recursively
		if (compared == -1) {
//			System.out.print("L ");
			currentNode.left = insert(newPoint, currentNode.left, depth + 1);
		}
		if (compared == 1) {
//			System.out.print("R ");
			currentNode.right = insert(newPoint, currentNode.right, depth + 1);
		}
//		if (compared == 0) { // duplicate point coordinates are still regarded as 'inserted'
//			count++;
//		}
		return currentNode;
	}

	// does the set contain the point?
	public boolean contains(Point2D point) {
		if (point == null) {
			throw new IllegalArgumentException();
		}
		if (root == null)
			return false;
		Node current = root;
		while (current != null) {
			if (current.compareTo(point) == 0) {
				return true;
			}
			int c = current.compareTo(point);
			if (c == -1) {
				current = current.left;
			} else if (c == 1) {
				current = current.right;
			}
		}
		// if node is null return false;
		return false;
	}

	// draw all points to standard draw
	public void draw() {
		// set up main drawing area
//        StdDraw.enableDoubleBuffering();
		// recursively explore each branch
		if (root == null)
			return;
		RectHV area = new RectHV(0.0, 0.0, 1.0, 1.0);
		draw(root, area);
	}

	// draw dividing line and point for each valid node
	private void draw(Node current, RectHV area) {
		// draw a line to divide the current node's area
		StdDraw.setPenRadius(0.001);
		if (current.depth % 2 == 0) {
			// vertical red line
			StdDraw.setPenColor(StdDraw.RED);
			StdDraw.line(current.point.x(), area.ymin(), current.point.x(), area.ymax());
		} else {
			// horizontal blue line
			StdDraw.setPenColor(StdDraw.BLUE);
			StdDraw.line(area.xmin(), current.point.y(), area.xmax(), current.point.y());
		}
		// black dot
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.setPenRadius(0.008);
		StdDraw.point(current.point.x(), current.point.y());
		if (current.left != null)
			draw(current.left, getSplitLeft(area, current));
		if (current.right != null)
			draw(current.right, getSplitRight(area, current));
	}

	// all points that are inside a given rectangle, including its edge
	public Iterable<Point2D> range(RectHV searchArea) {
		if (searchArea == null) {
			throw new IllegalArgumentException();
		}
		if (root == null)
			return null;
		Queue<Point2D> results = new Queue<>();
		RectHV area = new RectHV(0.0, 0.0, 1.0, 1.0);
		range(searchArea, area, root, results);
		return results;
	}

	// recursive method to collect results of range search
	private void range(RectHV searchArea, RectHV domain, Node current, Queue<Point2D> results) {
		// put hits to collection
		if (searchArea.contains(current.point)) {
			results.enqueue(current.point);
		}
		// split and update search area boxes according to current node and test both
		// cases independently, to ensure proper collection of all relevant points
		RectHV leftSplit = getSplitLeft(domain, current);
		if (current.left != null && searchArea.intersects(leftSplit)) {
			range(searchArea, leftSplit, current.left, results);
		}
		RectHV rightSplit = getSplitRight(domain, current);
		if (current.right != null && searchArea.intersects(rightSplit)) {
			range(searchArea, rightSplit, current.right, results);
		}
	}

	// a nearest neighbour in the set to point p; null if the set is empty
	public Point2D nearest(Point2D query) {
		if (query == null) {
			throw new IllegalArgumentException();
		}
		if (isEmpty()) {
			return null;
		}
		// create rect to enclose all possible points and pass to recursive method
		RectHV domain = new RectHV(0.0, 0.0, 1.0, 1.0);
		// recursive method to return best result
		return nearest(query, root.point, root, domain);
	}

	private Point2D nearest(Point2D query, Point2D best, Node current, RectHV domain) {
		// check for better result
		if (query.distanceSquaredTo(current.point) <= query.distanceSquaredTo(best)) {
			best = current.point;
		}
		// split and update search area boxes according to current node and
		RectHV leftSplit = getSplitLeft(domain, current);
		RectHV rightSplit = getSplitRight(domain, current);

		// test left and right cases without dependence on other result.
		// always search towards query results first, but do not ignore potential of
		// looking in other split
		int searchPriority = current.compareTo(query); 
		if (searchPriority == 0) {
			best = current.point;
			return best;
		}
		if (searchPriority == -1) { 
			// query point is in the 'left'/lower split, look there first
			if (current.left != null) {
				best = nearest(query, best, current.left, leftSplit);
			}
			if (current.right != null && rightSplit.distanceTo(query) < best.distanceTo(query)) {
				best = nearest(query, best, current.right, rightSplit);
			}
		}
		if (searchPriority == 1) {
			// query point is in the 'right'/upper split, look there first
			if (current.right != null) {
				best = nearest(query, best, current.right, rightSplit);
			}
			if (current.left != null && leftSplit.distanceTo(query) < best.distanceTo(query)) {
				best = nearest(query, best, current.left, leftSplit);
			}
		}

		return best;
	}

	// return the split of a domain which corresponds to the 'lower' portion
	private RectHV getSplitLeft(RectHV input, Node node) {
		if (node.depth % 2 == 0) { // will be a vertical split
			return new RectHV(input.xmin(), input.ymin(), node.point.x(), input.ymax());
		} else { // is a horizontal split
			return new RectHV(input.xmin(), input.ymin(), input.xmax(), node.point.y());
		}
	}

	// return the split of a domain which corresponds to the 'upper' portion
	private RectHV getSplitRight(RectHV input, Node node) {
		if (node.depth % 2 == 0) {
			return new RectHV(node.point.x(), input.ymin(), input.xmax(), input.ymax());
		} else {
			return new RectHV(input.xmin(), node.point.y(), input.xmax(), input.ymax());
		}
	}

	// internal nested Node class holds a POint2D object, references to its
	// children, and a depth record (distance from root)
	private class Node implements Comparable<Point2D> {

		public Point2D point;
		public Node left, right;
		public int depth;

		public Node(Point2D point, int depth) {
			this.point = point;
			this.depth = depth;
			left = null;
			right = null;
		}

		@Override
		public int compareTo(Point2D other) {
			if (point.equals(other)) {
				return 0;
			}
			if (depth % 2 == 0) {
				if (other.x() < point.x())
					return -1;
				if (other.x() > point.x())
					return 1;
				if (other.y() < point.y())
					return -1;
				if (other.y() > point.y())
					return 1;
			} else if (depth % 2 == 1) {
				if (other.y() < point.y())
					return -1;
				if (other.y() > point.y())
					return 1;
				if (other.x() < point.x())
					return -1;
				if (other.x() > point.x())
					return 1;
			}
			return 0;
		}
	}

// unit testing of the methods (optional)
	public static void main(String[] args) {

		KdTree k = new KdTree();

		k.insert(new Point2D(0.7, 0.3));
		k.insert(new Point2D(0.5, 0.6));
		k.insert(new Point2D(0.2, 0.2));
		k.insert(new Point2D(0.1, 0.5));
		k.insert(new Point2D(0.3, 0.3));
		k.insert(new Point2D(0.4, 0.4));
		k.insert(new Point2D(0.4, 0.45));
		k.insert(new Point2D(0.6, 0.9));
		k.insert(new Point2D(0.6, 1.9));
		k.insert(new Point2D(0.9, 0.5));

		System.out.println(k.contains(new Point2D(0.8, 0.5)));
		System.out.println(k.size());
		for (Point2D p : k.range(new RectHV(0.2, 0.3, 0.6, 0.7))) {
			System.out.println(p);
		}
		System.out.println(k.nearest(new Point2D(0.1, 0.7)));
		k.draw();
	}

}
