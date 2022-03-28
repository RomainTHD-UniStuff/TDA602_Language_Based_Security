import backend.Pocket;
import backend.Store;
import backend.Wallet;

import java.util.Scanner;

public class ShoppingCart {
    private static Scanner scanner;

    private static void print(Wallet wallet, Pocket pocket) {
        System.out.println("\n");
        System.out.println("Your current balance is: " + wallet.getBalance() + " credits.");
        System.out.println(Store.asString());
        System.out.println("Your current pocket is:\n" + pocket.getPocket());
    }

    private static String scan() {
        System.out.print("What do you want to buy? (empty line to stop) > ");
        return scanner.nextLine();
    }

    private static boolean shouldQuit(String msg) {
        return msg.equals("quit")
               || msg.equals("exit")
               || msg.equals("q")
               || msg.equals("");
    }

    private static void delay() {
        System.out.println("(waiting for input to resume...)");
        scanner.nextLine();
    }

    public static void main(String[] args) {
        Wallet wallet = new Wallet();
        Pocket pocket = new Pocket();
        scanner = new Scanner(System.in);

        print(wallet, pocket);
        String product;

        while (!shouldQuit(product = scan().trim().toLowerCase())) {
            /* TODO:
               - check if the amount of credits is enough, if not stop the execution.
               - otherwise, withdraw the price of the product from the wallet.
               - add the name of the product to the pocket file.
               - print the new balance.
            */

            Integer price = Store.getProductPrice(product);

            if (price == null) {
                System.out.println("Product not found.");
                continue;
            }

            int balance = wallet.getBalance();

            delay();
            /*
            Step 1: start buying a candy, credits = 30000
            Step 2: buy the car, credits = 0
            Step 3: resume buying the candy, credits = 29999
            */

            if (price > balance) {
                System.out.println("You don't have enough credits to buy this product.");
                continue;
            } else {
                wallet.setBalance(balance - price);
                pocket.addProduct(product);
            }

            // Just to print everything again...
            print(wallet, pocket);
        }
    }
}
