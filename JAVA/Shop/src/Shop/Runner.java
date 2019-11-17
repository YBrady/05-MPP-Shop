package Shop;

//Import all the packages needed 
import java.util.Scanner; // for user inputs


public class Runner {
	// The main method - how the shop works
	public static void main(String[] args) {
		
		// Setting up null product and product stock
		// Use this to create customer in non-live mode
		Product nullProd = new Product("", 0);
		ProductStock nullPS = new ProductStock(nullProd, 0);
		
		// Create the shop and customer
		Shop shop = new Shop("src/Shop/stock.csv");
		Customer customer = new Customer("src/Shop/customer.csv", 0.0, nullPS);
				
		// Scanner looks after user input
		Scanner scanchoice = new Scanner(System.in);
		// Recording the choice made
		int choiceentry;
  
	    do {
			// Header info
			System.out.println("-------------------------------\n      Welcome to the JAVA Shop\n-------------------------------");
			System.out.println("How can we help?\nPlease Select from the choices available below:\n");
			// Menu selection items
			System.out.println("1 - Show current Shop contents"); // shows the current state of the shop
			System.out.println("2 - Show current Shopping List"); // shows the current state of the customer shopping list
			System.out.println("3 - Shop via Shopping List"); // Do a shop off the CSV
			System.out.println("4 - Shop via Live Mode"); // Do a shop via live mode
			System.out.println("5 - Reset Shop Stock & Float"); // Reset the shop to the original settings - this reads the csv again. The csv is not altered during the program.
			System.out.println("6 - Reset Customer Shopping List & Budget"); // Reset the customer similar to above
			System.out.println("99 - Exit the Shop"); // Out
			System.out.println("\nPlease make your selection:");
	    	
			// Records the entry made by the user
			choiceentry = scanchoice.nextInt();
			
			// Depending on the user choice - what you do
	        switch (choiceentry)
	        {
	            case 1: // Show current shop contents
	            	System.out.println(shop);
	                break;
	            case 2: // Show current Shopping List
	        		// Apply the prices to the customer shoppinglist
	            	// Need to do this everytime you interact with the customer via list mode in case prices changed
	        		shop.applyPrices(customer);
	        		// Show the shopping list with prices
	    	        System.out.println(customer);
	                break;
	            case 3: // Shop via Shopping List
	        		// Apply the prices to the customer shoppinglist
	            	// Need to do this everytime you interact with the customer via list mode in case prices changed
	        		shop.applyPrices(customer);
	        		// This part does the shopping / reconciliations
	    	        shop.processOrder(customer);
	                break;
	            case 4: // Shop via Live Mode
	    	        shop.liveMode();
	                break;
	            case 5: // Reset Shop Quantities & Cash
	            	// Reads in the shop data from csv again - to restock or test other situations
            		shop =  new Shop("src/Shop/stock.csv");
            		System.out.println("*** Shop now reset.***");
	                break;
                case 6: // Reset Customer Quantities & Cash
	            	// Reads in the customer data from csv again - to replenish money or test other situations
            		customer =  new Customer("src/Shop/customer.csv", 0.0, nullPS);
            		shop.applyPrices(customer);
            		System.out.println("*** Customer now reset.***");
	                break;
                case 99: // Exit the shop
                	System.out.println("\nGoodbye "+ customer.getName() + " and thank you for your custom!");
	                break;
	            default:
	            	// Error message
	                System.out.println("Choice must be a value between 1 and 3.");
	        }   
	    } while (choiceentry != 99);
	    // Close the scan
	    scanchoice.close();
	}
}
