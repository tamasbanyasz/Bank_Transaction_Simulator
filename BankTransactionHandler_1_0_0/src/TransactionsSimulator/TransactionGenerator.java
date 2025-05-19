package TransactionsSimulator;

import java.util.*;

/*
 * The TransactionGenerator class is responsible for randomly generating financial transactions (deposit, withdrawal, or transfer) for a predefined 
 * list of account IDs. It stores the generated transactions in a buffer that can later be retrieved and cleared.
 */

public class TransactionGenerator {

    private final Random random = new Random();
    private final List<String> accountIds;          // List of account IDs to use in transactions
    private final List<Transaction> buffer = new ArrayList<>();  // Buffer to temporarily hold generated transactions

    /**
     * Constructor that accepts a list of account IDs.
     */
    public TransactionGenerator(List<String> accountIds) {
        this.accountIds = accountIds;
    }

    /**
     * Generates a random transaction and stores it in the buffer.
     * - Amount: Between 50 and 500
     * - Type: Randomly selected from TransactionType
     * - For transfers: selects a different target account
     */
    public synchronized void generateRandomTransaction() {
        String from = accountIds.get(random.nextInt(accountIds.size()));
        TransactionType type = TransactionType.values()[random.nextInt(TransactionType.values().length)];
        int amount = 50 + random.nextInt(450); // Random amount between 50 and 500

        Transaction tx;

        // If it's a transfer, choose a different target account
        if (type == TransactionType.TRANSFER) {
            String to;
            do {
                to = accountIds.get(random.nextInt(accountIds.size()));
            } while (to.equals(from)); // Ensure sender and receiver are not the same
            tx = new Transaction(from, amount, type, to);
        } else {
            tx = new Transaction(from, amount, type);
        }

        buffer.add(tx);
        System.out.println("Generated transaction: " + tx);
    }

    /**
     * Retrieves and clears all buffered transactions.
     * Returns a list of transactions ready for processing.
     */
    public synchronized List<Transaction> retrieveAndClearBuffer() {
        List<Transaction> toProcess = new ArrayList<>(buffer);
        buffer.clear(); // Clear the buffer after retrieving
        return toProcess;
    }
}
