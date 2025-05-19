package ThreadHandler;

import java.util.concurrent.BlockingQueue;

/**
 * ProducerTask is a Runnable that continuously retrieves integers (representing accounts)
 * from a shared queue, optionally processes them if running, and updates the UI and a shared counter.
 */
public class ProducerTask implements Runnable {

    // Shared input queue from BankSimulator containing account balances
    private final BlockingQueue<Integer> acc_queue;

    // The name of the thread (e.g., "Thread-1")
    private final String name;

    // Flag to control whether this producer should process values
    private volatile boolean running = false;

    // Shared counter among threads, used to aggregate values
    private final SharedCounter sharedCounter;

    // Reference to the GUI, used to update labels and event output
    private final MainFrame ui;

    /**
     * Constructor for the producer task.
     *
     * @param acc_queue      shared blocking queue of account values
     * @param name           name of this producer thread
     * @param counter        shared counter to be updated
     * @param ui             reference to the GUI for updates
     */
    public ProducerTask(BlockingQueue<Integer> acc_queue, String name, SharedCounter counter, MainFrame ui) {
        this.acc_queue = acc_queue;
        this.name = name;
        this.sharedCounter = counter;
        this.ui = ui;
    }

    /**
     * Start the producer task — enables processing of queued values.
     */
    public void start() {
        running = true;
    }

    /**
     * Stop the producer task — disables processing of queued values.
     */
    public void stop() {
        running = false;
    }

    /**
     * Check if the producer is currently running.
     *
     * @return true if running, false otherwise
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Get the name of this producer thread.
     *
     * @return the name of the thread
     */
    public String getName() {
        return name;
    }

   
 // Updates the GUI with the current contents of this thread's queue
    private void updateAccQueueDisplay() {
        StringBuilder sb = new StringBuilder(name + " acc_queue: ");
        for (Integer val : acc_queue) {
            sb.append(val).append(" ");
        }
        // Update the display in the GUI
        ui.setAccQueueDisplay(name, sb.toString());
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Continuously update the queue display
                updateAccQueueDisplay();

                if (running) {
                    // Take an element from the shared queue (blocks if empty)
                    Integer account = acc_queue.take();
                    if (account != null) {
                        // Show the taken value in the GUI under this thread's label
                        MainFrame.setThreadQueueDisplay(name, String.valueOf(account));
                        
                        // Add the value to the shared counter
                        sharedCounter.add(account);
                        
                        // Append the event to the log area
                        ui.appendOutput(name + " added: " + account + " -> new total: " + sharedCounter.get());
                    } 
                }
                // Sleep to reduce CPU usage
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Restore interrupted status and exit loop if necessary
                Thread.currentThread().interrupt();
            }
        }
    }
}