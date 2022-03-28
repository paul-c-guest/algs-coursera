import edu.princeton.cs.algs4.StdOut;

/**
 * Constructs a <b>circular suffix array</b> for the supplied text. Methods
 * allow for return of the length of the text; and retrieval of the sorted
 * position of a suffix.
 *
 */
public class CircularSuffixArray {

	private String in;
	private int len;
	private CircularSuffix[] suffices;
	private int[] sortedSuffixArray;

	// circular suffix array of s
	public CircularSuffixArray(String s) {
		if (s == null)
			throw new IllegalArgumentException();

		this.in = s;
		this.len = in.length();

		if (len > 0)
			sortedSuffixArray = setArraysAndSort();
		else
			sortedSuffixArray = new int[] { 0 };

	}

	private int[] setArraysAndSort() {
		suffices = new CircularSuffix[len];

		for (int i = 0; i < len; i++)
			suffices[i] = new CircularSuffix(i);

		BasicMSD msd = new BasicMSD();
		msd.sort(suffices);

		int[] aux = new int[len];
		for (int i = 0; i < len; i++) {
			aux[i] = suffices[i].index;
		}

		return aux;
	}

	private char getSuffixCharAt(int suffix, int index) {
		return in.charAt((suffix + index) % len);
	}

	// length of input string
	public int length() {
		return len;
	}

	// returns index of i-th sorted suffix
	public int index(int i) {
		if (i < 0 || i >= len)
			throw new IllegalArgumentException("the requested index is out of bounds");
		else if (len == 0)
			return 0;
		else
			return sortedSuffixArray[i];
	}

	// unit testing (required)
	public static void main(String[] args) {
//		Stopwatch timer = new Stopwatch();ing, int , int) {
//		
//	}
		CircularSuffixArray csa = new CircularSuffixArray("ABRACADABRA!");
//		CircularSuffixArray csa = new CircularSuffixArray("BBABBBBBABBBB");
//		CircularSuffixArray csa = new CircularSuffixArray("couscous");
//		In reader = new In(args[0]);
//		CircularSuffixArray csa = new CircularSuffixArray(reader.readAll());
		StdOut.println("length of input: " + csa.length());
		StdOut.println("index of first sorted suffix: " + csa.index(0));

//		System.out.println("final sorted order of circular suffixes: ");
//		for (int i = 0; i < csa.length(); i++)
//			System.out.println(i + " : " + csa.index(i));

//		StdOut.println("time: " + timer.elapsedTime());
	}

	private class CircularSuffix {

		int index;

		public CircularSuffix(int index) {
			this.index = index;
		}

		char getCharAt(int depth) {
			return getSuffixCharAt(index, depth);
		}

	}

	/**
	 * stripped out version of MSD from edu.princeton.cs.algs4, reworked to operate
	 * only on local CircularSuffix objects
	 * 
	 * @author ballsies
	 *
	 */
	private class BasicMSD {

		final int R = 256; // extended ASCII alphabet size

		/**
		 * Rearranges the array of extended ASCII CircularSuffix strings in ascending
		 * order.
		 *
		 * @param sufficies the array to be sorted
		 */
		void sort(CircularSuffix[] sufficies) {
			CircularSuffix[] aux = new CircularSuffix[len];
			sort(sufficies, 0, len - 1, 0, aux);
		}

		// sort from a[lo] to a[hi], starting at the dth character
		void sort(CircularSuffix[] a, int lo, int hi, int d, CircularSuffix[] aux) {

			if (hi <= lo || d == len - 1) return;

			// compute frequency counts
			int[] count = new int[R + 2];
			for (int i = lo; i <= hi; i++) {
				int c = a[i].getCharAt(d);
				count[c + 2]++;
			}

			// transform counts to indicies
			for (int r = 0; r < R + 1; r++)
				count[r + 1] += count[r];

			// distribute
			for (int i = lo; i <= hi; i++) {
				int c = a[i].getCharAt(d);
				aux[count[c + 1]++] = a[i];
			}

			// copy back
			for (int i = lo; i <= hi; i++) {
				if (a[i].index != aux[i - lo].index)
				a[i] = aux[i - lo];
			}

			// recursively sort for each character (excludes sentinel -1)
				for (int r = 0; r < R; r++)
					sort(a, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);

		}

	}
}
