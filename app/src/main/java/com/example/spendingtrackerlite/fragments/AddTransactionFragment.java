package com.example.spendingtrackerlite.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spendingtrackerlite.DatabaseHelper;
import com.example.spendingtrackerlite.R;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTransactionFragment extends Fragment {

    private TextInputLayout tilStoreCode, tilBarcode, tilVariant, tilQuantity, tilPrice,
            tilDiscountedPrice, tilDate, tilTime, tilLink;
    private TextInputEditText etStoreCode, etBarcode, etVariant,etQuantity, etPrice,
            etDiscountedPrice, etDate, etTime, etLink;
    private Button buttonPickDate, buttonPickTime, buttonAddTransaction;
    private MaterialCheckBox checkboxIsPurchase;

    private DatabaseHelper dbHelper;
    private final Calendar calendar = Calendar.getInstance();

    public AddTransactionFragment() {
        // Required empty public constructor
    }

    public static AddTransactionFragment newInstance() {
        return new AddTransactionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        // Initialize TextInputLayouts
        tilStoreCode = view.findViewById(R.id.til_transaction_store_code);
        tilBarcode = view.findViewById(R.id.til_transaction_barcode);
        tilVariant = view.findViewById(R.id.til_transaction_variant); // New
        tilQuantity = view.findViewById(R.id.til_transaction_quantity); // New
        tilPrice = view.findViewById(R.id.til_transaction_price);
        tilDiscountedPrice = view.findViewById(R.id.til_transaction_discounted_price); // New
        tilDate = view.findViewById(R.id.til_transaction_date);
        tilTime = view.findViewById(R.id.til_transaction_time);
        tilLink = view.findViewById(R.id.til_transaction_link); // New

        // Initialize TextInputEditTexts
        etStoreCode = view.findViewById(R.id.et_transaction_store_code);
        etBarcode = view.findViewById(R.id.et_transaction_barcode);
        etVariant = view.findViewById(R.id.et_transaction_variant); // New
        etQuantity = view.findViewById(R.id.et_transaction_quantity); // New
        etPrice = view.findViewById(R.id.et_transaction_price);
        etDiscountedPrice = view.findViewById(R.id.et_transaction_discounted_price); // New
        etDate = view.findViewById(R.id.et_transaction_date);
        etTime = view.findViewById(R.id.et_transaction_time);
        etLink = view.findViewById(R.id.et_transaction_link); // New

        // Initialize Buttons
        buttonPickDate = view.findViewById(R.id.button_pick_date);
        buttonPickTime = view.findViewById(R.id.button_pick_time);
        buttonAddTransaction = view.findViewById(R.id.button_add_transaction);
        checkboxIsPurchase = view.findViewById(R.id.checkbox_is_purchase);

        setupDateTimePickers();

        buttonAddTransaction.setOnClickListener(v -> attemptAddTransaction());

        return view;
    }

    private void setupDateTimePickers() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateInView();
        };

        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            updateTimeInView();
        };

        etDate.setOnClickListener(v -> new DatePickerDialog(getContext(), dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());
        buttonPickDate.setOnClickListener(v -> new DatePickerDialog(getContext(), dateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show());


        etTime.setOnClickListener(v -> new TimePickerDialog(getContext(), timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                true).show()); // true for 24-hour view
        buttonPickTime.setOnClickListener(v -> new TimePickerDialog(getContext(), timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                true).show());
    }

    private void updateDateInView() {
        String myFormat = "yyyy-MM-dd"; // Your desired format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDate.setText(sdf.format(calendar.getTime()));
        tilDate.setError(null); // Clear error on selection
    }

    private void updateTimeInView() {
        String myFormat = "HH:mm"; // Your desired format (use "HH:mm:ss" if seconds are needed)
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etTime.setText(sdf.format(calendar.getTime()));
        tilTime.setError(null); // Clear error on selection
    }

    private void attemptAddTransaction() {
        // Reset errors
        tilStoreCode.setError(null);
        tilBarcode.setError(null);
        tilVariant.setError(null);
        tilQuantity.setError(null);
        tilPrice.setError(null);
        tilDiscountedPrice.setError(null);
        tilDate.setError(null);
        tilTime.setError(null);
        tilLink.setError(null);

        // Get values
        String storeCode = etStoreCode.getText().toString().trim();
        String barcode = etBarcode.getText().toString().trim();
        String variantStr = etVariant.getText().toString().trim();
        String quantityStr = etQuantity.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String discountedPriceStr = etDiscountedPrice.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String link = etLink.getText().toString().trim();
        boolean isPurchase = checkboxIsPurchase.isChecked();

        boolean cancel = false;
        View focusView = null;

        // --- Validation ---

        // Validate Store Code
        if (TextUtils.isEmpty(storeCode)) {
            tilStoreCode.setError(getString(R.string.error_field_required));
            focusView = etStoreCode;
            cancel = true;
        } else if (!dbHelper.storeScodeExists(storeCode)) {
            tilStoreCode.setError("Store Code does not exist.");
            focusView = etStoreCode;
            cancel = true;
        }

        // Validate Barcode
        if (TextUtils.isEmpty(barcode)) {
            tilBarcode.setError(getString(R.string.error_field_required));
            if (focusView == null) focusView = etBarcode;
            cancel = true;
        } else if (barcode.length() != 13) {
            tilBarcode.setError("Barcode cannot exceed 13 characters.");
            if (focusView == null) focusView = etBarcode;
            cancel = true;
        }
        else if (!dbHelper.barcodeExists(barcode)) {
            tilBarcode.setError("Barcode does not exist.");
            if (focusView == null) focusView = etBarcode;
            cancel = true;
        }

        // Validate and parse Variant (defaults to 1)
        int variant = 1; // Default variant
        if (!TextUtils.isEmpty(variantStr)) {
            try {
                variant = Integer.parseInt(variantStr);
                if (variant <= 0) {
                    tilVariant.setError("Variant must be a positive number.");
                    if (focusView == null) focusView = etVariant;
                    cancel = true;
                }
            } catch (NumberFormatException e) {
                tilVariant.setError("Invalid number for Variant.");
                if (focusView == null) focusView = etVariant;
                cancel = true;
            }
        }

        int quantity = 1; // Default
        if (TextUtils.isEmpty(quantityStr)) {
            // Optional: etTransactionQuantity.setError("Quantity is required"); cancel = true;
            // Or just use default of 1
        } else {
            try {
                quantity = Integer.parseInt(quantityStr);
                if (quantity <= 0) {
                    etQuantity.setError("Quantity must be positive");
                    focusView = etQuantity;
                    cancel = true;
                }
            } catch (NumberFormatException e) {
                etQuantity.setError("Invalid number");
                focusView = etQuantity;
                cancel = true;
            }
        }



        // Validate Barcode + Variant combination exists in Products (only if individual fields are valid so far)
        if (!cancel && !TextUtils.isEmpty(barcode)) {
            if (!dbHelper.productVariantExists(barcode, variant)) {
                tilVariant.setError("This Product (Barcode + Variant) does not exist.");
                focusView = etBarcode;
                cancel = true;
            }
        }

        // Validate and parse Price
        Double price = null;
        if (TextUtils.isEmpty(priceStr)) {
            tilPrice.setError(getString(R.string.error_field_required));
            if (focusView == null) focusView = etPrice;
            cancel = true;
        } else {
            try {
                price = Double.parseDouble(priceStr);
                if (price <= 0) { // Price should be positive
                    tilPrice.setError("Price must be greater than zero.");
                    if (focusView == null) focusView = etPrice;
                    cancel = true;
                }
            } catch (NumberFormatException e) {
                tilPrice.setError("Invalid number for Price.");
                if (focusView == null) focusView = etPrice;
                cancel = true;
            }
        }

        // Validate and parse Discounted Price (Optional)
        Double discountedPrice = null; // Stays null if empty
        if (!TextUtils.isEmpty(discountedPriceStr)) {
            try {
                discountedPrice = Double.parseDouble(discountedPriceStr);
                if (discountedPrice < 0) {
                    tilDiscountedPrice.setError("Discounted Price cannot be negative.");
                    if (focusView == null) focusView = etDiscountedPrice;
                    cancel = true;
                } else if (price != null && discountedPrice > price) {
                    tilDiscountedPrice.setError("Discounted Price cannot be greater than original Price.");
                    if (focusView == null) focusView = etDiscountedPrice;
                    cancel = true;
                }
            } catch (NumberFormatException e) {
                tilDiscountedPrice.setError("Invalid number for Discounted Price.");
                if (focusView == null) focusView = etDiscountedPrice;
                cancel = true;
            }
        }

        // Validate Date
        if (TextUtils.isEmpty(date)) {
            tilDate.setError(getString(R.string.error_field_required));
            if (focusView == null) focusView = etDate; // Or buttonPickDate
            cancel = true;
        }

        // Validate Time
        if (TextUtils.isEmpty(time)) {
            tilTime.setError(getString(R.string.error_field_required));
            if (focusView == null) focusView = etTime; // Or buttonPickTime
            cancel = true;
        }

        // Validate Link (Optional - basic check if provided)
        if (!TextUtils.isEmpty(link) && !Patterns.WEB_URL.matcher(link).matches()) {
            // This is a basic check, might not catch all invalid URLs or allow all valid ones
            // For more robust validation, consider a dedicated library or more complex regex.
            tilLink.setError("Invalid URL format.");
            if (focusView == null) focusView = etLink;
            cancel = true;
        }

        if (cancel) {
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // All validations passed, attempt to insert
            // Ensure your DatabaseHelper.insertTransaction method is updated
            dbHelper.insertTransaction(
                    storeCode,
                    barcode,
                    variant,    // New
                    quantity,   // New
                    price,      // Double
                    discountedPrice, // Double or null
                    date,
                    time,
                    link.isEmpty() ? null : link, // Pass null if empty
                    isPurchase
            );

            clearForm();
        }
    }

    private void clearForm() {
        etStoreCode.setText("");
        etBarcode.setText("");
        etVariant.setText("");
        etQuantity.setText("");
        etPrice.setText("");
        etDiscountedPrice.setText("");
        etDate.setText("");
        etTime.setText("");
        etLink.setText("");

        tilStoreCode.setError(null);
        tilBarcode.setError(null);
        tilVariant.setError(null);
        tilQuantity.setError(null);
        tilPrice.setError(null);
        tilDiscountedPrice.setError(null);
        tilDate.setError(null);
        tilTime.setError(null);
        tilLink.setError(null);
        checkboxIsPurchase.setChecked(true);

        etStoreCode.requestFocus(); // Focus first field
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify view references if using traditional findViewById
        // Not strictly necessary with ViewBinding if the binding object itself is nulled out.
    }
}
