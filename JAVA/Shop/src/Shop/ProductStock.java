package Shop;

public class ProductStock {
	private Product product;
	private int quantity;
	
	// The constructor
	public ProductStock(Product product, int quantity) {
		super();
		this.product = product;
		this.quantity = quantity;
	}
	
	// Getters
	public Product getProduct() {
		return product;
	}
	public int getQuantity() {
		return quantity;
	}
	
	// Over-riding the print out product in specific way
	@Override
	public String toString() {
		return "PRODUCT NAME: " + product + "\nQUANTITY IN STOCK: " + quantity + "\n ---------\n";
	}

	// Needed to update quantities in shopping list and in shop stock on purchase
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}