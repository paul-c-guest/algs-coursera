package DynamicConnectivity;

public class QuickFind extends Algorithm {

    public QuickFind(int size) {
        super(size);
    }
    
    // Quick Find solution for dynamic connectivity problem
    // has a fast method for checking connections, 
    // at the expense of a potentially slow union method
    // which iterates through the entire array/network
    
    // (is there a quadratic time problem?)
    
    // this algorithm performs poorly against QUnion
    
    @Override
    protected void union(int firstNode, int secondNode) {

        if (!connected(firstNode, secondNode)) {

            int a = array[firstNode];
            int b = array[secondNode];

            for (int i : array) {
                if (array[i] == a) {
                    array[i] = b;
                }
            }
        }
    }

    @Override
    protected boolean connected(int first, int second) {
        return array[first] == array[second];
    }

}
