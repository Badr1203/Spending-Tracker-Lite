package com.example.spendingtrackerlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SpendingTracker.db";
    private static final String DATABASE_PATH = "/data/data/com.example.spendingtrackerlite/databases/";
    private static final int DATABASE_VERSION = 2;

    //Tables
    private static final String TABLE_PRODUCTS = "Products", TABLE_TRANSACTIONS = "Transactions", TABLE_STORES = "Stores";

    // --- Columns for 'Products' Table ---
    private static final String COL_P_ID = "ID", COL_P_BARCODE = "Barcode", COL_P_VARIANT = "Variant", COL_P_CATEGORY = "Category",
            COL_P_TYPE = "Type", COL_P_BRAND = "Brand", COL_P_TITLE = "Title", COL_P_UNIT = "Unit", COL_P_QUANTITY = "Quantity",
            COL_P_PERCENTAGE = "Percentage", COL_P_MANUFACTURER = "Manufacturer", COL_P_COUNTRY = "Country";
    // --- Columns for 'Stores' Table ---
    public static final String COL_S_SCODE = "SCODE", COL_S_NAME = "Name", COL_S_LONGITUDE = "Longitude", COL_S_LATITUDE = "Latitude";
    // --- Columns for 'Transactions' Table ---
    public static final String COL_T_PRICE = "Price", COL_T_DISCOUNTED_PRICE = "Discounted_price",
    COL_T_DATE = "Date", COL_T_TIME = "Time", COL_T_LINK = "Link";
    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        // Enable foreign key constraint enforcement (recommended for SQLite)
        db.execSQL("PRAGMA foreign_keys = ON;");

        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COL_P_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_P_BARCODE + " CHAR(13) NOT NULL, " +
                COL_P_VARIANT + " INTEGER NOT NULL DEFAULT 1, " +
                COL_P_CATEGORY + " TEXT NOT NULL, " +
                COL_P_TYPE + " TEXT NOT NULL, " +
                COL_P_BRAND + " TEXT NOT NULL, " +
                COL_P_TITLE + " TEXT DEFAULT NULL, " +
                COL_P_UNIT + " TEXT NOT NULL, " +
                COL_P_QUANTITY + " REAL NOT NULL, " +
                COL_P_PERCENTAGE + " REAL DEFAULT NULL, " +
                COL_P_MANUFACTURER + " TEXT DEFAULT NULL, " +
                COL_P_COUNTRY + " TEXT DEFAULT NULL, " +
                "UNIQUE(" + COL_P_BARCODE + ", " + COL_P_VARIANT + ")" +
                ");";
        db.execSQL(createProductsTable);

        String createStoresTable = "CREATE TABLE " + TABLE_STORES + " (" +
                COL_S_SCODE + " TEXT PRIMARY KEY NOT NULL, " + // Using COL_SCODE from Stores
                COL_S_NAME + " TEXT DEFAULT NULL, " +
                COL_S_LONGITUDE + " REAL NOT NULL, " +
                COL_S_LATITUDE + " REAL NOT NULL" +
                ");";
        db.execSQL(createStoresTable);

        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COL_S_SCODE + " TEXT NOT NULL, " +
                COL_P_BARCODE + " CHAR(13) NOT NULL, " +
                COL_P_VARIANT + " INTEGER NOT NULL DEFAULT 1, " +
                COL_T_PRICE + " REAL NOT NULL, " + // Using REAL for DECIMAL types in SQLite
                COL_T_DISCOUNTED_PRICE + " REAL DEFAULT NULL, " + // Using REAL
                COL_T_DATE + " TEXT NOT NULL, " +
                COL_T_TIME + " TEXT NOT NULL, " +
                COL_T_LINK + " TEXT DEFAULT NULL, " +
                "PRIMARY KEY (" + COL_S_SCODE + ", " + COL_P_BARCODE + ", " + COL_P_VARIANT + ", " + COL_T_DATE + ", " + COL_T_TIME + ")," +
                "FOREIGN KEY (" + COL_S_SCODE + ") REFERENCES " + TABLE_STORES + "(" + COL_S_SCODE + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY (" + COL_P_BARCODE + ", " + COL_P_VARIANT + ") REFERENCES " + TABLE_PRODUCTS + "(" + COL_P_BARCODE + ", " + COL_P_VARIANT + ") ON UPDATE CASCADE ON DELETE CASCADE" +
                ");";
        db.execSQL(createTransactionsTable);
    }

    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        // This is a simple example. For production apps, you'd use a more robust migration strategy.
        // See: https://developer.android.com/training/data-storage/sqlite/migrate
        Log.w("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("PRAGMA foreign_keys = OFF;"); // Disable FKs for smoother alteration/dropping if needed

        if (oldVersion < 2) {
            // Drop older tables if they exist (simple approach, data will be lost)
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORES);
            // Recreate tables with the new schema
            onCreate(db); // This will re-enable FKs because onCreate calls PRAGMA foreign_keys = ON;
        }
    }

    public void insertProduct(String barcode, int variant, String category, String type, String brand, String title, String unit, Double quantity, Double percentage, String manufacturer, String country) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_P_BARCODE, barcode);
        cv.put(COL_P_VARIANT, variant);
        cv.put(COL_P_CATEGORY, category);
        cv.put(COL_P_TYPE, type);
        cv.put(COL_P_BRAND, brand);
        cv.put(COL_P_TITLE, title);
        cv.put(COL_P_UNIT, unit);
        cv.put(COL_P_QUANTITY, quantity);
        cv.put(COL_P_PERCENTAGE, percentage);
        cv.put(COL_P_MANUFACTURER, manufacturer);
        cv.put(COL_P_COUNTRY, country);

        if ( barcode.length() != 13) Toast.makeText(context, "Barcode should be 13 digits.", Toast.LENGTH_SHORT).show();
        else if ( variant < 0 || category.isEmpty() ||
                type.isEmpty() || brand.isEmpty() ||
                unit.isEmpty() || quantity.isNaN()) Toast.makeText(context, "Failed to Insert", Toast.LENGTH_SHORT).show();
        else {
            long result = db.insert(TABLE_PRODUCTS, null, cv);
            if (result == -1) {
                Toast.makeText(context, "Failed to Insert", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Product Inserted", Toast.LENGTH_SHORT).show();
            }}
    }

    public void insertTransaction(String scode, String barcode, int variant, Double price, Double discounted_price, String date, String time, String link) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_S_SCODE, scode);
        cv.put(COL_P_BARCODE, barcode);
        cv.put(COL_P_VARIANT, variant);
        cv.put(COL_T_PRICE, price);
        cv.put(COL_T_DISCOUNTED_PRICE, discounted_price);
        cv.put(COL_T_DATE, date);
        cv.put(COL_T_TIME, time);
        cv.put(COL_T_LINK, link);

        long result = -1;
        try {
            db.beginTransaction(); // Start a transaction for atomicity [1]
            result = db.insert(TABLE_TRANSACTIONS, null, cv);
            if (result != -1) {
                db.setTransactionSuccessful(); // Mark transaction as successful [1]
            }
        } catch (Exception e) {
            Log.e("DB_INSERT_TRANSACTION", "Error inserting transaction", e);
        } finally {
            db.endTransaction(); // End the transaction (commits if successful, rolls back otherwise) [1]
        }

        if (result == -1) {
            Toast.makeText(context, "Failed to Insert Transaction", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Transaction Inserted Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public void insertStore(String scode, String name, Double longitude, Double latitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_S_SCODE, scode);
        cv.put(COL_S_NAME, name);
        cv.put(COL_S_LONGITUDE, longitude);
        cv.put(COL_S_LATITUDE, latitude);

        // Validation
        if (scode == null || scode.isEmpty() ||
            longitude.isNaN() || latitude.isNaN()) {
            Toast.makeText(context, "Failed to Insert Store: Invalid data", Toast.LENGTH_SHORT).show();
            Log.e("DB_INSERT_TRANSACTION", "Invalid data provided: SCODE=" + scode +
                    ", SCODE=" + scode + ", Name=" + name + ", Longitude=" + longitude + ", Latitude=" + latitude);
            return; // Exit if data is invalid
        }

        long result = -1;
        try {
            db.beginTransaction();
            result = db.insert(TABLE_STORES, null, cv);
            if (result != -1) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            android.util.Log.e("DB_INSERT_STORE", "Error inserting store", e);
        } finally {
            db.endTransaction();
        }

        if (result == -1) {
            Toast.makeText(context, "Failed to Insert Store. SCODE might already exist or data is invalid.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Store Inserted Successfully", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean storeScodeExists(String scode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_S_SCODE + " FROM " + TABLE_STORES + " WHERE " + COL_S_SCODE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{scode});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public ArrayList<String> getAllProducts() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

        if (cursor.moveToFirst()) {
            do {
                //int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String barcode = cursor.getString(cursor.getColumnIndexOrThrow(COL_P_BARCODE));
                int variant = cursor.getInt(cursor.getColumnIndexOrThrow(COL_P_VARIANT));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_P_CATEGORY));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COL_P_TYPE));
                String brand = cursor.getString(cursor.getColumnIndexOrThrow(COL_P_BRAND));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_P_TITLE));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COL_P_UNIT));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_P_QUANTITY));
                double percent = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_P_PERCENTAGE));
                String manufacturer = cursor.getString(cursor.getColumnIndexOrThrow(COL_P_MANUFACTURER));
                String country = cursor.getString(cursor.getColumnIndexOrThrow(COL_P_COUNTRY));

                DecimalFormat df = new DecimalFormat("#.##");
                String info = barcode + " " + category + " " + type+ " " +
                        brand;
                if (!title.isEmpty()) info += " " + title;
                info += " " + df.format(quantity) + unit;
                if (percent > 0) info += " " + df.format(percent) + " " +
                        manufacturer + " " + country;
                list.add(info);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<String> getTransactionsForProduct(String productBarcode) {
        List<String> transactionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Ensure the barcode is not null or empty to prevent SQL issues
        if (TextUtils.isEmpty(productBarcode)) {
            Log.e("DB_GET_TRANSACTIONS", "Product barcode is null or empty.");
            return transactionList; // Return empty list
        }

        // Query to get transactions for a specific barcode, including variant information.
        // We also join with Products to potentially get the product title for better display.
        String query = "SELECT T." + COL_S_SCODE + ", " +
                "S." + COL_S_NAME + " AS StoreName, " +
                "T." + COL_P_VARIANT + ", " +         // Get the variant from Transactions
                "P." + COL_P_TITLE + " AS ProductTitle, " + // Get Product Title
                "T." + COL_T_PRICE + ", " +
                "T." + COL_T_DISCOUNTED_PRICE + ", " + // Get discounted price
                "T." + COL_T_DATE + ", " +
                "T." + COL_T_TIME + ", " +
                "T." + COL_T_LINK + " " +             // Get the link
                "FROM " + TABLE_TRANSACTIONS + " T " +
                "LEFT JOIN " + TABLE_STORES + " S ON T." + COL_S_SCODE + " = S." + COL_S_SCODE + " " +
                "LEFT JOIN " + TABLE_PRODUCTS + " P ON T." + COL_P_BARCODE + " = P." + COL_P_BARCODE +
                " AND T." + COL_P_VARIANT + " = P." + COL_P_VARIANT + " " + // Join on Barcode AND Variant
                "WHERE T." + COL_P_BARCODE + " = ? " + // Filter by the main product barcode
                "ORDER BY T." + COL_T_DATE + " DESC, T." + COL_T_TIME + " DESC";


        // Use a parameterized query to prevent SQL injection
        String selection = COL_S_SCODE + " = ?";
        String[] selectionArgs = { productBarcode };

        try {
            cursor = db.rawQuery(query, selectionArgs); // Pass barcode as selection argument

            if (cursor != null && cursor.moveToFirst()) {
                // Get column indices once, outside the loop, for efficiency
                int scodeColIdx = cursor.getColumnIndexOrThrow(COL_S_SCODE);
                int storeNameColIdx = cursor.getColumnIndexOrThrow("StoreName");
                int variantColIdx = cursor.getColumnIndexOrThrow(COL_P_VARIANT);
                int productTitleColIdx = cursor.getColumnIndexOrThrow("ProductTitle");
                int priceColIdx = cursor.getColumnIndexOrThrow(COL_T_PRICE);
                int discountedPriceColIdx = cursor.getColumnIndexOrThrow(COL_T_DISCOUNTED_PRICE);
                int dateColIdx = cursor.getColumnIndexOrThrow(COL_T_DATE);
                int timeColIdx = cursor.getColumnIndexOrThrow(COL_T_TIME);
                int linkColIdx = cursor.getColumnIndexOrThrow(COL_T_LINK);

                DecimalFormat df = new DecimalFormat("#0.00"); // Format to always show two decimal places

                do {
                    String scode = cursor.getString(scodeColIdx);
                    String storeName = cursor.getString(storeNameColIdx);
                    int variant = cursor.getInt(variantColIdx);
                    String productTitle = cursor.getString(productTitleColIdx); // Can be null if product no longer exists
                    double price = cursor.getDouble(priceColIdx);
                    Double discountedPrice = null;
                    if (!cursor.isNull(discountedPriceColIdx)) {
                        discountedPrice = cursor.getDouble(discountedPriceColIdx);
                    }
                    String date = cursor.getString(dateColIdx);
                    String time = cursor.getString(timeColIdx);
                    String link = cursor.getString(linkColIdx);

                    String displayStoreName = (storeName != null && !storeName.isEmpty()) ? storeName : scode;
                    String displayProductInfo = (productTitle != null && !productTitle.isEmpty()) ?
                            productTitle + " (Variant: " + variant + ")" :
                            "Barcode: " + productBarcode + " (Variant: " + variant + ")";


                    StringBuilder transactionDetail = new StringBuilder();
                    transactionDetail.append("Product: ").append(displayProductInfo);
                    transactionDetail.append("\nStore: ").append(displayStoreName);


                    // Handle price and discounted price display
                    transactionDetail.append("\nPrice: ").append(df.format(price));
                    if (discountedPrice != null && discountedPrice < price) { // Show discounted if valid and different
                        transactionDetail.append(" (Discounted: ").append(df.format(discountedPrice)).append(")");
                    }

                    transactionDetail.append("\nDate: ").append(date).append(" ").append(time);

                    if (link != null && !link.isEmpty()) {
                        transactionDetail.append("\nLink: ").append(link);
                    }

                    transactionList.add(transactionDetail.toString());
                } while (cursor.moveToNext());
            } else {
                Log.d("DB_GET_TRANSACTIONS", "No transactions found for barcode: " + productBarcode);
            }
        } catch (Exception e) {
            Log.e("DB_GET_TRANSACTIONS", "Error while trying to get transactions for product barcode: " + productBarcode, e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return transactionList;
    }

    // Inside DatabaseHelper.java

// ... (other methods)

    public List<String> getAllStores() {
        List<String> stores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT " + COL_S_SCODE + ", " + COL_S_NAME + ", " + COL_S_LONGITUDE + ", " + COL_S_LATITUDE +
                " FROM " + TABLE_STORES +
                " ORDER BY " + COL_S_NAME + " ASC"; // Order by name, for example

        try {
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String scode = cursor.getString(cursor.getColumnIndexOrThrow(COL_S_SCODE));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_S_NAME));
                    // GetDouble might return 0.0 if the column is NULL and not handled.
                    // Check for null before getting double if you want to display "N/A" or similar for NULL coordinates
                    double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_S_LONGITUDE));
                    double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_S_LATITUDE));

                    // Format the string for display. You can customize this.
                    StringBuilder storeDetails = new StringBuilder();
                    storeDetails.append("Name: ").append(name).append(" (").append(scode).append(")");

                    // Check if longitude and latitude columns are actually null before trying to use getDouble
                    // To do this robustly, you might need to check cursor.isNull(columnIndex)
                    int longitudeColIndex = cursor.getColumnIndex(COL_S_LONGITUDE);
                    int latitudeColIndex = cursor.getColumnIndex(COL_S_LATITUDE);

                    if (!cursor.isNull(longitudeColIndex) && !cursor.isNull(latitudeColIndex)) {
                        storeDetails.append("\nLocation: ").append(String.format(java.util.Locale.US, "%.4f, %.4f", latitude, longitude));
                    } else {
                        storeDetails.append("\nLocation: Not Available");
                    }

                    stores.add(storeDetails.toString());
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            android.util.Log.e("DB_GET_STORES", "Error while trying to get all stores", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // db.close(); // Don't close if helper manages it
        }
        return stores;
    }

    public boolean productVariantExists(String barcode, int variant) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_P_ID + " FROM " + TABLE_PRODUCTS +
                " WHERE " + COL_P_BARCODE + " = ? AND " + COL_P_VARIANT + " = ?";
        Cursor cursor = null;
        boolean exists = false;
        try {
            cursor = db.rawQuery(query, new String[]{barcode, String.valueOf(variant)});
            exists = cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("DB_CHECK_PRODUCT_VARIANT", "Error checking if Product Variant exists", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }



    public void createDatabase() {
        boolean dbExists = checkDatabase();
        if (!dbExists) {
            this.getReadableDatabase(); // creates empty db
            copyDatabase();
        }
    }

    private boolean checkDatabase() {
        File dbFile = new File(DATABASE_PATH + DATABASE_NAME);
        return dbFile.exists();
    }

    private void copyDatabase() {
        try {
            InputStream input = context.getAssets().open(DATABASE_NAME);
            OutputStream output = new FileOutputStream(DATABASE_PATH + DATABASE_NAME);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            Log.e("CopyDatabase", "Failed to copy DB", e);
        }
    }

    public boolean barcodeExists(String barcode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_P_BARCODE + " FROM " + TABLE_PRODUCTS + " WHERE " + COL_P_BARCODE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{barcode});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }
}
