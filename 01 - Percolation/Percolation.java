
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/**
 * simulates a table measuring n width by n height, for use in percolation
 * tests.
 *
 *
 * utilises weighted quick union algorithm provided by princeton. creates a
 * blank table which can be incrementally populated for purposes of statistical
 * analysis. backwash problems persist in previous version. this version
 * implememts two union tables, one with a virtual top and the other a virtual
 * bottom only. performing unions on both tables and checking for isFull flow at
 * either virtual site should eliminate any chance of backwash occurring. timing
 * may suffer.
 *
 */
public class Percolation {

    private final WeightedQuickUnionUF unionTableTop;
    private final WeightedQuickUnionUF unionTableBottom;
    private final int n;
    private final int virtualTop;
    private final int virtualBottom;
    private boolean[] sites;
    private boolean percolates;
    private int openSites;

    public Percolation(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("int 'n' at initialisation cannot be less than one");
        }

        this.n = n;

        /**
         * create two UF tables which will BOTH receive each REGULAR union call.
         * each table will also receive an additional union call to its virtual
         * top or bottom site if the opened site is in the bottom or top row,
         * respectively.
         */
        unionTableTop = new WeightedQuickUnionUF(n * n + 1);
        unionTableBottom = new WeightedQuickUnionUF(n * n + 1);

        // set virtual site indices to the highest array index in the union tables
        virtualTop = n * n;
        virtualBottom = n * n;

        // a simple array of boolean values to track open/blocked states of sites
        // assume blocked == false, open == true.
        sites = new boolean[n * n];

        // set all sites blocked initially
        for (int s = 0; s < sites.length; s++) {
            sites[s] = false;
        }
        openSites = 0; // counter for stats object

        percolates = false; // made true when system passes percolation test
    }

    // opens the site (row, col) if it is not open already
    // and makes union only with surrounding* OPEN sites (directly above, below, left, right).
    public void open(int row, int col) {

        if (!coordsAreValid(row, col)) {
            throw new IllegalArgumentException("coordinates out of range opening site at " + row + ", " + col);
        }

        if (!isOpen(row, col)) {

            // get an index to assist opening the correct site in the array and increment number of open sites
            int site = getArrayIndex(row, col);
            sites[site] = true;
            openSites++;

            // cleaner checking algorithm begins here
            // general checks and unions for all sites including top and bottom rows,
            // but ignoring special cases of  rows with virtual sites to connect
            if (row > 1 && isOpen(site - 1)) { // has an open site above
                unionTableTop.union(site, site - 1);
                unionTableBottom.union(site, site - 1);
            }

            if (row < n && isOpen(site + 1)) { // has an open site below
                unionTableTop.union(site, site + 1);
                unionTableBottom.union(site, site + 1);
            }

            if (col > 1 && isOpen(site - n)) { // has open site to left
                unionTableTop.union(site, site - n);
                unionTableBottom.union(site, site - n);
            }

            if (col < n && isOpen(site + n)) { // has open site to right
                unionTableTop.union(site, site + n);
                unionTableBottom.union(site, site + n);
            }

            if (row == 1) { // top row 
                unionTableTop.union(site, virtualTop);
            }

            if (row == n) { // bottom row
                unionTableBottom.union(site, virtualBottom);
            }

            // test for percolation
            percolationTest(site);
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        if (!coordsAreValid(row, col)) {
            throw new IllegalArgumentException("invalid coordinates checking site at " + row + ", " + col);
        }
        return sites[getArrayIndex(row, col)];
    }

    private boolean isOpen(int index) {
        return sites[index];
    }

    // is the site (row, col) full? 
    // i.e. can it connect to an open site on the 'top' or 'bottom' row 
    public boolean isFull(int row, int col) {

        if (!coordsAreValid(row, col)) {
            throw new IllegalArgumentException("coordinates out of range checking full site");
        }

        return unionTableTop.connected(getArrayIndex(row, col), virtualTop);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return percolates;
    }

    // eureka! if newly opened site can connect in both directions we deduce that system percolates
    private void percolationTest(int site) {
        if (unionTableTop.connected(site, virtualTop)
            && unionTableBottom.connected(site, virtualBottom)) {
            percolates = true;
        }
    }

    private boolean coordsAreValid(int row, int col) {
        return row > 0 && row <= n && col > 0 && col <= n;
    }

    private int getArrayIndex(int row, int col) {
        return (row - 1) + (n * (col - 1));
    }
}
