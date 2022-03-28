
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;
import java.util.Iterator;

public class Permutation {

    /**
     * takes an integer k as a command-line argument; reads a sequence of
     * strings from standard input using StdIn.readString(); and prints exactly
     * k of them, uniformly at random. Prints each item from the sequence at
     * most once.
     *
     */
    public static void main(String[] args) {

        int k = Integer.parseInt(args[0]);

//        Deque<String> d = new Deque<>();
RandomizedQueue<String> d = new RandomizedQueue<>();
        
        // place each thing from stdin to list randomly
        while (!StdIn.isEmpty()) {
            String entry = StdIn.readString();
            d.enqueue(entry);
//            int q = StdRandom.uniform(2);
//            if (q == 0) {
//                d.addFirst(entry);
//            } else {
//                d.addLast(entry);
//            }
        }

        // print out k random elements from list
        for (int i = 0; i < k; i++) {
            System.out.println(d.dequeue());
            
//            int q = StdRandom.uniform(2);
//            if (q == 0) {
//                System.out.println(d.removeFirst());
//            } else {
//                System.out.println(d.removeLast());
//            }
//
        }
    }
}
