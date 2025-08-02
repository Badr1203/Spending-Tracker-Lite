# Spending Tracker Lite (Android)

Spending Tracker Lite is a simple Android application designed to help users track their spending by logging products and the transactions associated with them. Users can add product details, record purchases with price, date, and store information, and then view a list of their products and the transaction history for each product.

## Features

*   **Product Management:**
    *   Add new products with details such as category, type, brand, title, unit, quantity, percentage (e.g., alcohol content), barcode, manufacturer, and country of origin.
    *   View a list of all added products.
*   **Transaction Tracking:**
    *   Record transactions for products, including the store code (or name), price, date, and time of purchase.
    *   View a list of all transactions associated with a specific product, showing store details, price, and date/time.
*   **Store Information:**
    *   (Assumed) Stores can be added with a unique code, name, and location (longitude, latitude).
*   **Local Data Storage:**
    *   All data is stored locally on the device using an SQLite database.

## Database Schema

The application uses an SQLite database with the following main tables:

1.  **`Products` Table:**
    *   `Id` (INTEGER, Primary Key, Autoincrement)
    *   `Category` (TEXT)
    *   `Type` (TEXT)
    *   `Brand` (TEXT)
    *   `Title` (TEXT)
    *   `Unit` (TEXT)
    *   `Quantity` (REAL)
    *   `Percentage` (REAL)
    *   `Barcode` (TEXT, Unique, Not Null) - Links to Transactions
    *   `Manufacturer` (TEXT)
    *   `Country` (TEXT)

2.  **`Stores` Table:**
    *   `SCODE` (TEXT, Primary Key, Not Null) - Store Code, links to Transactions
    *   `Name` (TEXT) - Store's actual name
    *   `Longitude` (REAL)
    *   `Latitude` (REAL)

3.  **`Transactions` Table:**
    *   `SCODE` (TEXT) - Foreign Key to `Stores.SCODE`
    *   `Barcode` (TEXT) - Foreign Key to `Products.Barcode`
    *   `Price` (REAL)
    *   `Date` (TEXT) - Format: YYYY-MM-DD
    *   `Time` (TEXT) - Format: HH:MM
    *   Primary Key: (`SCODE`, `Barcode`, `Date`, `Time`)
    *   Foreign Key Constraints: `ON UPDATE CASCADE ON DELETE CASCADE` for both `SCODE` and `Barcode`.

## Getting Started

*(This section will likely need customization based on your actual project setup)*

### Prerequisites

*   Android Studio (latest stable version recommended)
*   Android SDK installed
*   An Android device or emulator (API Level X or higher - *Specify your minSdkVersion*)

### Building and Running

1.  **Clone the repository (if applicable):**
2.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select "Open an existing Android Studio project".
    *   Navigate to the project directory and select it.
3.  **Build the project:**
    *   Allow Android Studio to sync Gradle files and download any necessary dependencies.
    *   Click on "Build" > "Make Project" (or use the Gradle sync/build button).
4.  **Run the application:**
    *   Select an available Android device or emulator.
    *   Click on "Run" > "Run 'app'" (or the green play button).

## Project Structure (Simplified)

*(You can expand this with more details about your specific package structure)*

*   `com.example.spendingtrackerlite/`
    *   `DatabaseHelper.java`: Manages SQLite database creation, versioning, and CRUD operations.
    *   `fragments/`: (Assumed based on our discussion) Contains UI fragments for different screens like:
        *   `AddProductFragment.java` & `fragment_add_product.xml`
        *   `AddTransactionFragment.java` & `fragment_add_transaction.xml`
        *   `ViewProductsFragment.java` & `fragment_view_products.xml`
        *   `ProductTransactionsFragment.java` & `fragment_product_transactions.xml`
    *   `activities/`: (Assumed) Contains main activities like `MainActivity.java`.
    *   `models/`: (Recommended) Could contain data model classes (e.g., `Product.java`, `Transaction.java`, `Store.java`).
    *   `res/`: Standard Android resource directory.
        *   `layout/`: XML layout files for activities and fragments.
        *   `drawable/`: Image assets.
        *   `values/`: Strings, colors, styles.
    *   `assets/`:
        *   `SpendingTracker.db`: (Implied by `copyDatabase()`) Pre-populated database file that can be copied on first launch if it doesn't exist.

## Key Code Components

*   **`DatabaseHelper.java`:**
    *   `onCreate()`: Defines and creates the database schema (tables and their columns) when the database is first created.
    *   `onUpgrade()`: Handles database schema upgrades if the `DATABASE_VERSION` changes (currently empty).
    *   `insertProduct()`: Adds a new product to the `Products` table.
    *   `insertTransaction()`: Adds a new transaction to the `Transactions` table.
    *   `getAllProducts()`: Retrieves a list of all products (currently as formatted strings).
    *   `getTransactionsForProduct()`: Retrieves transactions for a specific product, joining with the `Stores` table to display store names.
    *   `createDatabase()`, `checkDatabase()`, `copyDatabase()`: Logic to copy a pre-existing database from assets if the app's database doesn't exist.

## Potential Future Enhancements

*   Implement editing and deletion functionality for products and transactions.
*   Add store management features (add, edit, delete stores).
*   Use `RecyclerView` instead of `ListView` for better performance and flexibility in displaying lists.
*   Implement a custom `Adapter` for `RecyclerView` / `ListView` to display `Product` or `Transaction` objects directly instead of formatted strings, improving code robustness and maintainability.
*   Add data validation with more user-friendly error messages in input forms.
*   Implement search and filtering capabilities for products and transactions.
*   Add data backup and restore functionality.
*   Improve UI/UX with Material Design components and better visual feedback.
*   Introduce data visualization (charts/graphs) for spending patterns.
*   User authentication (if data needs to be synced or private).

## Contributing

*(If this is an open-source project or you expect contributions, add guidelines here. Otherwise, you can omit this section or state that contributions are not currently sought.)*

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## License

*(Specify your project's license here, e.g., MIT, Apache 2.0, or state that it's proprietary.)*

Distributed under the [LICENSE_NAME] License. See `LICENSE.txt` for more information (if you have one).

---
