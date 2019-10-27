// Project creating a shop in C
// Multi Paradigm Computing Module in Hiher Diploma in Data Analytics 2019
// Student Name: Yvonne Brady
// Student ID: G00376355


// Need to call these libraries in c
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <stdbool.h>

// Create a container for the product information
struct Product {
	char* name;
	double price;
};

// Create a container for the stock information
struct ProductStock {
	struct Product product;
	int quantity;
};

// Create a container for the shop status
struct Shop {
	double cash;
	struct ProductStock stock[20];
	int index;
};

// Create a container for the customer information
struct Customer {
	char* name;
	double budget;
	struct ProductStock shoppingList[10];
	int index;
};

// Global Variables
// Easier to make them gloabal than passing them around everywhere
struct Shop shop;
struct Customer customer;

// Print info on product
void printProduct(struct Product p)
{
	printf("PRODUCT NAME: %s \nPRODUCT PRICE: EUR %.2f\n", p.name, p.price);
}


// Reads a csv file line by line to create stock list
struct Shop createAndStockShop()
{
    FILE * fp; // file
    char * line = NULL; // Each line of csv
    size_t len = 0;
    size_t read;

    fp = fopen("stock.csv", "r"); // Open the file as read only
    if (fp == NULL) // Error handling to exit if there is no file or cannot read
        exit(EXIT_FAILURE);

	// Reading in the initial shop float
	getline(&line, &len, fp);
	double cashInShop = atof(line); // convert it to a float
	shop.cash = cashInShop; // save it to the shop

	//This bit does the reading - line by line
    while ((read = getline(&line, &len, fp)) != -1) {
		//Reads the first item
		char *n = strtok(line, ","); // name
		// Null in this context means do not go on to the next item
		char *p = strtok(NULL, ","); // product
		char *q = strtok(NULL, ","); // quantity
		int quantity = atoi(q); // atoi converts the string into an integer
		double price = atof(p); // atof converts the string into a float
		char *name = malloc(sizeof(char) * 50); // manually allocating memory for this
		strcpy(name, n); // copy the properly foramtted / allocated string to the product name
		struct Product product = { name, price }; // write in the product
		struct ProductStock stockItem = { product, quantity }; // Add the quantities of each
		shop.stock[shop.index++] = stockItem; // index++ means that it starts as 0 and increases with each addition
		// This is a way to know the length of the array for looping around later. 
    }
	// returns the completed shop	
	return shop;
}

// Prints a stck sheet from the shop
void printShop()
{
	// Header info
	printf("\n***SHOP CONTENTS***\n\n");
	// How much cash is in the shop
	printf("FLOAT: EUR %.2f \n\n", shop.cash);
	// Loop through each stock item in the shop
	for (int i = 0; i < shop.index; i++)
	{
		// Call the printProduct function that prints the name and price
		printProduct(shop.stock[i].product);
		// Then print out the quantity in stock
		printf("QUANTITY IN STOCK: %d\n", shop.stock[i].quantity);
		printf("-------------\n"); // And a divider to make it pretty!
	}
}

// This is to check to see if we have enough stock to fulfill an order
// And updates quantities if we do.
bool checkProductStock(char *n, int order)
{
	// Loops through each stock item
	for (int i =0; i < shop.index; i++)
	{
		struct ProductStock product = shop.stock[i];
		// Gets the name from each item in stock
		char *name = product.product.name;
		// Compares it to the passed parameter product name
		if (strcmp(name, n) == 0) 
		{
			// Then checks if there is sufficient to fill the order
			if (shop.stock[i].quantity >= order){
				// Updates the shop stock to reflect the purchase
				shop.stock[i].quantity = shop.stock[i].quantity - order;
				// Lets the calling function know we have enough stock for the order
				return true;
			}
			else{
				// Otherwise lets the calling function know there is not enough stock for the order
				return false;
			}
		}
	}
	// Error - if the stock item was not found.
	return -1;
}

// This function finds the price of a product passed to it
double findProductPrice(char *n)
{
	// Loops through each of the shop stock items
	for (int i =0; i < shop.index; i++)
	{
		struct Product product = shop.stock[i].product;
		// Checks the product name against the name passed to the function
		char *name = product.name;
		// If there is a match
		if (strcmp(name, n) == 0) 
		{
			// return the price of that product to the calling function
			return product.price;
		}
	}
	// Otherwise return an erro value
	return -1;
}

// Reads a csv file line by line to create stock list
struct Customer readCustomer()
{
    FILE * fp; // file
    char * line = NULL; // Each line of csv
    size_t len = 0;
    size_t read;

