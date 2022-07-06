
import java.io.*;
import java.util.*;
import java.util.function.BiFunction;

public class OrderBookService {

    private static final TreeMap<Integer, Integer> bidOrderBook = new TreeMap<>(Collections.reverseOrder());
    private static final TreeMap<Integer, Integer> askOrderBook = new TreeMap<>();
    private static final TreeMap<Integer, Integer> spreadOrderBook = new TreeMap<>();

    static final File inputFile = new File("input.txt");

    static final File outputFile = new File("output.txt");

    public static void main(String[] args) {
        if (!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader
                (new FileInputStream(inputFile)))) {

            String[] lineInputs;
            String line;
            while ((line = br.readLine()) != null) {
                lineInputs = line.split(",");

                switch (lineInputs[0]) {
                    case "u":
                        if (lineInputs.length != 4) {
                            break;
                        }
                        updateOrderBook(lineInputs);
                        break;
                    case "q":
                        if (lineInputs.length < 2 ||  lineInputs.length > 3) {
                           break;
                        }
                        findAndWriteOrder(lineInputs);
                        break;
                    case "o":
                        if (lineInputs.length != 3) {
                            break;
                        }
                        buyOrSell(lineInputs);
                        break;
                    default:
                        System.out.println("Argument doesn't contain as first letter o,u or q");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void updateOrderBook(String[] lineInputs) {
        int size;
        int price;
        try {
            price = Integer.parseInt(lineInputs[1]);
            size = Integer.parseInt(lineInputs[2]);
        } catch (NumberFormatException e) {
            System.out.println("String does not contain a parsable integer");
            return;
        }
        switch (lineInputs[3]) {
            case "bid":
                bidOrderBook.merge(price,size,(k, v) -> bidOrderBook.get(price) + size);
                break;
            case "ask":
                askOrderBook.merge(price,size,(k, v) -> askOrderBook.get(price) + size);
                break;
            case "spread":
                spreadOrderBook.merge(price,size,(k, v) -> spreadOrderBook.get(price) + size);
                break;
            default:
                System.out.println("Argument doesn't contain bid,ask or spread");
        }
    }

    static void findAndWriteOrder(String[] lineInputs) {
        String status = lineInputs[1];

        if (status.equals("best_ask") || status.equals("best_bid")) {
            TreeMap<Integer, Integer> orderBook = status.equals("best_bid") ? bidOrderBook : askOrderBook;

            for (Map.Entry<Integer, Integer> orderEntry : orderBook.entrySet()) {
                if (orderEntry.getValue() > 0) {
                    writeOrderToFile(orderEntry.getKey() + "," + orderEntry.getValue());
                    break;
                }
            }
        } else if (lineInputs[1].equals("size")) {
            Integer size;
            try {
                int price = Integer.parseInt(lineInputs[2]);
                size = bidOrderBook.get(price);
                if (size == null) {
                    size = askOrderBook.get(price);
                }
            } catch (NumberFormatException e) {
                System.out.println("String does not contain a parsable integer");
                return;
            }
            if (size == null) {
                writeOrderToFile("0");
            } else {
                writeOrderToFile(String.valueOf(size));
            }
        } else {
            System.out.println("Illegal argument");
        }
    }

    private static void buyOrSell(String[] lineInputs) {
        int quantity;
        try {
            quantity = Integer.parseInt(lineInputs[2]);
        } catch (NumberFormatException e) {
            System.out.println("String does not contain a parsable integer");
            return;
        }
        String operation = lineInputs[1];
        if (!operation.equals("buy") && !operation.equals("sell")) {
            System.out.println("Argument doesn't contain buy or sell");
            return;
        }

        TreeMap<Integer, Integer> orderBook = operation.equals("sell") ? bidOrderBook : askOrderBook;

        for (Map.Entry<Integer, Integer> orderEntry : orderBook.entrySet()) {
            if (orderEntry.getValue()  > 0) {
                if (orderEntry.getValue() >= quantity) {
                    orderEntry.setValue(
                            orderEntry.getValue()  - quantity);
                    break;
                } else {
                    System.out.println("Insufficient size of shares");
                    return;
                }
            }
        }
    }

    private static void writeOrderToFile(String text) {

        try (BufferedWriter out = new BufferedWriter(new FileWriter(outputFile, true))) {
            out.write(text);
            out.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
