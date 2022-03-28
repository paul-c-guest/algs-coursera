package DynamicConnectivity;

import java.util.HashMap;

public class WeightedQU extends QuickUnion {

    private int[] nodeSize;

    public WeightedQU(int size) {
        super(size);
        
        nodeSize = new int[size];
        for (int i = 0; i < size; i++) {
            nodeSize[i] = 1;
        }
    }

    @Override
    public void union(int first, int second) {

        int leftRoot = root(first);
        int rightRoot = root(second);

        if (leftRoot != rightRoot) {

            if (nodeSize[leftRoot] <= nodeSize[rightRoot]) {
                nodeSize[rightRoot] += nodeSize[leftRoot];
                array[leftRoot] = array[rightRoot];

            } else {
                nodeSize[leftRoot] += nodeSize[rightRoot];
                array[rightRoot] = array[leftRoot];
            }
        }
    }

    @Override
    protected boolean connected(int first, int second) {
        return root(first) == root(second);
    }

    @Override
    protected int root(int query) {
        int rootTrace = array[query];

        while (rootTrace != array[rootTrace]) {
            rootTrace = array[rootTrace];
        }
        return rootTrace;
    }

}
