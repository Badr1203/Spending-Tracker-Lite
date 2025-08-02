package com.example.spendingtrackerlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Products.db";
    private static final String DATABASE_PATH = "/data/data/com.example.spendingtrackerlite/databases/";
    private static final int DATABASE_VERSION = 1;

    //Tables
    private static final String TABLE_PRODUCTS = "Products", TABLE_TRANSACTIONS = "Transactions", TABLE_STORES = "Stores";

    //Columns
    private static final String COLUMN_ID = "Id", COLUMN_CATEGORY = "Category", COLUMN_TYPE = "Type",
            COLUMN_BRAND = "Brand", COLUMN_TITLE = "Title", COLUMN_UNIT = "Unit",COLUMN_QUANTITY = "Quantity",
            COLUMN_PERCENT = "Percent", COLUMN_BARCODE = "Barcode", COLUMN_MANUFACTURER = "manufacturer", COLUMN_COUNTRY = "country";
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

    // Inside your DatabaseHelper.java class

    public void insertTransaction(String scode, String barcode, double price, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_SCODE, scode);
        cv.put(COL_BARCODE, barcode); // Using COL_BARCODE as defined for Transactions table
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
            return; // Exit if data is invalid
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
    }

    public ArrayList<String> getAllProducts() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
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
                list.add(id + " " + category + " " + type + " " + brand + " " + title + " " + quantity + " " + unit + " " + percent + " " + barcode + " " + manufacturer+ " " + country);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
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
