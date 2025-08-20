/**
 * Represents the stocks a user owns.
 */
public class StockHolding {
    public int quantity;
    public double avgPrice;

    public StockHolding(int quantity, double avgPrice) {
        this.quantity = quantity;
        this.avgPrice = avgPrice;
    }
}