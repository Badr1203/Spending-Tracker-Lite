package com.example.spendingtrackerlite.fragments;

// ProductTransactionsFragment.java
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spendingtrackerlite.R;
import com.example.spendingtrackerlite.DatabaseHelper; // Replace

import java.util.ArrayList;
import java.util.List;

public class ProductTransactionsFragment extends Fragment {

    private static final String ARG_PRODUCT_BARCODE = "product_barcode";
    private static final String ARG_PRODUCT_TITLE = "product_title"; // Optional: for display

    private String productBarcode;
    private String productTitle; // Optional

    private ListView transactionsListView;
    private TextView headerTextView;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> transactionDetailsList;

    // It's good practice to use newInstance for fragment creation with arguments
    public static ProductTransactionsFragment newInstance(String productBarcode) {
        ProductTransactionsFragment fragment = new ProductTransactionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_BARCODE, productBarcode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productBarcode = getArguments().getString(ARG_PRODUCT_BARCODE);
            productTitle = getArguments().getString(ARG_PRODUCT_TITLE); // Optional
        }
        dbHelper = new DatabaseHelper(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_transactions, container, false);

        headerTextView = view.findViewById(R.id.tv_product_transactions_header);
        transactionsListView = view.findViewById(R.id.lv_product_transactions);

        transactionDetailsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, transactionDetailsList);
        transactionsListView.setAdapter(adapter);

        if (productTitle != null && !productTitle.isEmpty()) {
            headerTextView.setText("Transactions for: " + productTitle);
        } else if (productBarcode != null) {
            headerTextView.setText("Transactions for Barcode: " + productBarcode);
        }

        loadTransactions();

        return view;
    }

    private void loadTransactions() {
        if (productBarcode == null || productBarcode.isEmpty()) {
            Toast.makeText(getContext(), "Product Barcode not provided.", Toast.LENGTH_SHORT).show();
            return;
        }

        // You'll need to create this method in your DatabaseHelper
        List<String> transactions = dbHelper.getTransactionsForProduct(productBarcode);

        if (transactions != null && !transactions.isEmpty()) {
            transactionDetailsList.clear();
            transactionDetailsList.addAll(transactions);
            adapter.notifyDataSetChanged();
        } else {
            transactionDetailsList.clear(); // Clear old data
            // Add a "no transactions found" message or handle UI accordingly
            transactionDetailsList.add("No transactions found for this product.");
            adapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "No transactions found for " + (productTitle != null ? productTitle : productBarcode), Toast.LENGTH_SHORT).show();
        }
    }
}
