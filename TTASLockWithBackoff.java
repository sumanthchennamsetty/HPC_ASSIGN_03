import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class TTASLockWithBackoff implements Lock {
    private AtomicBoolean state = new AtomicBoolean(false);
    private long minBackoff;
    private long maxBackoff;
    private long currentBackoff;

    public TTASLockWithBackoff(long minBackoff, long maxBackoff) {
        this.minBackoff = minBackoff;
        this.maxBackoff = maxBackoff;
        this.currentBackoff = minBackoff;
    }

    public void lock() {
        while (true) {
            while (state.get()) {
                // Lock is busy; apply exponential backoff.
                try {
                    Thread.sleep(currentBackoff);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                currentBackoff = Math.min(2 * currentBackoff, maxBackoff);
            }
            if (!state.getAndSet(true)) {
                return;
            }
        }
    }

    public void unlock() {
        state.set(false);
        currentBackoff = minBackoff; // Reset the backoff to the minimum after unlocking.
    }

    // Other methods (not shown) to implement the Lock interface:
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    public boolean tryLock() {
        throw new UnsupportedOperationException();
    }

    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException();
    }
}
