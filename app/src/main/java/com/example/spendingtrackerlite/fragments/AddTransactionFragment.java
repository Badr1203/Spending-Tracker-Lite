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
import com.example.spendingtrackerlite.R;

// For Date/Time Pickers
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
        editTextDate = view.findViewById(R.id.et_date);
        editTextTime = view.findViewById(R.id.et_time);
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
        buttonAddTransaction.setOnClickListener(v -> {
            // Implement your logic to get data from EditTexts
            // and save the transaction to the database
            String scode = editTextStoreCode.getText().toString().trim();
            String barcode = editTextBarcode.getText().toString().trim();
            String priceStr = editTextPrice.getText().toString().trim();
            String date = editTextDate.getText().toString().trim(); // Already formatted
            String time = editTextTime.getText().toString().trim(); // Already formatted

            // Add validation here (e.g., check for empty fields, valid price)

            // Example:
            // if (scode.isEmpty() || barcode.isEmpty() || priceStr.isEmpty() || date.isEmpty() || time.isEmpty()) {
            //     Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            //     return;
            // }
            // double price = Double.parseDouble(priceStr);
            // Call your DatabaseHelper method to add the transaction
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
