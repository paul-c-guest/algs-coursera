
import java.util.Arrays;

public class BruteCollinearPoints {

    private final Point[] points;
    private final LineSegment[] segments;
    private final int length;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] input) {
        if (input == null) {
            throw new IllegalArgumentException("null object given");
        } else {
            length = input.length;
            points = new Point[length];
            getPoints(input);
        }

        // create temporary holding place for any valid collinear point sets
        LineStack<LineSeg> lineStack = new LineStack<>();

        // check every combination of 4 Points, for possible line segments
        for (int i = 0; i < length - 3; i++) {
            for (int k = i + 1; k < length - 2; k++) {
                for (int m = k + 1; m < length - 1; m++) {
                    for (int n = m + 1; n < length; n++) {
                        // pass each unique cluster of [i,k,m,n] to checking method
                        if (testFourPoints(i, k, m, n)) {
                            lineStack.push(new LineSeg(points[i], points[n]));
                        }
                    }
                }
            }
        }
        // get info to create the array of LineSegments 
        segments = new LineSegment[lineStack.size];
        lineStack.populateSegments();
    }

    // evaluates equivalance of the 3 slopes between four points
    private boolean testFourPoints(int i, int k, int m, int n) {
        double baseline = points[i].slopeTo(points[k]);
        return baseline == points[i].slopeTo(points[m])
                && baseline == points[i].slopeTo(points[n]);
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

    /**
     * nested linked list to temporarily store LineSeg objects. core functions
     * exist only for current use within constructor, proper functionality as a
     * linked stack should not be expected.
     *
     * @param <Object>
     */
    private class LineStack<Object> {

        private int size;
        private LineSeg first, oldFirst;

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
                // for each LineSeg, use first and last (fourth) point coordinates for creation of LineSegment
                for (int i = 0; i < size; i++) {
                    segments[i] = current.lSeg;
                    current = current.next;
                }
            }
        }
    }

    /**
     * a nested class utilised as helper/temporary collection of four points on
     * a line, to later be used to populate array of LineSegment objects
     */
    private class LineSeg {

        public LineSegment lSeg;
        public LineSeg next;

        public LineSeg(Point a, Point b) {
            lSeg = new LineSegment(a, b);
        }

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

}
