
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdRandom;

public class PercolationStats {

    private static int n;
    private static int T;
    private double mean;
    private double stddev;
    private double confidenceLo;
    private double confidenceHi;

    // perform a number of trials on an n-by-n system
    public PercolationStats(int n, int trials) {

        // check validity of given values
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("input values cannot be zero or below");
        }

        // set the array where individual results will be stored, 
        // number of places determined by number of trials
        double[] results = new double[trials];

        // run the tests
        for (int i = 0; i < trials; i++) {

            // create a new percolation for each test
            Percolation currentTrial = new Percolation(n);

            // open random sites until system percolates
            while (!currentTrial.percolates()) {
                currentTrial.open(StdRandom.uniform(1, n + 1), StdRandom.uniform(1, n + 1));
            }

            // record number of open sites / total sites to results
            results[i] = currentTrial.numberOfOpenSites() / (double) (n * n);
        }

        mean = StdStats.mean(results);
        stddev = StdStats.stddev(results);
        confidenceLo = mean - 1.96 * stddev / Math.sqrt(trials);
        confidenceHi = mean + 1.96 * stddev / Math.sqrt(trials);

    }

    public static void main(String[] args) {

        // check for two arguments at command line
        if (args.length != 2) {
            throw new IllegalArgumentException("expected two integers: n and T");
        }

        // attempt transfer arguments to relevant variables, else create exception
        try {
            n = Integer.parseInt(args[0]);
            T = Integer.parseInt(args[1]);
        } catch (java.lang.IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        // create PercolationStats object with given values
        PercolationStats stats = new PercolationStats(n, T);

        // calculate stats after trials
        StdOut.println("mean = " + stats.mean);
        StdOut.println("stddev = " + stats.stddev);
        StdOut.println("95% confidence interval = [" + stats.confidenceLo() + ", " + stats.confidenceHi() + "]");

    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return confidenceLo;
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return confidenceHi;
    }

}
