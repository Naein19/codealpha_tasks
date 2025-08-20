import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a single stock in the market.
 */
public class Stock {
    public String ticker;
    public String name;
    public double price;
    private transient List<Double> history = new ArrayList<>(); // Use 'transient' to exclude from JSON serialization
    private transient Random random = new Random();

    // Default constructor for Gson
    public Stock() {
    }

    public Stock(String ticker, String name, double price) {
        this.ticker = ticker;
        this.name = name;
        this.price = price;
        this.history.add(price);
    }

    public void updatePrice() {
        if (this.random == null)
            this.random = new Random();
        if (this.history == null)
            this.history = new ArrayList<>();

        double changePercent = (random.nextDouble() * 2 - 1) * 0.05; // Fluctuate between -5% and +5%
        this.price *= (1 + changePercent);
        this.price = Math.round(this.price * 100.0) / 100.0; // Round to 2 decimal places

        if (this.history.isEmpty())
            this.history.add(this.price);
        this.history.add(this.price);
        if (this.history.size() > 10) {
            this.history.remove(0);
        }
    }

    public double getPriceChange() {
        if (history == null || history.size() < 2) {
            return 0.0;
        }
        return this.price - this.history.get(this.history.size() - 2);
    }
}