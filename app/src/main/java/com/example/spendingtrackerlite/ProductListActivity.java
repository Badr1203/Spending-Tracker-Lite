package com.example.spendingtrackerlite;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ProductListActivity  extends AppCompatActivity {
    EditText searchEditText;
    ListView productListView;
    ArrayList<String> allProducts;
    ArrayAdapter<String> adapter;
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        productListView = findViewById(R.id.listViewProducts);
        searchEditText = findViewById(R.id.searchEditText);

        dbHelper = new DatabaseHelper(this);
        allProducts = dbHelper.getAllProducts();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>(allProducts));
        productListView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                liveSearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void liveSearch(String query) {
        ArrayList<String> filteredList = new ArrayList<>();
        for (String product : allProducts) {
            if (product.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }

        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

}
