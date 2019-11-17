package Shop;

// Import all the packages needed 
import java.io.IOException; // Error handling
import java.nio.charset.StandardCharsets; // Defines charsets, decoders, and encoders, for translating between bytes and Unicode characters.
import java.nio.file.Files; // for accessing files, file attributes, and file systems.
import java.nio.file.Paths; // for accessing files, file attributes, and file systems.
import java.util.ArrayList; // for ArrayLists (ShoppingList is ArrayList of ProductStock
import java.util.Collections; // static methods that operate on or return collections.
import java.util.List; // looks after lists

public class Customer {
	// Declaring the variables
	private String name;
	private double budget;
	private ArrayList<ProductStock> shoppingList;
	
	// The class constructor - this is used in both shopping list & live modes
	public Customer(String fileName, double liveBudget, ProductStock LiveItem) {
		// Reading the shopping list to take the data
		shoppingList = new ArrayList<ProductStock>();
		// Customer will know it is in live mode if "Live Mode" is the file nsme
		if (fileName == "Live Mode") {
			name = "Live Mode"; // just adding a name here for troubleshooting purposes
			budget = liveBudget; // set the budget
			shoppingList.add(LiveItem); // live mode has a shopping list of 1 item
		}
		// The rest is for reading the information from a file
		else {
			// Create a list to store each line
			List<String> lines = Collections.emptyList();
		try {
			// Read the file
			lines = Files.readAllLines(Paths.get(fileName), StandardCharsets.UTF_8);
			// First line is different as it holds the customer name and budget
			String[] firstLine = lines.get(0).split(",");
			// Store those values as variables
			name = firstLine[0];
			budget = Double.parseDouble(firstLine[1]);

			// Removing first line so all that remains is a shopping list item
			lines.remove(0);

			// Go through each of the lines
			for (String line : lines) {
				String[] arr = line.split(","); // defining the split between values
				String name = arr[0]; // getting product name
				int quantity = Integer.parseInt(arr[1].trim()); // and quantity - removing whitespace as would see it as string otherwise
				Product p = new Product(name, 0); // creating a product - don't care about price at this stage
				ProductStock s = new ProductStock(p, quantity); // creating the shopping list item
				
				shoppingList.add(s); // Add it to the shopping list
			}
		}

		catch (IOException e) {
			// if file is not found or unavailable
			System.out.println("Error accessing " + fileName + " file. Please ensure this file exists and is accessible.");
			System.out.println("Quitting shop. Goodbye!");
			System.exit(0);
		}}
	}
	// Getters
	public String getName() {
		return name;
	}

	public double getBudget() {
		return budget;
	}

	public ArrayList<ProductStock> getShoppingList() {
		return shoppingList;
	}

	// Setters
	public void setBudget(double budget) {
		this.budget = budget;
	}
	
	// Constructing what is printed to screen when this class is asked to be printed
	// Override required so to override the default printing to screen
	@Override
	public String toString() {
		// ret is a string that is built up and eventually printed out
		// Need to set it to the null string as it is built upon throughout
		// and if in Shopping List mode will have a header, but not in live mode
		String ret = ""; 
		// If it is in list mode - add a header
		if (name != "Live Mode") {
			ret +="\n*** "+ name.toUpperCase() + "'S SHOPPING LIST ***\n ";
			ret += "\nCUSTOMER NAME: " + name;
			ret +="\nCUSTOMER BUDGET:" + String.format("%.2f",budget);
			ret += "\n-------------\n";
		}
		
		// Loop through each list item (only one for live mode) and print out each value
		for (ProductStock productStock : shoppingList) {
			// Print out the details required
			ret += "PRODUCT NAME: " + productStock.getProduct().getName();
			ret +="\nPRODUCT PRICE: EUR "+ String.format("%.2f", productStock.getProduct().getPrice());
			ret += "\nQUANTITY REQUIRED: " + productStock.getQuantity();
			// Calculate the cost
			if (productStock.getProduct().getPrice() > -1.0) {
				ret += "\nTOTAL ITEM COST: EUR "+ String.format("%.2f", (productStock.getProduct().getPrice()*productStock.getQuantity()));
			}
			else {
				// If product is not in stock list
				ret+= "\nSorry, shop does not stock " + productStock.getProduct().getName();
			}
			ret += "\n-------------\n";
		}
		return ret;
	}
}