package Shop;

public class Product {

	private String name;
	private double price;
	
	// The constructor
	public Product(String name, double price) {
		// Although not a child this is implicit and need not be entered here at all
		super(); 
		this.name = name;
		this.price = price;
	}
	
	// Getters to make these value available to others
	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}
	
	// Setter - used to set price in customer shopping list only
	public void setPrice(double price) {
		this.price = price;
	}

	// Over-riding the print out product in specific way
	@Override
	public String toString() {
		return name + "\nPRODUCT PRICE: " + String.format("%.2f", price);
	}
}
