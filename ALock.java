import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ALock implements Lock {
    private final AtomicBoolean[] flags;
    private final int numThreads;
    private int slot = 0; // Each thread has a unique slot in the array.

    public ALock(int numThreads) {
        this.numThreads = numThreads;
        flags = new AtomicBoolean[numThreads];
        for (int i = 0; i < numThreads; i++) {
            flags[i] = new AtomicBoolean(false);
        }
    }

    public void lock() {
        int mySlot = slot;
        slot = (slot + 1) % numThreads; // Move to the next slot.
        flags[mySlot].set(true);
        
        while (flags[mySlot].get()) {
            // Spin while waiting for your turn.
        }
    }

    public void unlock() {
        int mySlot = (slot - 1 + numThreads) % numThreads; // Previous slot.
        flags[mySlot].set(false);
    }

 
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
