import model.Order;
import model.Status;

import java.io.*;
import java.util.*;

public class OrderBookService {

    private static final TreeMap<Integer, Order> orderBook = new TreeMap<>(Collections.reverseOrder());

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

            String[] strArr;
            String str;
            while ((str = br.readLine()) != null) {
                strArr = str.split(",");



                switch (strArr[0]) {
                    case "u":
                        if (strArr.length != 4) {
                            break;
                        }
                        updateOrderBook(strArr);
                        break;
                    case "q":
                        if (strArr.length < 2 ||  strArr.length > 3) {
                           break;
                        }
                        findAndWriteOrder(strArr);
                        break;
                    case "o":
                        if (strArr.length != 3) {
                            break;
                        }
                        buyOrSell(strArr);
                        break;
                    default:
                        System.out.println("Argument doesn't contain as first letter o,u or q");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void updateOrderBook(String[] args) {
        Order order = new Order();
        try {
            order.setPrice(Integer.valueOf(args[1]));
            order.setSize(Integer.parseInt(args[2]));
        } catch (NumberFormatException e) {
            System.out.println("String does not contain a parsable integer");
            return;
        }
        switch (args[3]) {
            case "bid":
                order.setStatus(Status.BID);
                break;
            case "ask":
                order.setStatus(Status.ASK);
                break;
            case "spread":
                order.setStatus(Status.SPREAD);
                break;
            default:
                System.out.println("Argument doesn't contain bid,ask or spread");
                return;
        }
        orderBook.put(order.getPrice(), order);
    }

    static void findAndWriteOrder(String[] args) {
        Status status = args[1].equals("best_bid") ? Status.BID : args[1].equals("best_ask") ? Status.ASK : null;

        if (status != null) {
            for (Map.Entry<Integer, Order> integerOrderEntry : orderBook.entrySet()) {
                if (integerOrderEntry.getValue().getStatus().equals(status) && integerOrderEntry.getValue().getSize() > 0) {
                    writeOrderToFile(integerOrderEntry.getValue().toString());
                    break;
                }
            }
        } else if (args[1].equals("size")) {
            Order order;

            try {
                order = orderBook.get(Integer.valueOf(args[2]));
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

    private static void buyOrSell(String[] strArr) {
        int quantity;
        try {
            quantity = Integer.parseInt(strArr[2]);
        } catch (NumberFormatException e) {
            System.out.println("String does not contain a parsable integer");
            return;
        }
        Status status = strArr[1].equals("sell") ? Status.BID : strArr[1].equals("buy") ? Status.ASK : null;
        if (status == null) {
            System.out.println("Argument doesn't contain buy or sell");
            return;
        }

        for (Map.Entry<Integer, Order> integerOrderEntry : orderBook.entrySet()) {
            if (integerOrderEntry.getValue().getStatus().equals(status) && integerOrderEntry.getValue().getSize() > 0) {
                if (integerOrderEntry.getValue().getSize() >= quantity) {
                    integerOrderEntry.getValue().setSize(
                            integerOrderEntry.getValue().getSize() - quantity);
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
