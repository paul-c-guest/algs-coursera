package DynamicConnectivity;

public abstract class Algorithm {

    protected int[] array;

    public Algorithm(int size) {
        
        array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }
        
    }

    protected void union(int firstNode, int secondNode) {
    }

    protected boolean connected(int firstNode, int secondNode) {
        return firstNode == secondNode;
    }

    protected int[] get() {
        return array;
    }

}
