package com.example.spendingtrackerlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

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
    private static final String TABLE_NAME = "Products";
    private static final String COLUMN_ID = "Id";
    private static final String COLUMN_CATEGORY = "Category";
    private static final String COLUMN_TYPE = "Type";
    private static final String COLUMN_BRAND = "Brand";
    private static final String COLUMN_TITLE = "Title";
    private static final String COLUMN_UNIT = "Unit";
    private static final String COLUMN_QUANTITY = "Quantity";
    private static final String COLUMN_PERCENT = "Percent";
    private static final String COLUMN_BARCODE = "Barcode";
    private static final String COLUMN_MANUFACTURER = "manufacturer";
    private static final String COLUMN_COUNTRY = "country";

    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

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
            long result = db.insert(TABLE_NAME, null, cv);
            if (result == -1) {
                Toast.makeText(context, "Failed to Insert", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Product Inserted", Toast.LENGTH_SHORT).show();
            }}
    }

    public ArrayList<String> getAllProducts() {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

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
            e.printStackTrace();
        }
    }
}
