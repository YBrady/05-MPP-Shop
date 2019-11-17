package Shop;

//Import all the packages needed 
import java.io.*; // system input and output through data streams, serialization and the file system
import java.nio.charset.StandardCharsets; // Defines charsets, decoders, and encoders, for translating between bytes and Unicode characters
import java.nio.file.Files; // for accessing files, file attributes, and file systems.
import java.nio.file.Paths; // for accessing files, file attributes, and file systems.
import java.util.ArrayList; // for ArrayLists (ShoppingList is ArrayList of ProductStock
import java.util.Collections; // static methods that operate on or return collections.
import java.util.List; // looks after lists
import java.util.Scanner; // for user inputs

public class Shop {
	
	// Variables used to define the shop
	private double cash;
	private ArrayList<ProductStock> stock;

	public Shop(String fileName) {
		// The constructor
		stock = new ArrayList<>();
		// List to read the csv lines into
		List<String> lines = Collections.emptyList();
		
		// try - to provide some error handling if there is a problem with the file
		try {
			// Read each line into the lines list
			lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
			// Cash is in the first line
			cash = Double.parseDouble(lines.get(0));
			// Removing first line as it is the only non-stock item
			lines.remove(0);
			// For every line in your file
			for (String line : lines) {
				// Define the comma as the delimiter between values
				String[] arr = line.split(",");
				// Product Name, Price and Quantity values separated 
				String name = arr[0];
				double price = Double.parseDouble(arr[1]);
				int quantity = Integer.parseInt(arr[2].trim()); // trim to get rid of whitespace
				// Map to the shop variables
				Product p = new Product(name, price);
				ProductStock s = new ProductStock(p, quantity);
				// Add to the shops stock
				stock.add(s);
			}
		}

		catch (IOException e) {
			// if file is not found or unavailable
			System.out.println("Error accessing " + fileName + " file. Please ensure this file exists and is accessible.");
			System.out.println("Quitting shop. Goodbye!");
			System.exit(0);
		}
	}

	// Getters
	public double getCash() {
		return cash;
	}

	public ArrayList<ProductStock> getStock() {
		return stock;
	}

	// Print out the shop contents
	@Override
	public String toString() {
		return ("\n*** SHOP CONTENTS ***\n\nFLOAT: EUR " + String.format("%.2f", cash) + "\n\n" + stock + "\n").replace("[","").replace("]","").replace(", ", "");
		
	}

	// Finds the product from the stock list - used to retrieve price and quantity information 
	// Takes in the product name
	private ProductStock findProduct(String name) {
		// Loops through the stock list
		for (ProductStock ps : stock) {
			// Gets the product info
			Product p = ps.getProduct();
			// Compares the product names
			if (p.getName().equals(name)) {
				// If they match - return the product stock info
				return ps;
			}
		}
		// If the product is not found
		Product p = new Product("error", -1.0); 
		ProductStock ps = new ProductStock(p,0);
		return ps;
	}
	
	// Adds the prices to the shopping list
	public void applyPrices(Customer c) {
		// Loops through each item on the shopping list
		for (ProductStock productStock : c.getShoppingList()) {
			// Get the product from the product stock
			Product p = productStock.getProduct();
			// Get the shops Product Stock info on the product and then its price  
			double price = findProduct(p.getName()).getProduct().getPrice();
			// Set the price in the shopping list
			p.setPrice(price);
		}
	}
	
