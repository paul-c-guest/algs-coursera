import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

	/**
	 * apply Burrows-Wheeler transform, reading from standard input and writing to
	 * standard output
	 */
	public static void transform() {
		// get input (command line provides source)

		String input = BinaryStdIn.readString();
		int len = input.length();

		CircularSuffixArray csa = new CircularSuffixArray(input);
		StringBuilder sb = new StringBuilder();

		for (int index = 0; index < len; index++) {
			int s = csa.index(index);
			sb.append(input.charAt((s + (len - 1)) % len));
			if (s == 0)
				BinaryStdOut.write(index);
		}

		// binarystdout (command line can capture output)
		BinaryStdOut.write(sb.toString());

		BinaryStdOut.flush();
	}

	private static class IntQueue {

		Node first, last;
		int size = 0;
		
		private class Node {
			Node next;
			final int index;

			public Node(int index) {
				this.index = index;
			}
		}

		boolean isEmpty() {
			return size == 0;
		}

		void enqueue(int index) {
			if (size == 0)
				first = last = new Node(index);
			else {
				last.next = new Node(index);
				last = last.next;
			}
			size++;
		}

		int dequeue() {
			int i = first.index;
			first = first.next;
			size--;
			return i;
		}
	}

	/**
	 * apply Burrows-Wheeler inverse transform, reading from standard input and
	 * writing to standard output
	 */
	public static void inverseTransform() {

		// binarystdin (command line args provide input)
		// first 32 bits are an integer, necessary for inversion of the input
		int key = BinaryStdIn.readInt();

		// get the rest of the stream as a char array
		char[] input = BinaryStdIn.readString().toCharArray();

		// make an array of the internal class IntegerQueues, length 256 (ascii radix)
		IntQueue[] qs = new IntQueue[256];

		for (int i = 0; i < input.length; i++) {
			char ch = input[i];
			if (qs[ch] == null)
				qs[ch] = new IntQueue();
			qs[ch].enqueue(i);
		}


		// construct the array of [next keys] from the array of queues. 
		int[] next = new int[input.length];

		for (int i = 0, index = 0; i < 256; i++) {
			if (qs[i] != null)
			while (!qs[i].isEmpty()) {
				next[index] = qs[i].dequeue();
				index++;
			}
		}

		// get the next key based on the the current key
		for (int i = 0; i < next.length; i++) {
			key = next[key];
			// binarystdout (command line will capture output as required)
			BinaryStdOut.write(input[key]);
		}
		BinaryStdOut.flush();
	}

	// if args[0] is "-", apply Burrows-Wheeler transform
	// if args[0] is "+", apply Burrows-Wheeler inverse transform
	public static void main(String[] args) {
		if (args[0].equals("-"))
			transform();
		if (args[0].equals("+"))
			inverseTransform();
	}

}
