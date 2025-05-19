package TransactionsSimulator;
import java.util.*;
import java.util.concurrent.*;

public class BankSimulator {

    private final Map<String, BankAccount> accounts;
    private final Map<String, BlockingQueue<Transaction>> bankQueues;
    private final List<String> accountIds;
    private final TransactionGenerator generator;
    private final BlockingQueue<Integer> balanceQueue;

    public BankSimulator() {
        // 1. Initialize accounts
        accounts = new HashMap<>();
        accounts.put("Account_1", new BankAccount("Account_1", "OTP"));
        accounts.put("Account_2", new BankAccount("Account_2", "KH"));
        accounts.put("Account_3", new BankAccount("Account_3", "OTP"));

        // 2. Initialize bank queues (per bank)
        bankQueues = new HashMap<>();
        bankQueues.put("OTP", new LinkedBlockingQueue<>(10));
        bankQueues.put("KH", new LinkedBlockingQueue<>(10));
        
        /*
         * WATCH OUT HERE!
         *  
         * 'bankQueues.put("bank_name", new LinkedBlockingQueue<>(VALUE));'
         * 
         * If not specifying a value that's can easily lead to memory problems if you put too many items in it and don't take out it fast enough
         */

        // 3. Initialize transaction generator with account IDs
        accountIds = new ArrayList<>(accounts.keySet());
        generator = new TransactionGenerator(accountIds);

        // 4. Queue for account balances (used externally)
        balanceQueue = new LinkedBlockingQueue<>();
    }

    // Getter for the balance queue
    public BlockingQueue<Integer> getBalanceQueue() {
        return balanceQueue;
    }

    // Starts the simulation
    public BlockingQueue<Integer> runSimulation() throws InterruptedException {
        // 1. Start bank transaction processors in separate threads
        BankTransactionProcessor otpProcessor = new BankTransactionProcessor(bankQueues.get("OTP"), accounts, "OTP");
        BankTransactionProcessor khProcessor = new BankTransactionProcessor(bankQueues.get("KH"), accounts, "KH");

        Thread otpThread = new Thread(otpProcessor, "OTP-Thread");
        Thread khThread = new Thread(khProcessor, "KH-Thread");
        otpThread.start();
        khThread.start();
  
        while (true) {
        	
        	/*
             * Time transaction example. Wait a certain amount of time for transactions to arrive.
             */
        	
        	
            // 2. Deposit base amount to all accounts at the start of each cycle
            accounts.get("Account_1").deposit(1000);
            accounts.get("Account_2").deposit(1000);
            accounts.get("Account_3").deposit(501);
            
            System.out.println("\n--- New simulation cycle started ---\n");

            // 3. Generate 5 transactions with a 1-second delay between each
            for (int i = 0; i < 5; i++) {
                generator.generateRandomTransaction();
                Thread.sleep(1000);
            }

            // 4. Retrieve and print generated transactions
            List<Transaction> transactions = generator.retrieveAndClearBuffer();
            System.out.println("Generated transactions:");
            for (Transaction tx : transactions) {
                System.out.println(tx);
            }

            // 5. Put transactions into the appropriate bank queues
            for (Transaction tx : transactions) {
                try {
                    String bankName = accounts.get(tx.getAccountId()).getBankName();
                    bankQueues.get(bankName).put(tx);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }

            // 6. Print waiting transactions for each bank
            printWaitingTransactions("OTP", bankQueues.get("OTP"));
            printWaitingTransactions("KH", bankQueues.get("KH"));

            // 7. Pause to allow transaction processing
            Thread.sleep(2000);

            // 8. Print current balances and store them in the balance queue
            System.out.println("Current balances:");
            for (BankAccount account : accounts.values()) {
                System.out.println(account.getAccountId() + " balance: " + account.getBalance());
                balanceQueue.put((int) account.getBalance());

                // Reset the balance for the next cycle
                
                // Or here we can apply the 'Percentage' example (Mentioned in ThirdPartyMonitor.java) or other calculate. 
                account.resetBalance();
            }
        }
    }

    // Helper function to print pending transactions in a thread-safe way
    public static synchronized void printWaitingTransactions(String bankName, BlockingQueue<Transaction> queue) {
        String purple = "\033[0;35m";  // Purple color
        String reset = "\033[0m";      // Reset color
        System.out.println("\nPending transactions at bank " + bankName + ":");

        synchronized (queue) {
            for (Transaction tx : queue) {
                System.out.println(purple + tx + reset);  // Print transaction in purple
            }
        }
    }
}
