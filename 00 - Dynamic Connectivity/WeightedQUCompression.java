package DynamicConnectivity;

public class WeightedQUCompression extends WeightedQU {

    public WeightedQUCompression(int size) {
        super(size);
    }

    @Override
    protected int root(int query) {
        int rootTrace = array[query];

        while (rootTrace != array[rootTrace]) {
            rootTrace = array[rootTrace];
            array[rootTrace] = array[array[rootTrace]];
        }
        return rootTrace;
    }

}