	// This is the method use to perform the transaction
	public void processOrder(Customer c) {
		// If it is in Shopping List mode - print some header info
		if (c.getName() != "Live Mode") {
			String head = "\n*** "+ c.getName().toUpperCase() + "'S SHOPPING LIST ***\n";
			head += "\nCUSTOMER NAME: " + c.getName();
			head +="\nCUSTOMER BUDGET:" + String.format("%.2f",c.getBudget());
			head += "\n-------------";
			System.out.println(head);
		}
		// ret used to build a string with the output
		String ret;
		// Loop through was shopping list item
		// In the case of live mode there will only be one item on the list
		for (ProductStock cListItem : c.getShoppingList()) {
			// Determine the quantity required
			int qr = cListItem.getQuantity();
			// The product required - used throughout so better to make it a parameter
			Product prodReq = cListItem.getProduct();
			// Get the price of the product
			double pr = prodReq.getPrice();
			// The product name
			String pname = prodReq.getName();
			// Find the associated product stock item inthe shop
			ProductStock shopProd = findProduct(pname);
			// calculate the cost of the purchase
			double cost = pr*qr;
			// Create a new parameter for the quantity actually bought. 
			// This is set to the quantity required initially 
			// but may be revised if the full order cannot be met.
			int qb = qr; // quantity bought - assumed initially to be the amount requested
			// Start building the ret string
			ret = "PRODUCT NAME: " + prodReq.getName();
			ret +="\nPRODUCT PRICE: EUR "+ String.format("%.2f", pr);
			ret += "\nQUANTITY REQUIRED: " + qr;
			
			if (pr > -1) {
				// Checks to see if the user has enough money
				if (c.getBudget() < cost){
					// If they don't have enough money, set the quantity bought to what they can afford
					// Dividing two ints will result in the quotient returned so no other cals required.
					qb = (int)(c.getBudget() / pr);
				}
				
				// Checks to see if the shop has enough stock
				if(qb >  shopProd.getQuantity()) {
					// If not set the quantity bought to the quantity remaining in shop.
					qb = shopProd.getQuantity();
				}
				
				// Adjust cost accordingly
				cost = (pr*qb);
				
				// Update the customer cash and quantity details to reflect the purchases made 
				cListItem.setQuantity(qr-qb);
				c.setBudget(c.getBudget()-cost);
				
				// Update the shop float and stock details
				shopProd.setQuantity(shopProd.getQuantity()-qb);
				cash += cost;
				
				// Continue to build the report out string with the details
				ret += "\nQUANTITY PURCHASED: " + qb;
				ret += "\nTOTAL ITEM COST: EUR "+ String.format("%.2f", cost);
				ret += "\nADJUSTED BUDGET: EUR " + String.format("%.2f",c.getBudget());
				ret += "\nADJUSTED SHOP FLOAT: EUR " + String.format("%.2f",cash);
				// If the order was not fulfilled completely
				if(qb != qr) {
					// Inform the customer
					ret +="\nUnable to fulfill complete order on this item due to insufficient funds / stock.";
				}
			}
			else {
				// If product is not in stock list
				ret+= "\nSorry, shop does not stock " + prodReq.getName();
			}
			ret += "\n-------------";
			System.out.println(ret);
				
			}
	}
	
	// Method to perform the live mode
	public void liveMode() {
		// New scanner - this is not closed as to do so would also close the main menu scanner. 
		// Both are closed on exiting from the main menu.
		Scanner liveScan = new Scanner(System.in);
		// Header info
		System.out.println();		
		System.out.println("You are now entering Live Mode!");
		System.out.println("What is your budget in EURO (to two decimal places)?");
		
		// Record the customer budget
		double liveBudget = liveScan.nextDouble();
		// To track the cutomers menu choice 
		int liveChoice;
		
		// Keep repeating the menu until the customer exits live mode
	    do {
			// Header info
			System.out.println("\n-------------------------------\n Welcome to the JAVA Shop - LIVE MODE \n-------------------------------");
			System.out.println("How can we help?\nYour current budget is EUR "+ String.format("%.2f",liveBudget));
			System.out.println("Please select what you would like to buy from the list below:\n");
			
			// Used to keep track of choices
			int i = 1;
			// Menu selection items - dynamically built from product stock
			// Loop through each stock item
			for (ProductStock ps : stock) {
				Product p = ps.getProduct();
				// Add that item as a menu choice together with the price of the product
				System.out.println(i + " - " + p.getName() + " @ " + String.format("%.2f",p.getPrice())+ " each.");
				i+=1;
			}
			// Add the option to exit and instructions to make the choice
			System.out.println("99 - Exit Live Mode"); // Out
			System.out.println("\nPlease make your selection:");
	    	
			// Wait until an integer has been entered
			liveChoice = liveScan.nextInt();
			// If it is a valid choice
			if (liveChoice <i) {
				// Record the product name as a variable
				String prodName = stock.get(liveChoice-1).getProduct().getName();
				// See how many the customer would like to purchase?
				System.out.println("How many "+ prodName + "s would you like to purchase?");
				// Record the quantity required
				int liveQuantity = liveScan.nextInt();
				// Find the price of the product as before
				double livePrice = findProduct(prodName).getProduct().getPrice();
				
				// Put that info into product and product stock formats
				Product liveProd = new Product(prodName,livePrice);
				ProductStock liveItem = new ProductStock(liveProd,liveQuantity);
				// Create the new customer with the live details
				Customer liveCust = new Customer("Live Mode", liveBudget, liveItem);

				// Perform the transaction as above
				processOrder(liveCust);
				
				// Update the budget after purchase
				liveBudget = liveCust.getBudget();
			}
				// On exit live mode
			else if (liveChoice == 99) {	    
				System.out.println("Thank you for chosing LIVE mode. Please come again soon.");

				}
				// Otherwise - an error message
			else {
				System.out.println("Please enter a valid selection");
			}
	       // Exit on 99
	    } while (liveChoice != 99);
	}
}