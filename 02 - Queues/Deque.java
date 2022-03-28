
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * a double-ended queue, "Deque", is a generalization of a stack and a queue
 * that supports adding and removing items from either the front or the back of
 * the data structure. Consider it like a normal FIFO queue, with a 'priority'
 * function that allows for new nodes to be placed at the front of the queue
 * instead of the back as would be the default.
 *
 */
public class Deque<Item> implements Iterable<Item> {

    private Node first, last;
    private int nodes;

    /**
     * Corner cases. Throw the specified exception for the following corner
     * cases:
     *
     * Throw an IllegalArgumentException if the client calls either addFirst()
     * or addLast() with a null argument.
     *
     * Throw a java.util.NoSuchElementException if the client calls either
     * removeFirst() or removeLast when the deque is empty.
     *
     * Throw a java.util.NoSuchElementException if the client calls the next()
     * method in the iterator when there are no more items to return.
     *
     * Throw an UnsupportedOperationException if the client calls the remove()
     * method in the iterator.
     *
     * Unit testing. Your main() method must call directly every public
     * constructor and method to help verify that they work as prescribed (e.g.,
     * by printing results to standard output).
     *
     * Performance requirements. Your deque implementation must support each
     * deque operation (including construction) in constant worst-case time. A
     * deque containing n items must use at most 48n + 192 bytes of memory.
     * Additionally, your iterator implementation must support each operation
     * (including construction) in constant worst-case time.
     *
     * to do: fully remove functionality for nodes to have a 'previous'
     * reference (found to be redundant feature, only useful if required to
     * iterate backwards.
     *
     */
    public Deque() {
        first = null; // in case iterator is called on empty list
        nodes = 0; // ititialise running total nodes in list
    }

    /**
     * Each node is a single element in the linked list; holding one Item, and a
     * link to the next node in the queue.
     */
    private class Node {

        Item item;
        Node next;
        Node previous;

        private Node(Item item, Node previous, Node next) {
            this.item = item;
            this.next = next;
            this.previous = previous;
        }

    }

//         is the deque empty?
    public boolean isEmpty() {
        return size() == 0;
    }

//     return the number of items on the deque
    public int size() { // requirement for worst case constant time implies keeping a running count?
        return nodes;
    }

//     add the item to the front
    public void addFirst(Item item) {

        if (item == null) {
            throw new IllegalArgumentException("item given is null");
        }

        if (nodes == 0) {
            first = new Node(item, null, null);

        } else {
            if (nodes == 1) { // assume single existing node holds 'first' position and title
                last = first; // second place is also last place
                first = new Node(item, null, last);
                last.previous = first;

            } else { // any other case is 'normal'
                Node oldFirst = first;
                first = new Node(item, null, oldFirst);
                oldFirst.previous = first;
            }
        }
        nodes++;

    }

    // add the item to the back
    public void addLast(Item item) { // an item joins the queue at the back and is given a null reference for the next item

        if (item == null) {
            throw new IllegalArgumentException("item given is null");
        }

        if (nodes == 0) {
            first = new Node(item, null, null); // when creating a node into empty queue, it will become 'first' for sake of iterator

        } else { // there exists at least one node. 

            if (nodes == 1) { // assume single existing node holds 'first' position and title
                last = new Node(item, first, null);
                first.next = last;

            } else { // any other case means there exists both 'first' and 'last'
                Node oldLast = last;
                last = new Node(item, oldLast, null);
                oldLast.next = last;
            }
        }
        nodes++;
    }

//     remove and return the item from the front
    public Item removeFirst() {

        if (nodes == 0) {
            throw new NoSuchElementException("nothing in list to remove");
        }

        Item item = first.item;

        if (nodes == 1) {
            first = null;

        } else {
            first = first.next;
            first.previous = null;
        }

        nodes--;
        return item;
    }

//     remove and return the item from the back
    public Item removeLast() {

        if (nodes == 0) {
            throw new NoSuchElementException("nothing in list to remove");
        }

        if (nodes == 1) { // last item is internally named 'first'
            Item item = first.item;
            first = null;
            nodes--;
            return item;
        }

        Item item = last.item;

        if (nodes == 2) {
            last = null;
            first.next = null;

        } else {
            last = last.previous;
            last.next = null;
        }

        nodes--;
        return item;
    }

//     return an iterator over items in order from front to back
    @Override
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // nested iterator provides functionality for client iteration through list
    private class DequeIterator implements Iterator<Item> {

        private Node current = first;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException("overrun end of list");
            }
            Item item = current.item;
            current = current.next;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("removal of items during iteration is not implemented");
        }

    }

//     unit testing (required) 
    public static void main(String[] args) {
//         call here all public deque methods for testing purposes
        Deque<String> d = new Deque<>();
        System.out.println("is empty? " + d.isEmpty());
        System.out.println("size: " + d.size());
        d.addFirst("head 1");
        d.addLast("tail 1");
        d.addFirst("head 2");
        d.addLast("tail 2");
        System.out.println("is empty? " + d.isEmpty());
        System.out.println("size: " + d.size());
        System.out.println("remove first: " + d.removeFirst());
        System.out.println("remove last: " + d.removeLast());
    }

}
