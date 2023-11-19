import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class MCSLock implements Lock {
    private static class Node {
        boolean locked = true;
        Node next = null;
    }

    private final ThreadLocal<Node> myNode;
    private final AtomicReference<Node> tail;

    public MCSLock() {
        myNode = ThreadLocal.withInitial(Node::new);
        tail = new AtomicReference<>(null);
    }

    public void lock() {
        Node node = myNode.get();
        Node predecessor = tail.getAndSet(node);

        if (predecessor != null) {
            node.locked = true;
            predecessor.next = node;
            while (node.locked) {
                // Spin until predecessor signals it's done.
            }
        }
    }

    public void unlock() {
        Node node = myNode.get();

        if (node.next == null) {
            // No other threads are waiting.
            if (tail.compareAndSet(node, null)) {
                return;
            }

            // Wait for the next node to be assigned.
            while (node.next == null) {
                // Spin.
            }
        }

        node.next.locked = false;
        node.next = null;
    }

    // Methods required by the Lock interface

    
    public Condition newCondition() {
        throw new UnsupportedOperationException("Not implemented");
    }

   
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public boolean tryLock() {
        throw new UnsupportedOperationException("Not implemented");
    }

   
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException("Not implemented");
    }
}
