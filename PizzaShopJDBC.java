import java.sql.*;
import java.util.Scanner;

public class PizzaShopJDBC {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Database connection details
        String url = "jdbc:mysql://sql12.freesqldatabase.com:3306/sql12751851?useSSL=false&serverTimezone=Asia/Kolkata"; // Fixed Time Zone
        String username = "sql12751851";  // Database username
        String password = "hyNsVk5abn";  // Database password

        // Establish connection to the database
        try {
            // Explicitly load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            try (Connection connection = DriverManager.getConnection(url, username, password)) {

                // Greet the customer
                System.out.println("Welcome to the Pizza Shop! 🍕");

                // Ask for customer's name
                System.out.print("Please enter your name: ");
                String customerName = scanner.nextLine();

                // Validate phone number to ensure it has exactly 10 digits
                String phoneNumber = "";
                while (true) {
                    System.out.print("Please enter your 10-digit phone number: ");
                    phoneNumber = scanner.nextLine();
                    if (phoneNumber.matches("\\d{10}")) {
                        break;
                    } else {
                        System.out.println("Invalid phone number! Please enter exactly 10 digits.");
                    }
                }

                // Pizza sizes and their prices
                String[] sizes = {"Small", "Medium", "Large"};
                double[] sizePrices = {200.0, 300.0, 400.0};  // Prices in INR

                // Toppings and their prices
                String[] toppings = {
                    "Cheese", "Olives", "Mushrooms", "Peppers", "Bacon",
                    "Pineapple", "Onions", "Tomatoes", "Chicken", "Spinach"
                };
                double[] toppingPrices = {30.0, 40.0, 50.0, 60.0, 70.0, 40.0, 50.0, 30.0, 80.0, 40.0};

                // Ask for pizza size with validation
                int sizeChoice = 0;
                while (true) {
                    System.out.println("\nChoose a pizza size:");
                    for (int i = 0; i < sizes.length; i++) {
                        System.out.println((i + 1) + ". " + sizes[i] + " - RS. " + sizePrices[i]);
                    }
                    System.out.print("Please enter the number corresponding to your pizza size (1-3): ");
                    sizeChoice = scanner.nextInt();

                    if (sizeChoice >= 1 && sizeChoice <= 3) {
                        break;  // Valid input, exit the loop
                    } else {
                        System.out.println("Invalid choice! Please select a number between 1 and 3.");
                    }
                }

                // Get the price of the chosen pizza size
                double totalPrice = sizePrices[sizeChoice - 1];

                // Ask for toppings with validation
                System.out.println("\nSelect toppings (Choose 0 to stop):");
                for (int i = 0; i < toppings.length; i++) {
                    System.out.println((i + 1) + ". " + toppings[i] + " - RS. " + toppingPrices[i]);
                }

                double toppingCost = 0.0;
                StringBuilder selectedToppings = new StringBuilder();
                while (true) {
                    System.out.print("Enter the topping number you want to add (0 to finish): ");
                    int toppingChoice = scanner.nextInt();

                    if (toppingChoice == 0) {
                        break;  // Stop adding toppings
                    } else if (toppingChoice > 0 && toppingChoice <= toppings.length) {
                        toppingCost += toppingPrices[toppingChoice - 1];
                        selectedToppings.append(toppings[toppingChoice - 1]).append(" ");
                    } else {
                        System.out.println("Invalid topping choice! Please select a valid topping number between 1 and 10.");
                    }
                }

                // Calculate the total price
                totalPrice += toppingCost;

                // Ask for order confirmation
                scanner.nextLine(); // Consume newline character
                System.out.println("\nYour Order Summary:");
                System.out.println("Customer Name: " + customerName);
                System.out.println("Phone Number: " + phoneNumber);
                System.out.println("Pizza Size: " + sizes[sizeChoice - 1]);
                System.out.println("Toppings: " + (selectedToppings.length() > 0 ? selectedToppings.toString() : "None"));
                System.out.println("Total Price: RS. " + totalPrice);

                // Confirm the order
                System.out.print("Do you want to confirm your order? (Yes/No): ");
                String confirmation = scanner.nextLine().toLowerCase();

                if (confirmation.equals("yes")) {
                    // Insert order into database
                    String sql = "INSERT INTO orders (customer_name, phone_number, pizza_size, toppings, total_price) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setString(1, customerName);
                        pstmt.setString(2, phoneNumber);
                        pstmt.setString(3, sizes[sizeChoice - 1]);
                        pstmt.setString(4, selectedToppings.toString().trim());
                        pstmt.setDouble(5, totalPrice);
                        pstmt.executeUpdate();
                    }

                    System.out.println("\nThank you for your order! Your pizza will be ready soon! 🍕");
                } else {
                    System.out.println("Your order has been canceled.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } finally {
            // Close the scanner to avoid resource leaks
            scanner.close();
        }
    }
}