    fp = fopen("customer.csv", "r"); // Open the file as read only
    if (fp == NULL) // Error handling to exit if there is no file or cannot read
        exit(EXIT_FAILURE);

	// Reading in the customer Name and Budget
	getline(&line, &len, fp);
	char *n = strtok(line, ","); // Customer Name
	char *c = strtok(NULL, ","); // Customer Budget
	double customerCash = atof(c); // Converting
	char *custName = malloc(sizeof(char) * 50); // Converting
	strcpy(custName, n); // Copying Name over
	// Creating the customer
	customer.name = custName;
	customer.budget = customerCash;
	struct ProductStock shopList;

	//This bit does the reading - line by line
    while ((read = getline(&line, &len, fp)) != -1) {
		//Reads the first item on a new item
		char *n = strtok(line, ","); // product name
		// Null in this context means don't go on to the next item
		char *q = strtok(NULL, ","); // quantity required
		int quantity = atoi(q); // atoi converts the string into an integer
		char *name = malloc(sizeof(char) * 50); // manually allocating memory for this
		strcpy(name, n); // again copying
		shopList.product.name = name; // assigning the variables - name
		shopList.product.price = findProductPrice(name); // assigning the variables - price
		shopList.quantity = quantity; // assigning the variables - quantity
		customer.shoppingList[customer.index++] = shopList; // index++ means that it starts as 0 and increases with each addition
		// This is a way to know the length of the array for looping around later. 
    }
	// And the customer is created	
	return customer;
}

// This is the livemode - it probably could be and possibly should be broken out into  smaller elements 0 but it works as is
void liveMode()
{
	// Declaring float variables for the customer budget and the total cost of the purchase
	double mybudget, totalCost;
	int quantity, select; // Integers for menu selection and product quantities
	// Need to ascertain the buget first
	printf("What is your budget in EURO (to two decimal places)?\n");
	scanf("%lf", &mybudget);
		// Keep bringin up the menu until told not to (99)
		do{
			// Banner
			printf("-----------------------------------\n   Welcome to the C Shop - LIVE MODE \n-----------------------------------\n");
			printf("\nHow can we help?\nYour current budget is %.2f.\nPlease select what you would like to buy from the list below:\n\n", mybudget);
			// Loop through every product and put them in a menu to see which is wanted to buy
			// Doing it this way reduces typing errors in product selection
			for(int i = 0; i < shop.index; i++)
				{
					// Menu selection per product
					printf("%d - %s @ %.2f each.\n", i+ 1, shop.stock[i].product.name, findProductPrice(shop.stock[i].product.name));
				}
				// And of course the menu out 
				printf("99 - Exit Live Mode\n");
				printf("\nPlease make your selection: ");
				// Get the selection
				scanf("%d", &select);
				// Do different things according to selection
				switch(select){
					// If you want out - bring out to earlier menu
					case 99:{
						printf("Thank you for choosing Live Mode. Please come again soon.\n");
					}	
					// Everything else - do this
					// This is the downside of looping through the prouct list - we don't knwo for sure what values are legitimate.
					default: {
						// Check to make sure it is within our limits
						if (select > shop.index+1) {
							printf ("Please enter a number from list above.\n");
							break;
						}
						// Assuming all ok - check the quantity required
						printf ("How many %ss would you like to purchase? ", shop.stock[select-1].product.name);
						scanf("%d", &quantity); 
						// Calculate the total cost
						totalCost = quantity * findProductPrice(shop.stock[select-1].product.name);
						// Check can they afford it
						if(mybudget < totalCost){
							// If not let them know
							printf("Insufficient funds for this transaction. No purchase made.\n");
						} else{
							// Check do we have enough stock. Here orders are either filled or not - not partial order of product
							if (checkProductStock(shop.stock[select-1].product.name, quantity)){
								// If all ok update the customer budget
								mybudget = mybudget - totalCost;
								// and the cash in the shop
								shop.cash = shop.cash + totalCost;
								// Let the customer know they were successful
								printf("%d %ss purchased at a total cost of EUR %.2f.\nNew budget is %.2f\n", quantity, shop.stock[select-1].product.name, totalCost, mybudget);
							}
							else{
								// Otherwise let them know there is not enough of the product
								printf("Insufficient stock to complete transaction. No purchase made.\n");
							}
						}
						// And the divider for clarity
						printf("---------------------------------------\n");
					}}}
				// Exit on 99 selection on menu
				while (select != 99);
}


