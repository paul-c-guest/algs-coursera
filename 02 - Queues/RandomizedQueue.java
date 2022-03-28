
import edu.princeton.cs.algs4.StdRandom;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A randomized queue is similar to a stack or queue, except that the item
 * removed is chosen uniformly at random among items in the data structure.
 */
public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] rq;
    private int N;

    /**
     * Iterator. Each iterator must return the items in uniformly random order.
     * The order of two or more iterators to the same randomized queue must be
     * mutually independent; each iterator must maintain its own random order.
     *
     * Corner cases. Throw the specified exception for the following corner
     * cases:
     *
     * Throw an IllegalArgumentException if the client calls enqueue() with a
     * null argument.
     *
     * Throw a java.util.NoSuchElementException if the client calls either
     * sample() or dequeue() when the randomized queue is empty.
     *
     * Throw a java.util.NoSuchElementException if the client calls the next()
     * method in the iterator when there are no more items to return.
     *
     * Throw an UnsupportedOperationException if the client calls the remove()
     * method in the iterator.
     *
     * Unit testing. Your main() method must call directly every public
     * constructor and method to verify that they work as prescribed (e.g., by
     * printing results to standard output).
     *
     * Performance requirements. Your randomized queue implementation must
     * support each randomized queue operation (besides creating an iterator) in
     * constant amortized time. That is, any intermixed sequence of m randomized
     * queue operations (starting from an empty queue) must take at most cm
     * steps in the worst case, for some constant c. A randomized queue
     * containing n items must use at most 48n + 192 bytes of memory.
     * Additionally, your iterator implementation must support operations next()
     * and hasNext() in constant worst-case time; and construction in linear
     * time; you may (and will need to) use a linear amount of extra memory per
     * iterator.
     *
     * to do: tests count the number of null elements in arrays. check where
     * tests do this and amend here to include null elements in relevant
     * iteration / for loop.
     *
     * tests count too many calls to stdrandom -- amend so calls will be linear
     * to n
     *
     */
    // construct an empty randomized queue
    public RandomizedQueue() {
        rq = (Item[]) new Object[1];
        N = 0; // array position marker for next open null slot. 
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return N == 0;
    }

    // return the number of items on the randomized queue, excluding null entries
    public int size() {
        return N;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("attempting to add a null item");
        }

        // resize larger first if necessary
        if (N == rq.length) {
            resize(rq.length * 2);
        }

        // place item then move marker forward
        rq[N++] = item;
    }

//     remove and return a random item
    public Item dequeue() {

        // get a random item 
        int r = getValidRandomIndex();
        Item item = rq[r];

        // take last item from array to position r
        rq[r] = rq[--N];
        rq[N] = null;

        // resize by half if list becomes quarter full
        if (N + 1 == rq.length / 4) {
            resize(rq.length / 2);
        }
        return item;
    }

//     return a random item (but do not remove it)
    public Item sample() {
        return rq[getValidRandomIndex()];
    }

    // test random indexes on the list until an non-null entry is found
    private int getValidRandomIndex() {
        if (isEmpty()) {
            throw new NoSuchElementException("collection is empty");
        } else {
            return StdRandom.uniform(N);
        }
    }

    // create a new array of appropriate size for current items, copy items to new array
    private void resize(int newSize) {
        Item[] newList = (Item[]) new Object[newSize];

        // put items to new array
        for (int i = 0; i < N; i++) {
            newList[i] = rq[i];
        }

        rq = newList;
    }

    // return an independent iterator over items in random order
    @Override
    public Iterator<Item> iterator() {
        return new RandomIterator(N);
    }

    /**
     * nested class for construction of iterator object
     */
    private class RandomIterator implements Iterator<Item> {

        Item[] localRQ;
        int position;
        int length;

        /**
         * constructor creates new array only long enough to hold all non-null
         * entries from list. list is cloned, completely dequeued to the new
         * array, then reference to the clone is handed back to main list.
         *
         */
        public RandomIterator(int length) {
            position = 0;
            this.length = length;
            Item[] rqCopy = rq.clone();

            localRQ = (Item[]) new Object[length];
            for (int i = 0; i < length; i++) {
                localRQ[i] = dequeue();
            }

            rq = rqCopy;
            N = length;

        }

        @Override
        public boolean hasNext() {
            return position < length;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException("outside of collection range");
            }
            return localRQ[position++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("removing elements during iteration not implemented");
        }

    }
//
//     unit testing (required), call here all public methods 

    public static void main(String[] args) {
        RandomizedQueue<String> q = new RandomizedQueue<>();

        q.enqueue("dog");
        q.enqueue("cat");
        q.enqueue("mouse");
        q.enqueue("horse");
        q.enqueue("rat");

        System.out.println("nodes: " + q.size());
        System.out.println(q.sample());
        System.out.println(q.dequeue());
        System.out.println("list is empty? " + q.isEmpty());

        Iterator<String> it = q.iterator();

        for (int i = 0; it.hasNext(); i++) {
            System.out.println(it.next());
        }

        System.out.println("nodes: " + q.size());
        System.out.println(q.sample());
        System.out.println(q.dequeue());
        System.out.println(q.dequeue());
        System.out.println(q.dequeue());
        System.out.println(q.dequeue());

        System.out.println("list is empty? " + q.isEmpty());

    }
}
