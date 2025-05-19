package TransactionsSimulator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

/*
 * The TransactionExporter class is responsible for exporting transaction data to JSON files. It saves both successful and failed transactions to separate 
 * files under the src/exports/ directory. The class ensures the export directory exists, loads existing transactions if the file already exists, and appends 
 * the new transaction.
 */

public class TransactionExporter {

    private static final String EXPORT_DIR = "src/exports/";

    public TransactionExporter() {
        // Create export directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(EXPORT_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Public method to export a transaction.
     * Failed transactions are stored in a separate file.
     */
    public synchronized void exportTransaction(Transaction tx, String bankName) {
        if (tx.getStatus() == TransactionStatus.FAILED) {
            exportToFile(tx, bankName + "_failed_transactions.json");
        } else {
            exportToFile(tx, bankName + "_transactions.json");
        }
    }

    /**
     * Private method that performs the actual writing to a JSON file.
     */
    private synchronized void exportToFile(Transaction tx, String fileName) {
        // Create a JSON object from the transaction
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accountId", tx.getAccountId());
        jsonObject.put("amount", tx.getAmount());
        jsonObject.put("timestamp", tx.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        jsonObject.put("transactionType", tx.getType().toString());
        jsonObject.put("targetAccountId", tx.getType() == TransactionType.TRANSFER ? tx.getTargetAccountId() : null);
        jsonObject.put("status", tx.getStatus().toString());

        Path filePath = Paths.get(EXPORT_DIR + fileName);
        JSONArray transactionList = new JSONArray();

        // Load existing transactions from file if it exists
        if (Files.exists(filePath)) {
            try {
                String content = Files.readString(filePath);
                if (content.trim().isEmpty()) {
                    content = "[]"; // Initialize empty array if file is empty
                }
                transactionList = new JSONArray(content);
            } catch (Exception e) {
                System.err.println("Failed to load existing JSON: " + e.getMessage());
            }
        }

        // Add the new transaction to the list
        transactionList.put(jsonObject);

        // Write the updated list back to the file
        try (FileWriter file = new FileWriter(filePath.toFile())) {
            file.write(transactionList.toString(2)); // Indentation level 2 for pretty printing
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
