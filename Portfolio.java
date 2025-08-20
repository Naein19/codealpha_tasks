import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the user's assets, including cash and stocks, and handles data
 * persistence.
 */
public class Portfolio {
    private static final String PORTFOLIO_FILE = "portfolio.json";
    private static final double INITIAL_CASH = 100000.00;

    public double cash;
    public Map<String, StockHolding> stocks;
    public List<Transaction> transactions;

    public Portfolio() {
        this.stocks = new HashMap<>();
        this.transactions = new ArrayList<>();
        loadData();
    }

    private void loadData() {
        if (Files.exists(Paths.get(PORTFOLIO_FILE))) {
            try (FileReader reader = new FileReader(PORTFOLIO_FILE)) {
                Gson gson = new Gson();
                Portfolio savedPortfolio = gson.fromJson(reader, Portfolio.class);
                this.cash = savedPortfolio.cash;
                this.stocks = savedPortfolio.stocks != null ? savedPortfolio.stocks : new HashMap<>();
                this.transactions = savedPortfolio.transactions != null ? savedPortfolio.transactions
                        : new ArrayList<>();
                System.out.println(
                        TradingApp.ANSI_GREEN + "✔ Portfolio data loaded successfully." + TradingApp.ANSI_RESET);
            } catch (IOException e) {
                System.out.println(TradingApp.ANSI_RED + "Error loading portfolio: " + e.getMessage()
                        + ". Starting fresh." + TradingApp.ANSI_RESET);
                initializeNew();
            }
        } else {
            System.out.println(TradingApp.ANSI_YELLOW + "No portfolio file found. Starting with a new portfolio."
                    + TradingApp.ANSI_RESET);
            initializeNew();
        }
    }

    private void initializeNew() {
        this.cash = INITIAL_CASH;
        this.stocks = new HashMap<>();
        this.transactions = new ArrayList<>();
        saveData();
    }

    public void saveData() {
        try (FileWriter writer = new FileWriter(PORTFOLIO_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this, writer);
        } catch (IOException e) {
            System.out.println(TradingApp.ANSI_RED + "Fatal Error: Could not save portfolio data! " + e.getMessage()
                    + TradingApp.ANSI_RESET);
        }
    }

    public void addTransaction(String type, String ticker, int quantity, double price) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        this.transactions.add(new Transaction(timestamp, type, ticker, quantity, price));
        saveData();
    }

    public boolean buyStock(String ticker, int quantity, Market market) {
        Stock stock = market.getStock(ticker);
        double cost = quantity * stock.price;
        if (cash < cost) {
            System.out.printf(TradingApp.ANSI_RED + "Error: Not enough cash. You need $%,.2f but only have $%,.2f.%n"
                    + TradingApp.ANSI_RESET, cost, cash);
            return false;
        }

        this.cash -= cost;
        StockHolding holding = this.stocks.get(ticker);
        if (holding != null) {
            double newTotalQuantity = holding.quantity + quantity;
            double newAvgPrice = ((holding.avgPrice * holding.quantity) + cost) / newTotalQuantity;
            holding.quantity = (int) newTotalQuantity;
            holding.avgPrice = Math.round(newAvgPrice * 100.0) / 100.0;
        } else {
            this.stocks.put(ticker, new StockHolding(quantity, stock.price));
        }

        addTransaction("BUY", ticker, quantity, stock.price);
        System.out.printf(
                TradingApp.ANSI_GREEN + "%n✔ Successfully bought %d shares of %s for $%,.2f%n" + TradingApp.ANSI_RESET,
                quantity, ticker, cost);
        saveData();
        return true;
    }

    public boolean sellStock(String ticker, int quantity, Market market) {
        StockHolding holding = this.stocks.get(ticker);
        Stock stock = market.getStock(ticker);
        if (holding == null || holding.quantity < quantity) {
            int ownedQty = (holding == null) ? 0 : holding.quantity;
            System.out.printf(TradingApp.ANSI_RED + "Error: You cannot sell %d shares of %s. You only own %d.%n"
                    + TradingApp.ANSI_RESET, quantity, ticker, ownedQty);
            return false;
        }

        double revenue = quantity * stock.price;
        this.cash += revenue;
        holding.quantity -= quantity;

        if (holding.quantity == 0) {
            this.stocks.remove(ticker);
        }

        addTransaction("SELL", ticker, quantity, stock.price);
        System.out.printf(
                TradingApp.ANSI_GREEN + "%n✔ Successfully sold %d shares of %s for $%,.2f%n" + TradingApp.ANSI_RESET,
                quantity, ticker, revenue);
        saveData();
        return true;
    }

    public double getTotalValue(Market market) {
        double stockValue = 0.0;
        for (Map.Entry<String, StockHolding> entry : stocks.entrySet()) {
            String ticker = entry.getKey();
            StockHolding holding = entry.getValue();
            stockValue += holding.quantity * market.getStock(ticker).price;
        }
        return this.cash + stockValue;
    }
}