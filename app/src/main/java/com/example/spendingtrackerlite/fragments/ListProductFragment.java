package com.example.spendingtrackerlite.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spendingtrackerlite.DatabaseHelper;
import com.example.spendingtrackerlite.R;

import java.util.ArrayList;

public class ListProductFragment extends Fragment {
    public ListProductFragment() {}
    EditText searchEditText;
    ListView productListView;
    ArrayList<String> allProducts;
    ArrayAdapter<String> adapter;
    DatabaseHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_product, container, false);
        super.onCreate(savedInstanceState);

        productListView = view.findViewById(R.id.listViewProducts);
        searchEditText = view.findViewById(R.id.searchEditText);

        dbHelper = new DatabaseHelper(getActivity());
        allProducts = dbHelper.getAllProducts();

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<>(allProducts));
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

        return view;
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
