package com.example.spendingtrackerlite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spendingtrackerlite.DatabaseHelper;
import com.example.spendingtrackerlite.R;

public class AddProductFragment extends Fragment {
    public AddProductFragment() {}

    EditText editTextCategory, editTextType, editTextBrand, editTextTitle,
            editTextUnit, editTextQuantity, editTextPercent, editTextBarcode,
            editTextManufacturer, editTextCountry;
    Button buttonInsert, buttonViewProducts;
    DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        // Bind views using view.findViewById
        editTextCategory = view.findViewById(R.id.editTextCategory);
        editTextType = view.findViewById(R.id.editTextType);
        editTextBrand = view.findViewById(R.id.editTextBrand);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextUnit = view.findViewById(R.id.editTextUnit);
        editTextQuantity = view.findViewById(R.id.editTextQuantity);
        editTextPercent = view.findViewById(R.id.editTextPercent);
        editTextBarcode = view.findViewById(R.id.editTextBarcode);
        editTextManufacturer = view.findViewById(R.id.editTextManufacturer);
        editTextCountry = view.findViewById(R.id.editTextCountry);

        buttonInsert = view.findViewById(R.id.buttonInsert);

        // Use getActivity() instead of 'this' in fragments
        dbHelper = new DatabaseHelper(getActivity());
        dbHelper.createDatabase();

        buttonInsert.setOnClickListener(v -> {
            String category = editTextCategory.getText().toString().trim();
            String type = editTextType.getText().toString().trim();
            String brand = editTextBrand.getText().toString().trim();
            String title = editTextTitle.getText().toString().trim();
            String unit = editTextUnit.getText().toString().trim();
            String quantityInput = editTextQuantity.getText().toString().trim();
            Double quantity = quantityInput.isEmpty() ? null : Double.parseDouble(quantityInput);
            String percentInput = editTextPercent.getText().toString().trim();
            Double percent = percentInput.isEmpty() ? null : Double.parseDouble(percentInput);
            String barcode = editTextBarcode.getText().toString().trim();
            String manufacturer = editTextManufacturer.getText().toString().trim();
            String country = editTextCountry.getText().toString().trim();

            dbHelper.insertProduct(category, type, brand, title, unit, quantity, percent, barcode, manufacturer, country);

            Toast.makeText(getActivity(), "Product Inserted", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
