import model.Order;
import model.Status;

import java.io.*;
import java.util.*;

public class OrderBookService {

    private static final TreeMap<Integer, Order> bidOrderBook = new TreeMap<>(Collections.reverseOrder());
    private static final TreeMap<Integer, Order> askOrderBook = new TreeMap<>();
    private static final TreeMap<Integer, Order> spreadOrderBook = new TreeMap<>();

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
        Order order = new Order();
        try {
            order.setPrice(Integer.valueOf(lineInputs[1]));
            order.setSize(Integer.parseInt(lineInputs[2]));
        } catch (NumberFormatException e) {
            System.out.println("String does not contain a parsable integer");
            return;
        }
        switch (lineInputs[3]) {
            case "bid":
                order.setStatus(Status.BID);
                bidOrderBook.put(order.getPrice(), order);
                break;
            case "ask":
                order.setStatus(Status.ASK);
                askOrderBook.put(order.getPrice(), order);
                break;
            case "spread":
                order.setStatus(Status.SPREAD);
                spreadOrderBook.put(order.getPrice(), order);
                break;
            default:
                System.out.println("Argument doesn't contain bid,ask or spread");
        }
    }

    static void findAndWriteOrder(String[] lineInputs) {
        Status status = lineInputs[1].equals("best_bid") ? Status.BID : lineInputs[1].equals("best_ask") ? Status.ASK : null;

        if (status != null) {
            TreeMap<Integer, Order> orderBook = status == Status.BID ? bidOrderBook : askOrderBook;

            for (Map.Entry<Integer, Order> orderEntry : orderBook.entrySet()) {
                if (orderEntry.getValue().getSize() > 0) {
                    writeOrderToFile(orderEntry.getValue().toString());
                    break;
                }
            }
        } else if (lineInputs[1].equals("size")) {
            Order order;

            try {
                int price = Integer.parseInt(lineInputs[2]);
                order = bidOrderBook.get(price);
                if (order == null) {
                    order = askOrderBook.get(price);
                }
            } catch (NumberFormatException e) {
                System.out.println("String does not contain a parsable integer");
                return;
            }
            if (order == null) {
                writeOrderToFile("0");
            } else {
                writeOrderToFile(String.valueOf(order.getSize()));
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
        Status status = lineInputs[1].equals("sell") ? Status.BID : lineInputs[1].equals("buy") ? Status.ASK : null;
        if (status == null) {
            System.out.println("Argument doesn't contain buy or sell");
            return;
        }

        TreeMap<Integer, Order> orderBook = status == Status.BID ? bidOrderBook : askOrderBook;

        for (Map.Entry<Integer, Order> orderEntry : orderBook.entrySet()) {
            if (orderEntry.getValue().getSize() > 0) {
                if (orderEntry.getValue().getSize() >= quantity) {
                    orderEntry.getValue().setSize(
                            orderEntry.getValue().getSize() - quantity);
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
