import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Topological;

public class WordNet {


	private List<String> synsetList; // need an autoresizing array
	private TreeMap<String, Bag<Integer>> nounMap;
	private Digraph dg; // princeton algs4 digraph object
	private SAP sap;
//	private Integer root; // set during constructor test for single common root

	// constructor takes the name of the two input files
	public WordNet(String synsets, String hypernyms) {
//		Stopwatch sw = new Stopwatch();
		// don't allow null arguments
		if (synsets == null || hypernyms == null)
			throw new IllegalArgumentException("null arg passed to constructor");

		synsetList = new ArrayList<String>();
		nounMap = new TreeMap<String, Bag<Integer>>();

		In in = new In(synsets);

		// populate the list and map from each line of the CSV file
		while (in.hasNextLine()) {

			String[] line = in.readLine().split(",");
			int index = Integer.parseInt(line[0]);

			// put the synset to an array
			synsetList.add(index, line[1]);

//			Bag<String> nouns = new Bag<String>();

			for (String noun : line[1].split(" ")) {

				// put the noun to the map with this index
				if (!nounMap.containsKey(noun)) {
					Bag<Integer> ints = new Bag<Integer>();
					ints.add(index);
					nounMap.put(noun, ints);
				} else {
					nounMap.get(noun).add(index);
				}

			}

		}

		// construct the digraph from file of hypernym connections
		dg = new Digraph(synsetList.size());
		in = new In(hypernyms);

		while (in.hasNextLine()) {
			String[] line = in.readLine().split(",");
			int hyponym = Integer.parseInt(line[0]);

			// add an edge from the hyponym to any subsequent integers (hypernyms)
			for (int i = 1; i < line.length; i++) {
				dg.addEdge(hyponym, Integer.parseInt(line[i]));
			}

		}

		// use algs4 Topological object to determine if constructed digpraph is a DAG
		if (!new Topological(dg).hasOrder())
			throw new IllegalArgumentException("constructed digraph is not a DAG");

		if (!graphHasOneCommonRoot())
			throw new IllegalArgumentException("constructed digraph has more than one root");

		// assume the given digraph is a rooted DAG, create the local SAP
		sap = new SAP(dg);

	}

	// returns all WordNet nouns
	public Iterable<String> nouns() {
		return nounMap.keySet();
	}

	// is the word a WordNet noun?
	public boolean isNoun(String word) {
		stringNullCheck(word);

		// claim: method takes logarithmic time due to searching on a red-black tree
		if (nounMap.containsKey(word))
			return true;
		return false;
	}

	// distance between nounA and nounB is an SAP length?
	public int distance(String nounA, String nounB) {
		stringNullCheck(nounA, nounB);

		// get the iterables
		Bag<Integer> a = getBag(nounA);
		Bag<Integer> b = getBag(nounB);

		// send arguments to the iterables method
		return sap.length(a, b);

	}

	// return the noun that is the closest common ancestor of both args
	public String sap(String nounA, String nounB) {
		stringNullCheck(nounA, nounB);
		// get the iterables

		Bag<Integer> a = getBag(nounA);
		Bag<Integer> b = getBag(nounB);

		// send arguments to the iterables method
		int sca = sap.ancestor(a, b);
		return synsetList.get(sca);
//		return synsetList.get(sca).toString();
	}

	private Bag<Integer> getBag(String noun) {
		return nounMap.get(noun);
	}

// test for more than one common root for all vertices.
	private boolean graphHasOneCommonRoot() {
		// temporary collection for any potential roots
		List<Integer> roots = new ArrayList<Integer>();

		/**
		 * for each vertex in the graph, test for edges directed outwards: a root would
		 * have zero 'outdegree' edges and at least one 'indegree'. store each hit.
		 */
		for (int i = 0; i < dg.V(); i++) {
			if (dg.outdegree(i) == 0 && dg.indegree(i) > 0)
				roots.add(i);
		}

		// return true if only one root was found
		return roots.size() == 1;

	}

	private void stringNullCheck(String... words) {
		for (String word : words) {
			if (word == null)
				throw new IllegalArgumentException();
		}
	}

	// do in-place unit testing of this class
	public static void main(String[] args) {

//		WordNet wn = new WordNet("synsets500-subgraph.txt", "hypernyms500-subgraph.txt");
//
//		System.out.println(wn.sap("ABO_antibodies", "zymase"));
//		System.out.println(wn.sap("albumen", "zymase"));
//		System.out.println(wn.sap("ABO_antibodies", "cacodyl_radical"));
	}

}
