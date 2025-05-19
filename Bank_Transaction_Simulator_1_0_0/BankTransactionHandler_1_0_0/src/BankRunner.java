import java.util.concurrent.BlockingQueue;
import TransactionsSimulator.BankSimulator;

/**
 * This class is responsible for starting and managing the bank simulation.
 * It provides access to the shared queue used by the simulation.
 */
public class BankRunner {
    // Instance of the BankSimulator, which simulates transactions
    private final BankSimulator simulator;

    // Constructor: initializes the simulator
    public BankRunner() {
        this.simulator = new BankSimulator();
    }

    /**
     * Returns the shared queue that contains account balances or transaction values.
     * This queue is used by other threads (e.g., GUI or processing threads) to read the data.
     */
    public BlockingQueue<Integer> getSharedQueue() {
        return simulator.getBalanceQueue();
    }

    /**
     * Starts the simulation in a separate thread.
     * This method creates a new thread to run the bank simulation so that it doesn't block the main GUI thread.
     */
    public void start() {
        new Thread(() -> {
            try {
                // Start simulating bank transactions (adds values to the queue)
                simulator.runSimulation();
            } catch (InterruptedException e) {
                e.printStackTrace(); // Print error if the thread is interrupted
            }
        }, "BankSimulatorThread").start(); // Give the thread a descriptive name
    }
}
