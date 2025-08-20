/**
 * Represents a single buy or sell transaction.
 */
public class Transaction {
    public String timestamp;
    public String type; // "BUY" or "SELL"
    public String ticker;
    public int quantity;
    public double price;
    public double total;

    public Transaction(String timestamp, String type, String ticker, int quantity, double price) {
        this.timestamp = timestamp;
        this.type = type;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
        this.total = Math.round(quantity * price * 100.0) / 100.0;
    }
}