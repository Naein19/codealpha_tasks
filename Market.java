import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Manages all the stocks available for trading and their price updates.
 */
public class Market {
    public Map<String, Stock> stocks;

    public Market() {
        this.stocks = new HashMap<>();
        initializeStocks();
    }

    private void initializeStocks() {
        stocks.put("AAPL", new Stock("AAPL", "Apple Inc.", 175.20));
        stocks.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 340.54));
        stocks.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.", 140.88));
        stocks.put("AMZN", new Stock("AMZN", "Amazon.com, Inc.", 135.30));
        stocks.put("TSLA", new Stock("TSLA", "Tesla, Inc.", 260.02));
        stocks.put("NVDA", new Stock("NVDA", "NVIDIA Corporation", 470.61));
        stocks.put("META", new Stock("META", "Meta Platforms, Inc.", 305.49));
        stocks.put("JPM", new Stock("JPM", "JPMorgan Chase & Co.", 150.12));
    }

    public void updateMarket() {
        for (Stock stock : stocks.values()) {
            stock.updatePrice();
        }
    }

    public Stock getStock(String ticker) {
        return stocks.get(ticker.toUpperCase());
    }

    public void displayMarket() {
        System.out.println("\n" + TradingApp.ANSI_PURPLE + "--- Live Stock Market ---" + TradingApp.ANSI_RESET);
        System.out.println(TradingApp.ANSI_CYAN + "----------------------------------------------------------------");
        System.out.printf("%-10s | %-25s | %12s | %12s%n", "Ticker", "Company Name", "Price", "Change");
        System.out.println("----------------------------------------------------------------" + TradingApp.ANSI_RESET);

        // Use TreeMap to display stocks sorted by ticker
        Map<String, Stock> sortedStocks = new TreeMap<>(stocks);
        for (Stock stock : sortedStocks.values()) {
            double change = stock.getPriceChange();
            String color = (change >= 0) ? TradingApp.ANSI_GREEN : TradingApp.ANSI_RED;
            String symbol = (change >= 0) ? "▲" : "▼";
            String changeStr = String.format("%s$%,.2f %s", color, Math.abs(change), symbol);

            System.out.printf("%-10s | %-25s | %12s | %s%n",
                    stock.ticker,
                    stock.name,
                    String.format("$%,.2f", stock.price),
                    changeStr + TradingApp.ANSI_RESET);
        }
        System.out.println(TradingApp.ANSI_CYAN + "----------------------------------------------------------------"
                + TradingApp.ANSI_RESET);
    }
}