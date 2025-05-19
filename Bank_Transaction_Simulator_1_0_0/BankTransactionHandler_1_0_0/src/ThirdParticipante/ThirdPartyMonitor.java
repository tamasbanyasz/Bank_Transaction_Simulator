package ThirdParticipante;
import java.util.logging.Logger;

import TransactionsSimulator.Transaction;
import TransactionsSimulator.TransactionStatus;

public class ThirdPartyMonitor {
    private static final Logger logger = Logger.getLogger(ThirdPartyMonitor.class.getName());

    // Monitor and audit a transaction if it's accepted or failed
    public synchronized void monitorTransaction(Transaction transaction) {
        logger.info("Harmadik fél figyeli a tranzakciót: " + transaction);
        
        // For example how can we regulate transactions 
        if (transaction.getAmount() <= 0) {
            logger.warning("Hiba: Érvénytelen tranzakció - " + transaction);
            transaction.setStatus(TransactionStatus.FAILED);
        } else {
            logger.info("Tranzakció elfogadva: " + transaction);
            transaction.setStatus(TransactionStatus.COMPLETED);
        }
        
        /*
        Another example:
        	-	Calculate a percentage and only subtract it.
       */

    }
    
    public void update(Transaction tx) {
        // This method handles the processing update of a transaction.
        System.out.println("Transaction processed: " + tx);
    }
}
