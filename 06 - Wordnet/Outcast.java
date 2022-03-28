import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

	private WordNet wn;

	// constructor takes a WordNet object
	public Outcast(WordNet wordnet) {
		wn = wordnet;
	}

	/**
	 * given an array of WordNet nouns, return an outcast.
	 * <p>
	 * (To identify an outcast, compute the sum of the distances between each noun
	 * and every other one)
	 */
	public String outcast(String[] nouns) {
		// for ease of implementation assume args during testing will be valid and
		// number at least two

		int[] results = new int[nouns.length];
		int thisTotal;

		for (int index = 0; index < nouns.length; index++) {

			thisTotal = 0;
			for (String target : nouns) {

//				if (there.equals(here)) continue;

				int singleDistance = wn.distance(nouns[index], target);

				thisTotal += singleDistance;

			}
//			System.out.println(nouns[index] + ": " + thisTotal);
			results[index] = thisTotal;
		}

		// get max value from distances to return correct String from nouns
		int outcast = 0;
//		System.out.println("initially setting outcast to: " + nouns[outcast]);
		for (int i = 1; i < results.length; i++) {
			if (results[i] > results[outcast]) {
				outcast = i;
//				System.out.println("changing outcast to: " + nouns[outcast]);
			}

		}
//		System.out.println("result: " + nouns[max]);
		return nouns[outcast];
	}

	public static void main(String[] args) {
		WordNet wordnet = new WordNet(args[0], args[1]);
		Outcast outcast = new Outcast(wordnet);
		for (int t = 2; t < args.length; t++) {
			In in = new In(args[t]);
			String[] nouns = in.readAllStrings();
			StdOut.println(args[t] + ": " + outcast.outcast(nouns));
		}
	}
}