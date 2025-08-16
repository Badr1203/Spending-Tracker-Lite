package com.example.spendingtrackerlite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.spendingtrackerlite.DatabaseHelper;
import com.example.spendingtrackerlite.R;
import com.example.spendingtrackerlite.models.Product;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Locale;

public class AddProductFragment extends Fragment {
    public AddProductFragment() {}

    private static final String ARG_EDIT_PRODUCT_ID = "edit_product_id";
    private long editingProductId = -1; // -1 indicates "add mode", otherwise "edit mode"
    private Product existingProductToEdit;


    private TextInputLayout tilProductBarcode, tilProductVariant, tilProductCategory, tilProductType, tilProductBrand,
            tilProductTitle, tilProductUnit, tilProductQuantity, tilProductPercentage;

    private TextInputEditText etProductBarcode, etProductVariant,etProductCategory, etProductType, etProductBrand,
            etProductTitle, etProductUnit, etProductQuantity, etProductPercentage,
            etProductManufacturer, etProductCountry;
    private DatabaseHelper dbHelper;

    public static AddProductFragment newInstance() {
        return new AddProductFragment();
    }

    /**
     * Factory method for creating a new instance for editing an existing product.
     * @param productId The ID of the product to edit.
     * @return A new instance of fragment AddProductFragment.
     */
    public static AddProductFragment newInstanceForEdit(long productId) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_EDIT_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getContext()); // Initialize dbHelper

        if (getArguments() != null) {
            editingProductId = getArguments().getLong(ARG_EDIT_PRODUCT_ID, -1);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);

        // Initialize TextInputLayouts
        tilProductBarcode = view.findViewById(R.id.til_product_barcode);
        tilProductVariant = view.findViewById(R.id.til_product_variant);
        tilProductCategory = view.findViewById(R.id.til_product_category);
        tilProductType = view.findViewById(R.id.til_product_type);
        tilProductBrand = view.findViewById(R.id.til_product_brand);
        tilProductTitle = view.findViewById(R.id.til_product_title);
        tilProductUnit = view.findViewById(R.id.til_product_unit);
        tilProductQuantity = view.findViewById(R.id.til_product_quantity);
        tilProductPercentage = view.findViewById(R.id.til_product_percentage);

        // Initialize TextInputEditTexts
        etProductBarcode = view.findViewById(R.id.et_product_barcode);
        etProductVariant = view.findViewById(R.id.et_product_variant);
        etProductCategory = view.findViewById(R.id.et_product_category);
        etProductType = view.findViewById(R.id.et_product_type);
        etProductBrand = view.findViewById(R.id.et_product_brand);
        etProductTitle = view.findViewById(R.id.et_product_title);
        etProductUnit = view.findViewById(R.id.et_product_unit);
        etProductQuantity = view.findViewById(R.id.et_product_quantity);
        etProductPercentage = view.findViewById(R.id.et_product_percentage);
        etProductManufacturer = view.findViewById(R.id.et_product_manufacturer);
        etProductCountry = view.findViewById(R.id.et_product_country);

        Button buttonAddOrUpdateProduct = view.findViewById(R.id.button_add_product);

        dbHelper = new DatabaseHelper(getActivity());

        if (editingProductId != -1) {
            // Edit Mode: Load product data and pre-fill the form
            loadProductDetailsForEditing();
            buttonAddOrUpdateProduct.setText(R.string.update_product);
            // Optionally, change the fragment title or toolbar title here
            if (getActivity() != null && getActivity().getActionBar() != null) {
                getActivity().getActionBar().setTitle("Edit Product");
            } else if (getActivity() instanceof AppCompatActivity) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Edit Product");
            }
        } else {
            // Add Mode
            buttonAddOrUpdateProduct.setText(R.string.add_product);
            // Optionally, set default title
            if (getActivity() != null && getActivity().getActionBar() != null) {
                getActivity().getActionBar().setTitle("Add New Product");
            } else if (getActivity() instanceof AppCompatActivity) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.add_new_product);
            }
        }

        buttonAddOrUpdateProduct.setOnClickListener(v -> attemptAddOrUpdateProduct());



        return view;
    }
    private void loadProductDetailsForEditing() {
        if (editingProductId != -1) {
            // Fetch product from DB using editingProductId
            // This method needs to be added to DatabaseHelper to fetch a single product by ID
            existingProductToEdit = dbHelper.getProductById(editingProductId);

            if (existingProductToEdit != null) {
                etProductBarcode.setText(existingProductToEdit.getBarcode());
                etProductVariant.setText(String.valueOf(existingProductToEdit.getVariant()));
                etProductCategory.setText(existingProductToEdit.getCategory());
                etProductType.setText(existingProductToEdit.getType());
                etProductBrand.setText(existingProductToEdit.getBrand());
                etProductTitle.setText(existingProductToEdit.getTitle());
                etProductQuantity.setText(String.format(Locale.US, "%.2f", existingProductToEdit.getQuantity()));
                etProductUnit.setText(String.valueOf(existingProductToEdit.getUnit()));
                if (existingProductToEdit.getPercentage() != null) {
                    etProductPercentage.setText(String.format(Locale.US, "%.1f", existingProductToEdit.getPercentage()));
                } else {
                    etProductPercentage.setText("");
                }
                etProductManufacturer.setText(existingProductToEdit.getManufacturer() != null ? existingProductToEdit.getManufacturer() : "");
                etProductCountry.setText(existingProductToEdit.getCountry() != null ? existingProductToEdit.getCountry() : "");

                // You might want to make barcode and variant non-editable in edit mode
                etProductBarcode.setEnabled(false);
                etProductVariant.setEnabled(false);
            } else {
                Toast.makeText(getContext(), "Error: Could not load product details for editing.", Toast.LENGTH_LONG).show();
                // Optionally, pop back or disable the form
                if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
            }
        }
    }

    private void attemptAddOrUpdateProduct() {
        // Reset errors
        tilProductBarcode.setError(null);
        tilProductVariant.setError(null);
        tilProductCategory.setError(null);
        tilProductType.setError(null);
        tilProductBrand.setError(null);
        tilProductTitle.setError(null);
        tilProductUnit.setError(null);
        tilProductQuantity.setError(null);
        tilProductPercentage.setError(null);

        // Get values from EditTexts
        String barcode = etProductBarcode.getText().toString().trim();
        String variantStr = etProductVariant.getText().toString().trim();
        String category = etProductCategory.getText().toString().trim();
        String type = etProductType.getText().toString().trim();
        String brand = etProductBrand.getText().toString().trim();
        String title = etProductTitle.getText().toString().trim();
        String unit = etProductUnit.getText().toString().trim();
        String quantityStr = etProductQuantity.getText().toString().trim();
        String percentageStr = etProductPercentage.getText().toString().trim();
        String manufacturer = etProductManufacturer.getText().toString().trim();
        String country = etProductCountry.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // --- Validation ---
         if (barcode.length() != 13) { // Example: Max length for CHAR(13)
            tilProductBarcode.setError("Barcode should be 13 digits long.");
            focusView = etProductBarcode;
            cancel = true;
         }

        // Validate and parse Variant (defaults to 1)
        int variant = 1; // Default variant
        if (!variantStr.isEmpty()) {
            try {
                variant = Integer.parseInt(variantStr);
                if (variant <= 0) {
                    tilProductVariant.setError("Variant must be a positive number.");
                    if (focusView == null) focusView = etProductVariant;
                    cancel = true;
                }
            } catch (NumberFormatException e) {
                tilProductVariant.setError("Invalid number for Variant.");
                if (focusView == null) focusView = etProductVariant;
                cancel = true;
            }
        }

        // Validate Type
        if (type.isEmpty()) {
            tilProductType.setError(getString(R.string.error_field_required));
            if (focusView == null) focusView = etProductType;
            cancel = true;
        }

        // Validate Brand
        if (brand.isEmpty()) {
            tilProductBrand.setError(getString(R.string.error_field_required));
            if (focusView == null) focusView = etProductBrand;
            cancel = true;
        }

        // Validate Unit
        if (unit.isEmpty()) {
            tilProductUnit.setError(getString(R.string.error_field_required));
            if (focusView == null) focusView = etProductUnit;
            cancel = true;
        }

        Double quantity = null;
        if (quantityStr.isEmpty()) {
            tilProductQuantity.setError(getString(R.string.error_field_required));
            if (focusView == null) focusView = etProductQuantity;
            cancel = true;
        } else {
            try {
                quantity = Double.parseDouble(quantityStr);
                if (quantity < 0) {
                    tilProductQuantity.setError("Quantity cannot be negative.");
                    if (focusView == null) focusView = etProductQuantity;
                    cancel = true;
                }
            } catch (NumberFormatException e) {
                tilProductQuantity.setError("Invalid number for Quantity.");
                if (focusView == null) focusView = etProductQuantity;
                cancel = true;
            }
        }

        Double percentage = null;
        if (!percentageStr.isEmpty()) {
            try {
                percentage = Double.parseDouble(percentageStr);
                if (percentage < 0 || percentage > 100) {
                     tilProductPercentage.setError("Percentage must be between 0 and 100.");
                     if (focusView == null) focusView = etProductPercentage;
                     cancel = true;
                }
            } catch (NumberFormatException e) {
                tilProductPercentage.setError("Invalid number for Percentage.");
                if (focusView == null) focusView = etProductPercentage;
                cancel = true;
            }
        }

        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {

            if (editingProductId != -1 && existingProductToEdit != null) {
                // UPDATE existing product
                // You'll need an updateProduct method in DatabaseHelper
                boolean success = dbHelper.updateProduct(
                        editingProductId, // Pass the ID
                        barcode, variant,category, type, brand, title, unit,
                        quantity, percentage, manufacturer, country
                );
                if (success) {
                    Toast.makeText(getContext(), "Product Updated Successfully", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack(); // Go back
                } else {
                    Toast.makeText(getContext(), "Failed to Update Product. Check logs.", Toast.LENGTH_LONG).show();
                }


            } else {

                // ADD new product (your existing logic)
                if (dbHelper.productVariantExists(barcode, variant)) {
                    tilProductVariant.setError("This Barcode + Variant combination already exists.");
                    // Or set error on tilProductBarcode, or both
                    focusView = etProductBarcode;
                    focusView.requestFocus();
                } else if (dbHelper.barcodeExists(barcode)) {
                    tilProductBarcode.setError("Barcode already exists.");
                    focusView = etProductBarcode;
                    focusView.requestFocus();
                } else {
                    dbHelper.insertProduct(
                            barcode,
                            variant,
                            category,
                            type,
                            brand,
                            title,
                            unit,
                            quantity,   // Already Double
                            percentage, // Already Double or null
                            manufacturer.isEmpty() ? null : manufacturer, // Pass null if empty
                            country.isEmpty() ? null : country          // Pass null if empty
                    );
                    // Toast message handled by insertProduct or show one here
                    Toast.makeText(getContext(), "Product Added Successfully", Toast.LENGTH_SHORT).show();
                    clearForm(); // Clear form for next entry
                }
            }

            clearForm();
        }
    }
    private void clearForm() {
        etProductBarcode.setText("");
        etProductVariant.setText("");
        etProductCategory.setText("");
        etProductType.setText("");
        etProductBrand.setText("");
        etProductTitle.setText("");
        etProductUnit.setText("");
        etProductQuantity.setText("");
        etProductPercentage.setText("");
        etProductManufacturer.setText("");
        etProductCountry.setText("");

        tilProductBarcode.setError(null);
        tilProductVariant.setError(null);
        tilProductCategory.setError(null);
        tilProductType.setError(null);
        tilProductBrand.setError(null);
        tilProductTitle.setError(null);
        tilProductUnit.setError(null);
        tilProductQuantity.setError(null);
        tilProductPercentage.setError(null);

        etProductBarcode.requestFocus();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify view references if necessary, though with ViewBinding or careful management,
        // this might not be strictly needed for short-lived fragments.
    }
}
