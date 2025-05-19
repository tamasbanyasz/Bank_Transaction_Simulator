package TransactionsSimulator;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * The TransactionQueue class is a thread-safe transaction buffer using a bounded blocking queue. It allows threads to add transactions safely, 
 * enabling synchronized transaction processing.
 */

public class TransactionQueue {

    // Thread-safe bounded queue to hold transactions
    private BlockingQueue<Transaction> transactionQueue;

    /**
     * Constructor to initialize the queue with a given capacity.
     *
     * @param capacity maximum number of transactions the queue can hold
     */
    public TransactionQueue(int capacity) {
        this.transactionQueue = new LinkedBlockingQueue<>(capacity);
    }

    /**
     * Adds a transaction to the queue.
     * If the queue is full, the method will block until space becomes available.
     *
     * @param transaction the transaction to be added
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public void addTransaction(Transaction transaction) throws InterruptedException {
        transactionQueue.put(transaction); // Add transaction to the queue
    }

    /**
     * Retrieves and removes the head transaction from the queue.
     * If the queue is empty, the method will block until an element becomes available.
     *
     * @return the transaction at the head of the queue
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public Transaction takeTransaction() throws InterruptedException {
        return transactionQueue.take(); // Take transaction from the queue
    }

    /**
     * Returns the number of transactions currently waiting in the queue.
     *
     * @return the current size of the queue
     */
    public int getSize() {
        return transactionQueue.size(); // Number of transactions currently in queue
    }
}

