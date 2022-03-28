package DynamicConnectivity;

public class QuickUnion extends Algorithm {

    // Quick Union solution for dynamic connectivity problem 
    // has an OK method for making unions, but needs to use
    // a potentially slow connection-checking method if there
    // are many unions to trace back through?
    
    // performs far better than QFind in most circumstance so far...
    
    
    
    public QuickUnion(int size) {
        super(size);
    }

    @Override
    public void union(int first, int second) {
        if (!connected(first, second)) {
            array[first] = array[second];
        }
    }

    @Override
    protected boolean connected(int first, int second) {
        return root(first) == root(second);
    }
    

    protected int root(int query) {
        int rootTrace = array[query];

        while (rootTrace != array[rootTrace]) {
            rootTrace = array[rootTrace];
        }
        return rootTrace;
    }

}
