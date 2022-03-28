import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * command line class for encoding and decoding given data. command line
 * requires a symbol, either a positive or negative sign, i.e. + or -
 * <p>
 * the sign corresponds respectively to 'decode' and 'encode'
 * 
 *
 */
public class MoveToFront {
	
	private static final short RDX = 256;

	private static short[] getFreshRadixArray() {
		short[] radix = new short[RDX];
		for (short i = 0; i < RDX; i++)
			radix[i] = i;
		return radix;
	}

	/**
	 * apply move-to-front encoding for the supplied input
	 */
	public static void encode() {
		short[] rdx = getFreshRadixArray();

		while (!BinaryStdIn.isEmpty()) {

			// get next character
			char ch = BinaryStdIn.readChar();

			// find current array index of the character
			char index = 0;
			for (char i = 0; i < RDX; i++)
				if (rdx[i] == ch) {
					index = i;
					break;
				}

			// bring ch to the front of the array
			short swap = rdx[index];
			System.arraycopy(rdx, 0, rdx, 1, index);
			rdx[0] = swap;

			// write out the index
			BinaryStdOut.write(index);
		}
		BinaryStdOut.flush();
	}

	/**
	 * apply move-to-front decoding, reading from standard input and writing to
	 * standard output
	 */
	public static void decode() {
		short[] rdx = getFreshRadixArray();

		while (!BinaryStdIn.isEmpty()) {

			// get character from current state of rdx
			char index = BinaryStdIn.readChar();
			short ch = rdx[index];
			BinaryStdOut.write((char) ch);

			// bring ? to the front of the radix
			System.arraycopy(rdx, 0, rdx, 1, index);
			rdx[0] = ch;
		}
		BinaryStdOut.flush();
	}

	// if args[0] is "-", apply move-to-front encoding
	// if args[0] is "+", apply move-to-front decoding
	public static void main(String[] args) {
		if (args[0].equals("-"))
			encode();

		if (args[0].equals("+"))
			decode();
	}

}
