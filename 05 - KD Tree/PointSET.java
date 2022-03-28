import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
//import edu.princeton.cs.algs4.StdDraw;

public class PointSET {

	private SET<Point2D> points;

	// construct an empty set of points
	public PointSET() {
		points = new SET<>();
	}

	// is the set empty?
	public boolean isEmpty() {
		return points.isEmpty();
	}

	// number of points in the set
	public int size() {
		return points.size();
	}

	// add the point to the set (if it is not already in the set)
	public void insert(Point2D p) {
		if (p == null) {
			throw new IllegalArgumentException();
		}
		points.add(p);
	}

	// does the set contain point p?
	public boolean contains(Point2D p) {
		if (p == null) {
			throw new IllegalArgumentException();
		}
		return points.contains(p);
	}

	/**
	 * go down each tree path, draw red or blue line, left or right, to the boundary
	 * from a coordinate derived from current node's x or y, and each child's y or
	 * x.
	 */
	public void draw() {

	}

	// all points that are inside the rectangle (or on the boundary)
	public Iterable<Point2D> range(RectHV rect) {
		if (rect == null) {
			throw new IllegalArgumentException();
		}
		if (isEmpty())
			return null;
		Queue<Point2D> range = new Queue<>();
		for (Point2D point : points) {
			if (rect.contains(point)) {
				range.enqueue(point);
			}
		}
		return range;
	}

	// a nearest neighbor in the set to point p; null if the set is empty
	public Point2D nearest(Point2D p) {
		if (p == null) {
			throw new IllegalArgumentException();
		}
		if (isEmpty())
			return null;
		Point2D closest = points.min();
		for (Point2D point : points) {
			if (p.distanceTo(point) < p.distanceTo(closest)) {
				closest = point;
			}
		}
		return closest;
	}

	// unit testing of the methods (optional)
	public static void main(String[] args) {
	}

}
