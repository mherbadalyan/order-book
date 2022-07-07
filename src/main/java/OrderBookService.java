
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class OrderBookService {

    private static final TreeMap<Integer, Integer> bidOrderBook = new TreeMap<>(Collections.reverseOrder());
    private static final TreeMap<Integer, Integer> askOrderBook = new TreeMap<>();


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
                (new FileInputStream(inputFile)));
             BufferedWriter out = new BufferedWriter(new FileWriter(outputFile, true))) {

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
                        if (lineInputs.length < 2 || lineInputs.length > 3) {
                            break;
                        }
                        findAndWriteOrder(lineInputs,out);
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

        TreeMap<Integer, Integer> orderBook;
        switch (lineInputs[3]) {
            case "bid":
                orderBook = bidOrderBook;
                break;
            case "ask":
                orderBook = askOrderBook;
                break;
            default:
                System.out.println("Argument doesn't contain bid or ask");
                return;
        }
        if (size == 0) {
            orderBook.remove(price);
        }else {
            orderBook.put(price, size);
        }
    }

    static void findAndWriteOrder(String[] lineInputs,BufferedWriter out) throws IOException {
        String status = lineInputs[1];

        if (status.equals("best_ask") || status.equals("best_bid")) {
            TreeMap<Integer, Integer> orderBook = status.equals("best_bid") ? bidOrderBook : askOrderBook;

            Map.Entry<Integer, Integer> first = orderBook.entrySet().iterator().next();
            writeOrderToFile(first.getKey() + "," + first.getValue(),out);

        } else if (status.equals("size")) {
            int size;
            int price;
            try {
                price = Integer.parseInt(lineInputs[2]);
            } catch (NumberFormatException e) {
                System.out.println("String does not contain a parsable integer");
                return;
            }
            size = bidOrderBook.getOrDefault(price, 0) +
                    askOrderBook.getOrDefault(price, 0);
            writeOrderToFile(String.valueOf(size),out);
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

        int reminder = quantity;
        Iterator<Map.Entry<Integer, Integer>> iterator = orderBook.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Integer> entry = iterator.next();
            if (reminder == 0) {
                break;
            }
            int size = entry.getValue();
            if (reminder >= size) {
                reminder -= size;
                iterator.remove();
            }else {
                entry.setValue(size - reminder);
                reminder = 0;
            }
        }

    }

    private static void writeOrderToFile(String text,BufferedWriter out) throws IOException {
            out.write(text);
            out.newLine();

    }
}