// This prints out the shopping list and / or does the shopping via csv
// The boolean parameter passed in to the function represents whether this is just a read of the customer 
// or a read and update in the case of purcahsing via CSV
void printCustomer(bool upd)
{
	// Header info
	printf("\n***%s's SHOPPING LIST***\n", customer.name);
	// Setting the budget out
	printf("\nCUSTOMER NAME: %s \nCUSTOMER BUDGET: %.2f\n", customer.name, customer.budget);
	printf("-------------\n");
	// Loops through each of the items on the shopping list
	for(int i = 0; i < customer.index; i++)
		{
		// Calls the printProduct function which prints the product infornmtion
		printProduct(customer.shoppingList[i].product);
		// States how many are requested
		printf("QUANTITY REQUIRED: %d\n", customer.shoppingList[i].quantity);
		// Calculates the cost if the order was fulfilled
		double cost = customer.shoppingList[i].quantity * customer.shoppingList[i].product.price; 
		// Relays the information to the user
		printf("TOTAL ITEM COST: EUR %.2f\n", cost);

		// If this is a purchase as opposed to a read
		if (upd){
			// Check to see does the customer have enough funds
			if(customer.budget >= cost){
				// If they do - check to see if there is enough stock
				if (checkProductStock(customer.shoppingList[i].product.name, customer.shoppingList[i].quantity)){
					// If all is good update the customer budget to reflect the money thay have spent
					customer.budget = customer.budget - cost;
					// Give that money to the shop
					shop.cash = shop.cash + cost;
					// Take the quantity away from the shopplng list
					customer.shoppingList[i].quantity = 0;
					// Relay this info back to the customer / shop
					printf("QUANTITY PURCHASED: %d\n", customer.shoppingList[i].quantity);
					printf("ADJUSTED BUDGET: EUR %.2f\n", customer.budget);
					printf("ADJUSTED SHOP FLOAT: EUR %.2f\n", shop.cash);
					}
				else{
					// Let them know if there is not enough stock
					printf("Insufficient stock to fulfill order. No purchase made.\n");
				}}
			else{
				// Or if they do not have enough money
				printf("Insufficient funds to fulfill order. No purchase made.\n");
			}
		}
			// And the divider again
			printf("-------------\n");
	}
}

// This is the amin program
int main(void) 
{
	// Create the shop
	shop = createAndStockShop();
	// And the customer
	customer = readCustomer();
	// This variable is to record the user menu selection
	int menuSelect;
	// Keep showing the menu until the user wants out (99)
	do{
		// Header info
		printf("-------------------------------\n      Welcome to the C Shop\n-------------------------------\n");
		printf("\nHow can we help?\nPlease Select from the choices available below:\n\n");
		// Menu selection items
		printf("1 - Show current Shop contents\n"); // shows the current state of the shop
		printf("2 - Show current Shopping List\n"); // shows the current state of the customer shopping list
		printf("3 - Shop via CSV\n"); // Do a shop off the CSV
		printf("4 - Shop via Live Mode\n"); // Do a shop via live mode
		printf("5 - Reset Shop Quantities & Cash\n"); // Reset the shop to the original settings - this reads the csv again. The csv is not altered during the program.
		printf("6 - Reset Customer Quantities & Cash\n"); // Reset the customer similar to above
		printf("99 - Exit the Shop\n"); // Out
		printf("\nPlease make your selection:\n");
		scanf("%d", &menuSelect);
		switch (menuSelect){
			case 1:{
				// Show current shop contents - calls printShop above
				printShop();
				break;
			}
			case 2:{
				// Show shopping list- calls print Customer with the update bool set to false
				printCustomer(false);
				break;
			}
			case 3:{
				// Shop via CSV - same function as above but with update bool set to true
				printCustomer(true);
				break;
			}
			case 4:{
				// Live Mode
				printf("\nYou are now entering LIVE Mode!\n");
				liveMode();
				break;
			}
			case 5:{
				// Reset the shop stock and cash
				shop = createAndStockShop();
				printf("\n*** Shop now reset. ***\n");
				break;
			}
			case 6:{
				// Reset the customer shopping list and cash
				customer = readCustomer();
				printf("\n*** Customer now reset. ***!\n");
				break;
			}
			case 99:{
				// Personal exit message
				printf("\nGoodbye %s and thank you for your custom!\n", customer.name);
				break;
			}
			default:{
				// Incorrect entry message
				printf("\nIncorrect Selection - please try again\n");
				break;
			}
		}
	}
	// Continue until 99 is entered
	while (menuSelect != 99);
	// The end
    return 0;
}