package com.example.spendingtrackerlite;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText editTextCategory, editTextType, editTextBrand, editTextTitle,
            editTextUnit, editTextQuantity, editTextPercent, editTextBarcode,
            editTextManufacturer, editTextCountry;
    Button buttonInsert, buttonViewProducts;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editTextCategory = findViewById(R.id.editTextCategory);
        editTextType = findViewById(R.id.editTextType);
        editTextBrand = findViewById(R.id.editTextBrand);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextUnit = findViewById(R.id.editTextUnit);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        editTextPercent = findViewById(R.id.editTextPercent);
        editTextBarcode = findViewById(R.id.editTextBarcode);
        editTextManufacturer = findViewById(R.id.editTextManufacturer);
        editTextCountry = findViewById(R.id.editTextCountry);

        buttonInsert = findViewById(R.id.buttonInsert);
        buttonViewProducts = findViewById(R.id.buttonViewProducts);

        dbHelper = new DatabaseHelper(this);
        dbHelper.createDatabase();

        buttonInsert.setOnClickListener(view -> {
            String category = editTextCategory.getText().toString().trim();
            String type = editTextType.getText().toString().trim();
            String brand = editTextBrand.getText().toString().trim();
            String title = editTextTitle.getText().toString().trim();
            String unit = editTextUnit.getText().toString().trim();
            String quantityInput = editTextQuantity.getText().toString().trim();
            Double quantity = null;
            if (!quantityInput.isEmpty()) quantity = Double.parseDouble(quantityInput);
            String percentInput = editTextPercent.getText().toString().trim();
            Double percent = null;
            if (!percentInput.isEmpty()) percent = Double.parseDouble(percentInput);
            String barcode = editTextBarcode.getText().toString().trim();
            String manufacturer = editTextManufacturer.getText().toString().trim();
            String country = editTextCountry.getText().toString().trim();
            dbHelper.insertProduct(category, type, brand, title, unit, quantity, percent, barcode, manufacturer, country);

        });

        buttonViewProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
                startActivity(intent);
            }
        });
    }
}