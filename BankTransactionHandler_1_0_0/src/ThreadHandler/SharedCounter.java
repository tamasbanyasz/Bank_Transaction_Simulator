package ThreadHandler;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * SharedCounter is a thread-safe counter using AtomicInteger.
 * It allows multiple threads to safely add values and retrieve the current total.
 */
public class SharedCounter {

    // AtomicInteger ensures atomic operations on the counter (safe for concurrent access)
    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Adds the specified value to the counter in a thread-safe manner.
     *
     * @param value the value to add
     */
    public void add(int value) {
        counter.addAndGet(value); // atomically adds the value
    }

    /**
     * Returns the current value of the counter.
     *
     * @return the current total as an int
     */
    public int get() {
        return counter.get(); // atomically retrieves the value
    }
}
