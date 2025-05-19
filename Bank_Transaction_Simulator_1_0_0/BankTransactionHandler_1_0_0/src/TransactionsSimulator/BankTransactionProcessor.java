package TransactionsSimulator;

import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

import ThirdParticipante.ThirdPartyMonitor;

/*
 * The BankTransactionProcessor class is responsible for processing banking transactions in a separate thread. 
 * It uses a thread pool (ExecutorService) to handle transactions concurrently. Each transaction is monitored by a third-party system before being 
 * applied to the appropriate bank account, and successful transactions are exported for logging or audit purposes.
 */

public class BankTransactionProcessor implements Runnable {

    private static final Logger logger = Logger.getLogger(BankTransactionProcessor.class.getName());

    private BlockingQueue<Transaction> transactionQueue;
    private Map<String, BankAccount> accounts;
    private String bankName;
    private volatile boolean stopRequested = false;

    private ExecutorService executor;  // Thread pool for processing transactions
    private TransactionExporter exporter;  // Handles transaction export (e.g. to file, DB)

    public BankTransactionProcessor(BlockingQueue<Transaction> transactionQueue, Map<String, BankAccount> accounts, String bankName) {
        this.transactionQueue = transactionQueue;
        this.accounts = accounts;
        this.bankName = bankName;
        this.executor = Executors.newFixedThreadPool(4);  // Create a thread pool with 4 threads
        this.exporter = new TransactionExporter();        // Initialize the exporter
    }

    @Override
    public void run() {
        try {
            while (!stopRequested) {
                // Continuously take transactions from the queue and process them
                Transaction transaction = transactionQueue.take();
                System.out.println("Transaction dequeued and will be processed: " + transaction);
                processTransaction(transaction);
                System.out.println("Transaction processed: " + transaction);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Restore interrupt status
        }
    }

    // Gracefully stop processing and shut down thread pool
    public void stop() {
        stopRequested = true;
        executor.shutdown();  // Initiate shutdown of the thread pool
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();  // Force shutdown if not finished in time
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    // Submits the transaction to be processed by one of the thread pool threads
    public void processTransaction(Transaction transaction) {
        executor.submit(() -> {
            BankAccount account = accounts.get(transaction.getAccountId());
            if (account != null) {

                // Monitor and validate the transaction using a third-party monitor
                ThirdPartyMonitor thirdPartyMonitor = new ThirdPartyMonitor();
                thirdPartyMonitor.monitorTransaction(transaction);

                // If transaction is valid and completed, apply it to the account
                if (transaction.getStatus() == TransactionStatus.COMPLETED) {
                    switch (transaction.getType()) {
                        case DEPOSIT:
                            account.deposit(transaction.getAmount());
                            break;
                        case WITHDRAWAL:
                            account.withdraw(transaction);
                            break;
                        case TRANSFER:
                            BankAccount targetAccount = accounts.get(transaction.getTargetAccountId());
                            if (targetAccount != null) {
                                account.transfer(transaction.getAmount(), targetAccount, transaction);
                            } else {
                                transaction.setStatus(TransactionStatus.FAILED); // Target account missing
                            }
                            break;
                    }

                    // Export the successful transaction for auditing
                    exporter.exportTransaction(transaction, bankName);
                } else {
                    logger.warning("Transaction failed and was not exported: " + transaction);
                }
            }
        });
    }
}
