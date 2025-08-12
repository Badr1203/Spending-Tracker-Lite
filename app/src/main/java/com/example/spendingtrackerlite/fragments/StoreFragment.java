package com.example.spendingtrackerlite.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.spendingtrackerlite.DatabaseHelper;
import com.example.spendingtrackerlite.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class StoreFragment extends Fragment {

    private ListView listViewStores;
    private TextView tvNoStores;
    private FloatingActionButton fabAddStore;
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> storeAdapter;
    private ArrayList<String> storeList;

    public StoreFragment() {
        // Required empty public constructor
    }

    public static StoreFragment newInstance() {
        return new StoreFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getContext());
        storeList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_store, container, false);

        listViewStores = view.findViewById(R.id.list_view_stores);
        tvNoStores = view.findViewById(R.id.tv_no_stores);
        fabAddStore = view.findViewById(R.id.fab_add_store);

        storeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, storeList);
        listViewStores.setAdapter(storeAdapter);

        fabAddStore.setOnClickListener(v -> {
            AddStoreFragment addStoreFragment = AddStoreFragment.newInstance();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_content, addStoreFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Optional: Add item click listener for ListView if you want to do something when a store is clicked
        // listViewStores.setOnItemClickListener((parent, view1, position, id) -> {
        //     String selectedStoreString = storeList.get(position);
        //     // Handle store item click - e.g., show details, edit, etc.
        // });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStores(); // Load or reload stores when the fragment becomes visible
    }

    private void loadStores() {
        List<String> currentStores = dbHelper.getAllStores();
        storeList.clear();

        if (currentStores != null && !currentStores.isEmpty()) {
            storeList.addAll(currentStores);
            listViewStores.setVisibility(View.VISIBLE);
            tvNoStores.setVisibility(View.GONE);
        } else {
            listViewStores.setVisibility(View.GONE);
            tvNoStores.setVisibility(View.VISIBLE);
        }
        storeAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // dbHelper = null; // Consider lifecycle of dbHelper
    }
}
