package com.example.spendingtrackerlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SpendingTracker.db";
    private static final String DATABASE_PATH = "/data/data/com.example.spendingtrackerlite/databases/";
    private static final int DATABASE_VERSION = 1;

    //Tables
    private static final String TABLE_PRODUCTS = "Products", TABLE_TRANSACTIONS = "Transactions", TABLE_STORES = "Stores";

    //Columns
    private static final String COLUMN_ID = "Id", COLUMN_CATEGORY = "Category", COLUMN_TYPE = "Type",
            COLUMN_BRAND = "Brand", COLUMN_TITLE = "Title", COLUMN_UNIT = "Unit",COLUMN_QUANTITY = "Quantity",
            COLUMN_PERCENT = "Percentage", COLUMN_BARCODE = "Barcode", COLUMN_MANUFACTURER = "manufacturer", COLUMN_COUNTRY = "country";
    public static final String COL_BARCODE = "Barcode", COL_PRICE = "Price",
            COL_DATE = "Date", COL_TIME = "Time";
    public static final String COL_SCODE = "SCODE", COL_NAME = "Name", COL_LONGITUDE = "Longitude", COL_LATITUDE = "Latitude";

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
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_BRAND + " TEXT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_UNIT + " TEXT, " +
                COLUMN_QUANTITY + " REAL, " +
                COLUMN_PERCENT + " REAL, " +
                COLUMN_BARCODE + " TEXT UNIQUE NOT NULL, " + // Using COLUMN_BARCODE from Products
                COLUMN_MANUFACTURER + " TEXT, " +
                COLUMN_COUNTRY + " TEXT" +
                ");";
        db.execSQL(createProductsTable);

        String createStoresTable = "CREATE TABLE " + TABLE_STORES + " (" +
                COL_SCODE + " TEXT PRIMARY KEY NOT NULL, " + // Using COL_SCODE from Stores
                COL_NAME + " TEXT, " +
                COL_LONGITUDE + " REAL, " +
                COL_LATITUDE + " REAL" +
                ");";
        db.execSQL(createStoresTable);

        String createTransactionsTable = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
                COL_SCODE + " TEXT, " +         // Foreign key to Stores
                COL_BARCODE + " TEXT, " +       // Foreign key to Products (using COL_BARCODE for consistency)
                COL_PRICE + " REAL, " +
                COL_DATE + " TEXT, " +
                COL_TIME + " TEXT, " +
                "PRIMARY KEY (" + COL_SCODE + ", " + COL_BARCODE + ", " + COL_DATE + ", " + COL_TIME + ")," +
                "FOREIGN KEY (" + COL_SCODE + ") REFERENCES " + TABLE_STORES + "(" + COL_SCODE + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                "FOREIGN KEY (" + COL_BARCODE + ") REFERENCES " + TABLE_PRODUCTS + "(" + COLUMN_BARCODE + ") ON UPDATE CASCADE ON DELETE CASCADE" + // Ensure this refers to Products.Barcode
                ");";
        db.execSQL(createTransactionsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insertProduct(String category, String type, String brand, String title, String unit, Double quantity, Double percent, String barcode, String manufacturer, String country) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_CATEGORY, category);
        cv.put(COLUMN_TYPE, type);
        cv.put(COLUMN_BRAND, brand);
        cv.put(COLUMN_TITLE, title);
        cv.put(COLUMN_UNIT, unit);
        cv.put(COLUMN_QUANTITY, quantity);
        cv.put(COLUMN_PERCENT, percent);
        cv.put(COLUMN_BARCODE, barcode);
        cv.put(COLUMN_MANUFACTURER, manufacturer);
        cv.put(COLUMN_COUNTRY, country);

        if ( category.isEmpty() || type.isEmpty() ||
                brand.isEmpty() ||unit.isEmpty() ||
                quantity.isNaN()) Toast.makeText(context, "Failed to Insert", Toast.LENGTH_SHORT).show();
        else {
            long result = db.insert(TABLE_PRODUCTS, null, cv);
            if (result == -1) {
                Toast.makeText(context, "Failed to Insert", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Product Inserted", Toast.LENGTH_SHORT).show();
            }}
    }

    public void insertTransaction(String scode, String barcode, double price, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_SCODE, scode);
        cv.put(COL_BARCODE, barcode);
        cv.put(COL_PRICE, price);
        cv.put(COL_DATE, date);
        cv.put(COL_TIME, time);

        // Basic validation (you might want to add more specific validation)
        if (scode == null || scode.isEmpty() ||
                barcode == null || barcode.isEmpty() ||
                date == null || date.isEmpty() ||
                time == null || time.isEmpty() ||
                Double.isNaN(price) || price < 0) { // Check for valid price
            Toast.makeText(context, "Failed to Insert Transaction: Invalid data", Toast.LENGTH_SHORT).show();
            Log.e("DB_INSERT_TRANSACTION", "Invalid data provided: SCODE=" + scode +
                    ", Barcode=" + barcode + ", Price=" + price + ", Date=" + date + ", Time=" + time);
            db.close();
            return; // Exit if data is invalid
        }

        if (!storeScodeExists(scode)) {
            Toast.makeText(context, "Failed to Insert Transaction: Store with SCODE '" + scode + "' does not exist.", Toast.LENGTH_LONG).show();
            Log.e("DB_INSERT_TRANSACTION", "SCODE '" + scode + "' not found in " + TABLE_STORES);
            return;
        }

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

        db.close();
    }

    public void insertStore(String scode, String name, Double longitude, Double latitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_SCODE, scode);
        cv.put(COL_NAME, name);
        cv.put(COL_LONGITUDE, longitude);
        cv.put(COL_LATITUDE, latitude);

        // Validation
        if (scode == null || scode.isEmpty() ||
            longitude.isNaN() || latitude.isNaN()) {
            Toast.makeText(context, "Failed to Insert Store: Invalid data", Toast.LENGTH_SHORT).show();
            Log.e("DB_INSERT_TRANSACTION", "Invalid data provided: SCODE=" + scode +
                    ", SCODE=" + scode + ", Name=" + name + ", Longitude=" + longitude + ", Latitude=" + latitude);
            db.close();
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
        // db.close(); // Don't close if helper manages it
    }

    public boolean storeScodeExists(String scode) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_SCODE + " FROM " + TABLE_STORES + " WHERE " + COL_SCODE + " = ?";
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
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE));
                String brand = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BRAND));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String unit = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_UNIT));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                double percent = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PERCENT));
                String barcode = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BARCODE));
                String manufacturer = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MANUFACTURER));
                String country = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COUNTRY));

                DecimalFormat df = new DecimalFormat("#.##");
                String info = barcode + " " + category + " " + type+ " " +
                        brand + " " + title + " " +
                        df.format(quantity) + " " + unit + " " +
                        df.format(percent)  + " " +
                        manufacturer + " " + country;
                list.add(info);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<String> getTransactionsForProduct(String productBarcode) {
        List<String> transactionList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Ensure the barcode is not null or empty to prevent SQL issues
        if (productBarcode == null || productBarcode.isEmpty()) {
            Log.e("DB_GET_TRANSACTIONS", "Product barcode is null or empty.");
            return transactionList; // Return empty list
        }

        String query = "SELECT T." + COL_SCODE + ", S." + COL_NAME + " AS StoreName, T." + COL_PRICE + ", T." + COL_DATE + ", T." + COL_TIME +
                " FROM " + TABLE_TRANSACTIONS + " T" +
                " LEFT JOIN " + TABLE_STORES + " S ON T." + COL_SCODE + " = S." + COL_SCODE +
                " WHERE T." + COL_BARCODE + " = ?" +
                " ORDER BY T." + COL_DATE + " DESC, T." + COL_TIME + " DESC";

        // Use a parameterized query to prevent SQL injection
        String selection = COL_BARCODE + " = ?";
        String[] selectionArgs = { productBarcode };

        try {
            cursor = db.rawQuery(query, selectionArgs); // Pass barcode as selection argument

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Adjust how you retrieve and format the transaction details
                    String scode = cursor.getString(cursor.getColumnIndexOrThrow(COL_SCODE));
                    // Get the StoreName. It might be null if the LEFT JOIN didn't find a match.
                    String storeName = cursor.getString(cursor.getColumnIndexOrThrow("StoreName"));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRICE));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE));
                    String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIME));

                    String displayStoreName = (storeName != null && !storeName.isEmpty()) ? storeName : scode; // Fallback to SCODE if name is null/empty

                    String priceStr;
                    if (price == (long) price) {
                        priceStr = String.format(java.util.Locale.US, "%d", (long) price);
                    } else {
                        priceStr = String.format(java.util.Locale.US, "%.2f", price);
                    }

                    String transactionDetail = "Store: " + displayStoreName +
                            "\nPrice: " + priceStr +
                            "\nDate: " + date + " " + time;
                    transactionList.add(transactionDetail);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("DB_GET_TRANSACTIONS", "Error while trying to get transactions for product", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close(); // Don't close db here if helper manages it
        }
        return transactionList;
    }

    // Inside DatabaseHelper.java

// ... (other methods)

    public List<String> getAllStores() {
        List<String> stores = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT " + COL_SCODE + ", " + COL_NAME + ", " + COL_LONGITUDE + ", " + COL_LATITUDE +
                " FROM " + TABLE_STORES +
                " ORDER BY " + COL_NAME + " ASC"; // Order by name, for example

        try {
            cursor = db.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String scode = cursor.getString(cursor.getColumnIndexOrThrow(COL_SCODE));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
                    // GetDouble might return 0.0 if the column is NULL and not handled.
                    // Check for null before getting double if you want to display "N/A" or similar for NULL coordinates
                    double longitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LONGITUDE));
                    double latitude = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LATITUDE));

                    // Format the string for display. You can customize this.
                    StringBuilder storeDetails = new StringBuilder();
                    storeDetails.append("Name: ").append(name).append(" (").append(scode).append(")");

                    // Check if longitude and latitude columns are actually null before trying to use getDouble
                    // To do this robustly, you might need to check cursor.isNull(columnIndex)
                    int longitudeColIndex = cursor.getColumnIndex(COL_LONGITUDE);
                    int latitudeColIndex = cursor.getColumnIndex(COL_LATITUDE);

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
}
