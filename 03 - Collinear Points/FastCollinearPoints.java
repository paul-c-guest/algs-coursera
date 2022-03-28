
import java.util.Arrays;
import java.util.Comparator;

public class FastCollinearPoints {

    private final Point[] points;
    private final LineSegment[] segments;
    private LineStack<LineSeg> lineStack;
    private final int length;

    // finds all line segments containing at least 4 points
    public FastCollinearPoints(Point[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null object given");
        } else {
            length = input.length;
            points = new Point[length];
            getPoints(input);
        }

        lineStack = new LineStack<>();

        for (Point p : points) {
            // clone the main points array and sort it against point p
            Point[] sortedPoints = points.clone();
            Arrays.sort(sortedPoints, p.slopeOrder());

            // make an array of the slope between p and array[i]
            double[] sortedSlopes = new double[length];
            for (int i = 1; i < length; i++) { // start at i = 1: unnecessary to test first point against itself
                sortedSlopes[i] = sortedPoints[0].slopeTo(sortedPoints[i]);
            }
            // test for collinear points and put hits to a collection of LineSegment objects
            getCollinearPoints(sortedPoints, sortedSlopes);
        }

        // transfer collection of line segments to final array for export
        segments = new LineSegment[lineStack.size];
        lineStack.populateSegments();
    }

    private void getPoints(Point[] input) {
        // check for null object in array while transferring input to local collection
        for (int point = 0; point < length; point++) {
            if (input[point] == null) {
                throw new IllegalArgumentException("point at array position " + point + " is null");
            } else {
                points[point] = input[point];
            }
        }

        Arrays.sort(points); // to bring duplicates together

        // test each point against previous for duplicates
        for (int i = 1; i < length; i++) {
            if (points[i].compareTo(points[i - 1]) == 0) {
                throw new IllegalArgumentException("point " + input[i] + " is repeated");
            }
        }
    }

    private void getCollinearPoints(Point[] array, double[] slopes) {
        int low = 1;
        while (low < array.length - 2) { // -3 to avoid overrunning array bounds
            double slopeToLow = slopes[low]; // array[0].slopeTo(array[low]);
            // if true, found a collinear set 4 points long
            if (slopeToLow == slopes[low + 2]) { // true if 4 points are collinear
                // test for more points on found line, set high accordingly
                int high = getHighestCollinearIndex(slopes, low + 2, slopeToLow);
                // put relevant collinear points into a helper array
                Point[] set = Arrays.copyOfRange(array, low - 1, high + 1); // include an extra slot for origin, i.e. "low - 1"
                // replace first entry with origin
                set[0] = array[0];
                // sort it naturally so first and last point on line will be at either end of array
                Arrays.sort(set);
                // to eliminate duplicates: only put the maximal line pair on the stack if the origin remains at set[0] after sorting
                if (set[0] == array[0]) {
                    lineStack.push(new LineSeg(set[0], set[set.length - 1]));
                }
                // low can jump ahead to next unchecked index
                low = high + 1;
            } else {
                low++;
            }
        }
    }

    // step through indexes until slopes do not match, or slope matches at final array entry
    private int getHighestCollinearIndex(double[] slopes, int index, double slopeToLow) {
//        int index = low + 2;
        while (++index < length && slopeToLow == slopes[index]) {
        }
        return index - 1;
    }

    // the number of line segments
    public int numberOfSegments() {
        int segCount = segments.length;
        return segCount;
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] clone = segments.clone();
        return clone;
    }

    private class LineStack<Object> {

        public int size;
        public LineSeg first, oldFirst;

        public LineStack() {
            size = 0;
            first = null;
            oldFirst = null;
        }

        public void push(LineSeg segment) {
            oldFirst = first;
            first = segment;
            first.next = oldFirst;
            size++;
        }

        public void populateSegments() {
            if (first != null) {
                LineSeg current = first;
                for (int s = 0; s < size; s++) {
                    segments[s] = current.lineSeg;
                    current = current.next;
                }
            }
        }

    }

    private class LineSeg {

        public final LineSegment lineSeg;
        public LineSeg next;

        public LineSeg(Point a, Point b) {
            lineSeg = new LineSegment(a, b);
        }
    }

}
