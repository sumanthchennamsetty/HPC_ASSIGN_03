import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class ConBST_Test {
    private static int THREADS;
    private static int TIME;
    BinarySearchTree tree;
    Counter instance;
    long[] opCount;
    long totalOps;
    Thread[] th;
    long start;
    String workload;
    private int prefillSize;
    int containsPercentage;
    int insertPercentage;
    int deletePercentage;

    public ConBST_Test(int num_threads, int prefillSize, String workload) {
        tree = new BinarySearchTree();
        THREADS = num_threads;
        instance = new Counter();
        this.prefillSize = prefillSize;
        this.workload = workload;
        th = new Thread[num_threads];
        opCount = new long[num_threads];
        totalOps = 0;

        Random random = new Random();

        for (int i = 0; i < prefillSize; i++) {
            int value = random.nextInt(100);
            tree.insert(value);
        }

        String[] percentages = workload.split("-");
        containsPercentage = Integer.parseInt(percentages[0].replace("C", ""));
        insertPercentage = Integer.parseInt(percentages[1].replace("I", ""));
        deletePercentage = Integer.parseInt(percentages[2].replace("D", ""));
    }

    public void testParallel() throws Exception {
        for (int i = 0; i < THREADS; i++) {
            th[i] = new AllMethods();
        }
        start = System.currentTimeMillis();
        for (int i = 0; i < THREADS; i++) {
            th[i].start();
        }
        for (int i = 0; i < THREADS; i++) {
            th[i].join();
        }
    }

    class AllMethods extends Thread {
        public void run() {
            int j = ThreadID.get();
            long count = 0;
            Random random = new Random();

            int containsOperations = (int) (prefillSize * (containsPercentage / 100.0));
            int insertOperations = (int) (prefillSize * (insertPercentage / 100.0));
            int deleteOperations = prefillSize - containsOperations - insertOperations;

            while (count < prefillSize) {
                instance.inc();
                int value = random.nextInt(100);

                double randomPercentage = random.nextDouble() * 100;

                if (containsOperations > 0 && randomPercentage < containsPercentage) {
                    boolean exists = tree.contains(value);
                    containsOperations--;
                } else if (insertOperations > 0 && randomPercentage < containsPercentage + insertPercentage) {
                    tree.insert(value);
                    insertOperations--;
                } else if (deleteOperations > 0) {
                    tree.delete(value);
                    deleteOperations--;
                }

                count++;
            }
            opCount[j] = count;
        }
    }

    public long totalOperations() {
        for (int i = 0; i < THREADS; i++) {
            totalOps = totalOps + opCount[i];
        }
        return totalOps;
    }

    public static void main(String[] args) {
        int num_threads = Integer.parseInt(args[0]);
        int prefillSize = Integer.parseInt(args[1]);
        String workload = args[2];

        long startTime = System.currentTimeMillis();
        ConBST_Test ob = new ConBST_Test(num_threads, prefillSize, workload);
        try {
            ob.testParallel();
        } catch (Exception e) {
            System.out.println(e);
        }
        long endTime = System.currentTimeMillis();
        long elapsedMillis = endTime - startTime;
        long totalOperations = ob.totalOperations();
        double throughput = (totalOperations / (1000000.0 * elapsedMillis)) * 1000.0;

        System.out.print("ConBST_Test:num_threads:" + num_threads + ":totalOps:" + totalOperations + " :throughput:" + throughput + "\n");
    }
}

class BinarySearchTree {
    Node root;

    static class Node {
        int data;
        Node left, right;

        Node(int data) {
            this.data = data;
            left = right = null;
        }
    }

    public void insert(int value) {
        root = insertRec(root, value);
    }

    private Node insertRec(Node root, int value) {
        if (root == null) {
            root = new Node(value);
            return root;
        }
        if (value < root.data) {
            root.left = insertRec(root.left, value);
        } else if (value > root.data) {
            root.right = insertRec(root.right, value);
        }
        return root;
    }

    public boolean contains(int value) {
        return containsRec(root, value);
    }

    private boolean containsRec(Node root, int value) {
        if (root == null) {
            return false;
        }
        if (value == root.data) {
            return true;
        } else if (value < root.data) {
            return containsRec(root.left, value);
        } else {
            return containsRec(root.right, value);
        }
    }

    public void delete(int value) {
        root = deleteRec(root, value);
    }

    private Node deleteRec(Node root, int value) {
        if (root == null) {
            return root;
        }
        if (value < root.data) {
            root.left = deleteRec(root.left, value);
        } else if (value > root.data) {
            root.right = deleteRec(root.right, value);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }
            root.data = minValue(root.right);
            root.right = deleteRec(root.right, root.data);
        }
        return root;
    }

    private int minValue(Node root) {
        int minValue = root.data;
        while (root.left != null) {
            minValue = root.left.data;
            root = root.left;
        }
        return minValue;
    }
}

class Counter {
    private long value = 0;

    public void inc() {
        value++;
    }

    public long getValue() {
        return value;
    }
}

class ThreadID {
    private static volatile int nextID = 0;
    private static ThreadLocal<Integer> threadID = ThreadLocal.withInitial(() -> nextID++);

    public static int get() {
        return threadID.get();
    }
}
