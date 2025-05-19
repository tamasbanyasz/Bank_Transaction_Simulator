package TransactionsSimulator;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * BankAccount class represents a bank account with basic operations.
 * It uses a ReentrantLock to ensure thread-safe access to balance updates.
 */
public class BankAccount {

    // Logger for logging account activities
    private static final Logger logger = Logger.getLogger(BankAccount.class.getName());

    // Unique identifier for the account
    private String accountId;

    // Name of the bank associated with this account
    private String bankName;

    // Current balance of the account
    private int balance;

    // Lock to ensure thread-safe operations on balance
    private final Lock lock = new ReentrantLock(); 

    /**
     * Constructor to create a new bank account.
     * @param accountId Unique account ID
     * @param bankName Name of the bank
     */
    public BankAccount(String accountId, String bankName) {
        this.accountId = accountId;
        this.bankName = bankName;
        this.balance = 0;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getBankName() {
        return bankName;
    }

    /**
     * Gets the current account balance in a thread-safe way.
     * @return Current balance
     */
    public int getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Deposits the given amount into the account.
     * Logs the transaction and uses locking to ensure safe access.
     * @param amount Amount to deposit
     */
    public void deposit(int amount) {
        lock.lock();
        try {
            balance += amount;
            logger.info("Deposited: " + amount + " to account " + getAccountId());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Withdraws money from the account based on a transaction.
     * If insufficient balance, the transaction is marked as FAILED.
     * @param transaction The transaction containing the amount to withdraw
     */
    public void withdraw(Transaction transaction) {
        lock.lock();
        try {
            logger.info("Starting withdrawal of " + transaction.getAmount() + " from " + accountId);
            if (balance >= transaction.getAmount()) {
                balance -= transaction.getAmount();
                logger.info("Withdrawal successful: " + transaction.getAmount() + " from account " + getAccountId());
                transaction.setStatus(TransactionStatus.COMPLETED);
            } else {
                logger.warning("Insufficient funds: " + getAccountId() + " tried to withdraw " + transaction.getAmount() +
                               " (Transaction: " + transaction.toString() + ")");
                transaction.setStatus(TransactionStatus.FAILED);
            }
            logger.info("New balance for " + getAccountId() + ": " + balance);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Transfers money from this account to another account.
     * Logs and checks for sufficient funds.
     * @param amount Amount to transfer
     * @param targetAccount The recipient account
     * @param transaction Transaction object for status tracking
     */
    public void transfer(int amount, BankAccount targetAccount, Transaction transaction) {
        lock.lock();
        try {
            if (balance >= amount) {
                balance -= amount;
                targetAccount.deposit(amount);
                logger.info("Transfer successful: " + amount + " from " + getAccountId() + " to " + targetAccount.getAccountId());
            } else {
                logger.warning("Insufficient funds for transfer: " + getAccountId() + " tried to send " + amount +
                               " (Transaction: " + transaction.toString() + ")");
                transaction.setStatus(TransactionStatus.FAILED);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Resets the balance to 0. Used typically to reset the simulation.
     */
    public void resetBalance() {
        lock.lock();
        try {
            balance = 0;
        } finally {
            lock.unlock();
        }
    }
}
