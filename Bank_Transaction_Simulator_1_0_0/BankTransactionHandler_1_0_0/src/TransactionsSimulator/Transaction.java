package TransactionsSimulator;

import java.time.LocalDateTime;

/*
 * The Transaction class models a banking transaction. It holds information about the transaction's account, amount, type, status, and target account 
 * (for transfer transactions). It also includes a timestamp to record when the transaction was created. This class handles the necessary logic for 
 * creating and modifying different types of transactions, such as deposits, withdrawals, and transfers.
 */

public class Transaction {

    private String accountId;            // Account ID initiating the transaction
    private String targetAccountId;      // Target account ID (only for TRANSFER type transactions)
    private int amount;                  // Amount of money being transferred or deposited
    private TransactionType type;        // Type of the transaction (DEPOSIT, WITHDRAWAL, or TRANSFER)
    private TransactionStatus status;    // Current status of the transaction (PENDING, COMPLETED, FAILED)
    private LocalDateTime timestamp;     // Timestamp when the transaction was created

    // Constructor for deposit and withdrawal transactions
    public Transaction(String accountId, int amount, TransactionType type) {
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
        this.status = TransactionStatus.PENDING; // Default status is PENDING
        this.targetAccountId = null;  // Default value is null for non-transfer transactions
        this.timestamp = LocalDateTime.now(); // Set current timestamp
    }

    // Getter and setter for timestamp
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    // Constructor for transfer transactions, includes target account ID
    public Transaction(String accountId, int amount, TransactionType type, String targetAccountId) {
        this(accountId, amount, type);
        if (type == TransactionType.TRANSFER) {
            this.targetAccountId = targetAccountId;  // Only valid for transfer transactions
        } else {
            throw new IllegalArgumentException("Target account ID is required only for transfer transactions.");
        }
    }

    // Getters and setters for transaction properties
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(String targetAccountId) {
        if (this.type == TransactionType.TRANSFER) {
            this.targetAccountId = targetAccountId;  // Only allow setting target account for transfer transactions
        } else {
            throw new IllegalArgumentException("Target account ID is not allowed for this transaction type.");
        }
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}

