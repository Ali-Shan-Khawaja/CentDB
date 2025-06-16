package Transaction;


public class TransactionInstance {
    private static TransactionInstance transactionInstance = null;

    private TransactionInstance() {

    }

    public static TransactionInstance getInstance() {
        return transactionInstance;
    }

    public static void setInstance() {
        if (transactionInstance == null) {
            transactionInstance = new TransactionInstance();
        }
    }

    public static void clearInstance() {
        transactionInstance = null;
    }
}
