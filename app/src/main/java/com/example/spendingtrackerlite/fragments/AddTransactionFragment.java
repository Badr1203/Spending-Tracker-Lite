package com.example.spendingtrackerlite.fragments;

// AddTransactionFragment.java
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Import Button
import android.widget.EditText; // Import EditText (or TextInputEditText)
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spendingtrackerlite.DatabaseHelper;
import com.example.spendingtrackerlite.R;

// For Date/Time Pickers
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Toast;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddTransactionFragment extends Fragment {

    private EditText editTextStoreCode, editTextBarcode, editTextPrice, editTextDate, editTextTime;
    private Button buttonAddTransaction, buttonPickDate, buttonPickTime;
    private Calendar myCalendar;

    public AddTransactionFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        // Initialize views
        editTextStoreCode = view.findViewById(R.id.editTextStoreCode);
        editTextBarcode = view.findViewById(R.id.editTextBarcode);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextTime = view.findViewById(R.id.editTextTime);
        buttonPickDate = view.findViewById(R.id.button_pick_date);
        buttonPickTime = view.findViewById(R.id.button_pick_time);
        buttonAddTransaction = view.findViewById(R.id.button_add_transaction);

        myCalendar = Calendar.getInstance();

        // Set up Date Picker
        DatePickerDialog.OnDateSetListener dateSetListener = (v, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        };

        editTextDate.setOnClickListener(v -> new DatePickerDialog(requireContext(), dateSetListener,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());
        buttonPickDate.setOnClickListener(v -> new DatePickerDialog(requireContext(), dateSetListener,
                myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());


        // Set up Time Picker
        TimePickerDialog.OnTimeSetListener timeSetListener = (v, hourOfDay, minute) -> {
            myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            myCalendar.set(Calendar.MINUTE, minute);
            updateTimeLabel();
        };

        editTextTime.setOnClickListener(v -> new TimePickerDialog(requireContext(), timeSetListener,
                myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show()); // true for 24-hour view
        buttonPickTime.setOnClickListener(v -> new TimePickerDialog(requireContext(), timeSetListener,
                myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true).show());

        // Set up listener for the Add Transaction button
// Inside AddTransactionFragment.java, in the onClickListener for buttonAddTransaction

        buttonAddTransaction.setOnClickListener(v -> {
            String scode = editTextStoreCode.getText().toString().trim();
            String barcode = editTextBarcode.getText().toString().trim();
            String priceStr = editTextPrice.getText().toString().trim();
            String date = editTextDate.getText().toString().trim(); // Assuming this is already formatted
            String time = editTextTime.getText().toString().trim(); // Assuming this is already formatted

            // --- More Robust Validation ---
            if (scode.isEmpty()) {
                editTextStoreCode.setError("Store Code cannot be empty");
                editTextStoreCode.requestFocus();
                return;
            }
            if (barcode.isEmpty()) {
                editTextBarcode.setError("Barcode cannot be empty");
                editTextBarcode.requestFocus();
                return;
            }
            if (priceStr.isEmpty()) {
                editTextPrice.setError("Price cannot be empty");
                editTextPrice.requestFocus();
                return;
            }
            if (date.isEmpty()) {
                // This usually won't happen if you're using the DatePicker to set the text
                Toast.makeText(getContext(), "Please pick a date", Toast.LENGTH_SHORT).show();
                return;
            }
            if (time.isEmpty()) {
                // This usually won't happen if you're using the TimePicker to set the text
                Toast.makeText(getContext(), "Please pick a time", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
                if (price < 0) {
                    editTextPrice.setError("Price cannot be negative");
                    editTextPrice.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                editTextPrice.setError("Invalid price format");
                editTextPrice.requestFocus();
                return;
            }

            // --- End of Validation ---

            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            dbHelper.insertTransaction(scode, barcode, price, date, time);

            // Optionally, clear fields or navigate away after successful insertion
            // editTextStoreCode.setText("");
            // editTextBarcode.setText("");
            // ... etc.
        });

        return view;
    }

    private void updateDateLabel() {
        String myFormat = "yyyy-MM-dd"; // Choose your desired format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateTimeLabel() {
        String myFormat = "HH:mm"; // Choose your desired format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        editTextTime.setText(sdf.format(myCalendar.getTime()));
    }
}
