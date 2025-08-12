package com.example.spendingtrackerlite.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spendingtrackerlite.DatabaseHelper; // Adjust import
import com.example.spendingtrackerlite.R; // Adjust import
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddStoreFragment extends Fragment {

    private TextInputLayout tilStoreScode, tilStoreName, tilStoreLongitude, tilStoreLatitude;
    private TextInputEditText etStoreScode, etStoreName, etStoreLongitude, etStoreLatitude;
    private Button buttonAddStore;

    private DatabaseHelper dbHelper;

    public AddStoreFragment() {
        // Required empty public constructor
    }

    public static AddStoreFragment newInstance() {
        return new AddStoreFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_store, container, false);

        // Initialize Views
        tilStoreScode = view.findViewById(R.id.til_store_scode);
        etStoreScode = view.findViewById(R.id.et_store_scode);
        tilStoreName = view.findViewById(R.id.til_store_name);
        etStoreName = view.findViewById(R.id.et_store_name);
        tilStoreLongitude = view.findViewById(R.id.til_store_longitude);
        etStoreLongitude = view.findViewById(R.id.et_store_longitude);
        tilStoreLatitude = view.findViewById(R.id.til_store_latitude);
        etStoreLatitude = view.findViewById(R.id.et_store_latitude);
        buttonAddStore = view.findViewById(R.id.button_add_store);

        buttonAddStore.setOnClickListener(v -> attemptAddStore());

        return view;
    }

    private void attemptAddStore() {
        // Reset errors
        tilStoreScode.setError(null);
        tilStoreName.setError(null);
        tilStoreLongitude.setError(null); // Optional fields, error might not be needed
        tilStoreLatitude.setError(null);  // Optional fields, error might not be needed

        String scode = etStoreScode.getText().toString().trim();
        String name = etStoreName.getText().toString().trim();
        String longitudeStr = etStoreLongitude.getText().toString().trim();
        String latitudeStr = etStoreLatitude.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Validate SCODE (Primary Key, should not be empty)
        if (TextUtils.isEmpty(scode)) {
            tilStoreScode.setError(getString(R.string.error_field_required)); // Add this string to strings.xml
            focusView = etStoreScode;
            cancel = true;
        } else if (dbHelper.storeScodeExists(scode)) { // You'll need to add storeScodeExists to DatabaseHelper
            tilStoreScode.setError("This Store Code already exists.");
            focusView = etStoreScode;
            cancel = true;
        }


        // Validate Store Name (Should not be empty)
        if (TextUtils.isEmpty(name)) {
            tilStoreName.setError(getString(R.string.error_field_required));
            if (focusView == null) focusView = etStoreName; // Set focus only if not set by SCODE
            cancel = true;
        }

        Double longitude = null;
        if (!TextUtils.isEmpty(longitudeStr)) {
            try {
                longitude = Double.parseDouble(longitudeStr);
                if (longitude < -180 || longitude > 180) {
                    tilStoreLongitude.setError("Invalid longitude (-180 to 180).");
                    if (focusView == null) focusView = etStoreLongitude;
                    cancel = true;
                }
            } catch (NumberFormatException e) {
                tilStoreLongitude.setError("Invalid number format for longitude.");
                if (focusView == null) focusView = etStoreLongitude;
                cancel = true;
            }
        }

        Double latitude = null;
        if (!TextUtils.isEmpty(latitudeStr)) {
            try {
                latitude = Double.parseDouble(latitudeStr);
                if (latitude < -90 || latitude > 90) {
                    tilStoreLatitude.setError("Invalid latitude (-90 to 90).");
                    if (focusView == null) focusView = etStoreLatitude;
                    cancel = true;
                }
            } catch (NumberFormatException e) {
                tilStoreLatitude.setError("Invalid number format for latitude.");
                if (focusView == null) focusView = etStoreLatitude;
                cancel = true;
            }
        }


        if (cancel) {
            // There was an error; don't attempt to add and focus the first field with an error.
            if (focusView != null) {
                focusView.requestFocus();
            }
        } else {
            // All good, proceed to add the store
            // You'll need to create an insertStore method in your DatabaseHelper
            dbHelper.insertStore(scode, name, longitude, latitude);

            // Optionally, clear the fields or navigate away
            clearForm();
            Toast.makeText(getContext(), "Store added successfully!", Toast.LENGTH_SHORT).show();

            // Example: Navigate back or to another fragment
            // if (getParentFragmentManager() != null) {
            //     getParentFragmentManager().popBackStack();
            // }
        }
    }

    private void clearForm() {
        etStoreScode.setText("");
        etStoreName.setText("");
        etStoreLongitude.setText("");
        etStoreLatitude.setText("");
        tilStoreScode.setError(null);
        tilStoreName.setError(null);
        tilStoreLongitude.setError(null);
        tilStoreLatitude.setError(null);
        etStoreScode.requestFocus(); // Set focus back to the first field
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Nullify view references to avoid memory leaks if DatabaseHelper is tied to context too long
        // dbHelper = null; // Be careful with this if dbHelper is used elsewhere or has a longer lifecycle
    }
}
