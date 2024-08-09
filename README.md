# Pharmacy Management System Data Structure Implementation Report

## Overview
   This report provides an overview of the data structures implemented in the pharmacy management system, detailing the 
   design and functionality of each class, their attributes, and methods. The system includes classes for managing drugs, 
   suppliers, customers, purchases, and their interactions.

## Classes and Data Structures
### Customers
- Attributes:
  - buyerName: Name of the customer.
  - contactInfo: Contact information of the customer.
  - purchases: Number of purchases made by the customer.

- Methods:
  - Getters for each attribute.
    
### Drug
- Attributes:
  - drugCode: Unique code for the drug.
  - name: Name of the drug.
  - category: Category of the drug.
  - price: Price of the drug.
  - stockQuantity: Quantity of e drug in stock.
  - suppliers: List of suppliers for the drug.
  - purchaseHistory: Linked list of purchase history.

- Methods:
  - Getters for each attribute.
  - addSupplier(Supplier supplier): Adds a supplier to the list.

### LinkedData
- Attributes:
  - drugCode: Code of the drug.
  - drugName: Name of the drug.
  - supplierName: Name of the supplier.
  - contact: Contact information of the supplier.
  - location: Location of the supplier.

- Methods:
  - Getters for each attribute.

### Purchase
- Attributes:
  - drugCode: Code of the drug.
  - quantity: Quantity purchased.
  - totalAmount: Total amount of the purchase.
  - dateTime: Date and time of the purchase.
  - buyerName: Name of the buyer.
  - contactInfo: Contact information of the buyer.

- Methods:
  - Getters and setters for each attribute.

### Pharmacy
- Attributes:
  - drugs: Map of drug codes to Drug objects.
  - suppliers: List of LinkedData objects representing supplier data.
  - customers: List of Customers.
  - purchases: Map of purchase date and time to Purchase objects.
  - db: Instance of LoadFromDb for database operations.

- Methods:
  - Getters for each attribute, initializing from the database.
  - addDrug(Drug drug): Adds a drug to the map.
  - removeDrug(String drugCode): Removes a drug from the map.
  - addSupplier(LinkedData supplier): Adds a supplier to the list.
  - addCustomer(Customers customer): Adds a customer to the list.
  - addPurchase(Purchase purchase): Adds a purchase to the map


## Data Interaction and Processing

### Data Retrieval
- Drugs, suppliers, customers, and purchases are retrieved from the database using the LoadFromDb class and stored in their respective data structures.

### Adding and Removing Data
- Drugs: Added and removed from a HashMap using addDrug and removeDrug methods.
- Suppliers: Added to a list using addSupplier.
- Customers: Added to a list using addCustomer.
- Purchases: Added to a HashMap with date and time as the key using addPurchase.

### Purchase Processing
- Each purchase records the drug code, quantity, total amount, date and time, buyer name, and contact information. The purchase history for each drug is maintained as a linked list.

## Conclusion
The implementation of the data structures in the pharmacy management system is designed to efficiently manage and process various entities such as drugs, suppliers, customers, and purchases. 
The classes are well-structured, with clear attributes and methods that ensure data integrity and ease of access. The use of HashMaps, Lists, and LinkedLists ensures that data retrieval and 
manipulation operations are performed efficiently.
