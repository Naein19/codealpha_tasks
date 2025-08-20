import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Main application class that drives the trading simulation.
 */
public class TradingApp {
    // ANSI escape codes for colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BOLD = "\u001B[1m";

    private Market market;
    private Portfolio portfolio;
    private Scanner scanner;

    public TradingApp() {
        this.market = new Market();
        this.portfolio = new Portfolio();
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        TradingApp app = new TradingApp();
        app.run();
    }

    public void run() {
        while (true) {
            market.updateMarket();
            displayMainMenu();
            System.out.print(ANSI_BOLD + "Choose an option: " + ANSI_RESET);
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewMarket();
                    break;
                case "2":
                    viewPortfolio();
                    break;
                case "3":
                    handleBuy();
                    break;
                case "4":
                    handleSell();
                    break;
                case "5":
                    viewTransactions();
                    break;
                case "6":
                    portfolio.saveData();
                    System.out.println(ANSI_YELLOW + "\nSaving portfolio and exiting. Happy trading! 👋" + ANSI_RESET);
                    return;
                default:
                    System.out.println(ANSI_RED + "Invalid option. Please try again." + ANSI_RESET);
            }

            try {
                System.out.println("\n" + ANSI_YELLOW + "Market prices are updating..." + ANSI_RESET);
                Thread.sleep(2000); // Pause to simulate time passing
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void displayMainMenu() {
        clearConsole();
        System.out.println(ANSI_GREEN + "=======================================================");
        System.out.printf("  📊 Portfolio Summary: Cash: $%,.2f | Value: $%,.2f%n", portfolio.cash,
                portfolio.getTotalValue(market));
        System.out.println("=======================================================" + ANSI_RESET);
        System.out.println(ANSI_CYAN + " Main Menu");
        System.out.println("---------------------");
        System.out.println(" [1] View Market Data 📈");
        System.out.println(" [2] View My Portfolio 💼");
        System.out.println(" [3] Buy Stock 🛒");
        System.out.println(" [4] Sell Stock 💰");
        System.out.println(" [5] View Transaction History 📜");
        System.out.println(" [6] Exit 🚪" + ANSI_RESET);
    }

    private void pressEnterToContinue() {
        System.out.print("\n" + ANSI_YELLOW + "Press Enter to return to the menu..." + ANSI_RESET);
        scanner.nextLine();
    }

    private void viewMarket() {
        clearConsole();
        market.displayMarket();
        pressEnterToContinue();
    }

    private void viewPortfolio() {
        clearConsole();
        System.out.println(ANSI_GREEN + "--- My Portfolio ---" + ANSI_RESET);
        System.out.println(ANSI_CYAN
                + "---------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %10s | %15s | %15s | %15s | %15s%n", "Ticker", "Shares", "Avg. Buy Price",
                "Current Price", "Market Value", "Profit/Loss");
        System.out.println(
                "---------------------------------------------------------------------------------------------------------"
                        + ANSI_RESET);

        double totalStockValue = 0;
        double totalInvestmentCost = 0;

        Map<String, StockHolding> sortedStocks = new TreeMap<>(portfolio.stocks);
        for (Map.Entry<String, StockHolding> entry : sortedStocks.entrySet()) {
            String ticker = entry.getKey();
            StockHolding holding = entry.getValue();
            Stock stock = market.getStock(ticker);

            double marketValue = holding.quantity * stock.price;
            double investmentCost = holding.quantity * holding.avgPrice;
            double profitLoss = marketValue - investmentCost;

            totalStockValue += marketValue;
            totalInvestmentCost += investmentCost;

            String color = (profitLoss >= 0) ? ANSI_GREEN : ANSI_RED;
            String plStr = String.format("%s$%,.2f%s", color, profitLoss, ANSI_RESET);

            System.out.printf("%-10s | %10d | %15s | %15s | %15s | %15s%n",
                    ticker, holding.quantity, String.format("$%,.2f", holding.avgPrice),
                    String.format("$%,.2f", stock.price), String.format("$%,.2f", marketValue), plStr);
        }
        System.out.println(ANSI_CYAN
                + "---------------------------------------------------------------------------------------------------------"
                + ANSI_RESET);

        System.out.println(ANSI_YELLOW + "\n--- Financial Summary ---");
        System.out.printf(" Cash Balance:      $%,.2f%n", portfolio.cash);
        System.out.printf(" Total Stock Value: $%,.2f%n", totalStockValue);
        System.out.printf(ANSI_BOLD + " Total Portfolio Value: $%,.2f%n" + ANSI_RESET, portfolio.getTotalValue(market));
        System.out.println(ANSI_RESET);
        pressEnterToContinue();
    }

    private void handleBuy() {
        clearConsole();
        market.displayMarket();
        System.out.printf("%nYour cash: " + ANSI_GREEN + "$%,.2f%n" + ANSI_RESET, portfolio.cash);
        System.out.print(ANSI_BOLD + "Enter the ticker of the stock you want to buy: " + ANSI_RESET);
        String ticker = scanner.nextLine().toUpperCase();
        Stock stock = market.getStock(ticker);

        if (stock == null) {
            System.out.println(ANSI_RED + "Error: Stock '" + ticker + "' not found." + ANSI_RESET);
            return;
        }

        System.out.printf("Current price of %s: " + ANSI_BOLD + "$%,.2f%n" + ANSI_RESET, ticker, stock.price);
        System.out.print(ANSI_BOLD + "How many shares do you want to buy? " + ANSI_RESET);
        try {
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (quantity <= 0) {
                System.out.println(ANSI_RED + "Error: Quantity must be a positive number." + ANSI_RESET);
                return;
            }
            portfolio.buyStock(ticker, quantity, market);
        } catch (InputMismatchException e) {
            System.out.println(ANSI_RED + "Invalid input. Please enter a number for quantity." + ANSI_RESET);
            scanner.nextLine(); // Clear invalid input
        }
        pressEnterToContinue();
    }

    private void handleSell() {
        clearConsole();
        viewPortfolio();

        if (portfolio.stocks.isEmpty()) {
            System.out.println(ANSI_YELLOW + "\nYou do not own any stocks to sell." + ANSI_RESET);
            pressEnterToContinue();
            return;
        }

        System.out.print(ANSI_BOLD + "\nEnter the ticker of the stock you want to sell: " + ANSI_RESET);
        String ticker = scanner.nextLine().toUpperCase();

        if (!portfolio.stocks.containsKey(ticker)) {
            System.out.println(ANSI_RED + "Error: You do not own any shares of '" + ticker + "'." + ANSI_RESET);
            pressEnterToContinue();
            return;
        }

        System.out.print(ANSI_BOLD + "How many shares do you want to sell? " + ANSI_RESET);
        try {
            int quantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (quantity <= 0) {
                System.out.println(ANSI_RED + "Error: Quantity must be a positive number." + ANSI_RESET);
                return;
            }
            portfolio.sellStock(ticker, quantity, market);
        } catch (InputMismatchException e) {
            System.out.println(ANSI_RED + "Invalid input. Please enter a number for quantity." + ANSI_RESET);
            scanner.nextLine(); // Clear invalid input
        }
        pressEnterToContinue();
    }

    private void viewTransactions() {
        clearConsole();
        System.out.println(ANSI_YELLOW + "--- Transaction History ---" + ANSI_RESET);
        System.out.println(ANSI_CYAN
                + "--------------------------------------------------------------------------------------------");
        System.out.printf("%-22s | %-6s | %-8s | %10s | %12s | %15s%n", "Timestamp", "Type", "Ticker", "Quantity",
                "Price/Share", "Total Value");
        System.out
                .println("--------------------------------------------------------------------------------------------"
                        + ANSI_RESET);

        if (portfolio.transactions.isEmpty()) {
            System.out.println("No transactions have been made yet.");
        } else {
            for (int i = portfolio.transactions.size() - 1; i >= 0; i--) {
                Transaction t = portfolio.transactions.get(i);
                String color = t.type.equals("BUY") ? ANSI_GREEN : ANSI_RED;
                System.out.printf("%-22s | %s%-6s%s | %-8s | %10d | %12s | %15s%n",
                        t.timestamp, color, t.type, ANSI_RESET, t.ticker, t.quantity,
                        String.format("$%,.2f", t.price), String.format("$%,.2f", t.total));
            }
        }
        System.out.println(ANSI_CYAN
                + "--------------------------------------------------------------------------------------------"
                + ANSI_RESET);
        pressEnterToContinue();
    }
}