package com.example.spendingtrackerlite.fragments; // Adjust to your fragment's package

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
// import androidx.recyclerview.widget.DividerItemDecoration; // Optional for dividers

import com.example.spendingtrackerlite.DatabaseHelper;
import com.example.spendingtrackerlite.R;
import com.example.spendingtrackerlite.adapters.ProductAdapter;
import com.example.spendingtrackerlite.models.Product;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Assuming your fragment is named ProductListFragment
public class ListProductFragment extends Fragment implements ProductAdapter.OnProductClickListener {

    private RecyclerView recyclerViewProducts;
    private ProductAdapter productAdapter;
    private List<Product> allProductsList;
    private TextInputEditText etSearchProduct;
    private TextView tvNoProductsFound;
    private DatabaseHelper dbHelper;

    public ListProductFragment() {

    }

    public static ListProductFragment newInstance() {
        return new ListProductFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getContext());
        allProductsList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_product, container, false);

        recyclerViewProducts = view.findViewById(R.id.recycler_view_products);
        etSearchProduct = view.findViewById(R.id.et_search_product);
        tvNoProductsFound = view.findViewById(R.id.tv_no_products_found);

        setupRecyclerView();
        setupSearch();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadAllProducts(); // Load products after view is created
    }


    private void setupRecyclerView() {
        // Initialize adapter with an empty list initially, or with allProductsList if loaded synchronously
        productAdapter = new ProductAdapter(getContext(), new ArrayList<>(), this);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewProducts.setAdapter(productAdapter);

        // Optional: Add item decoration for dividers
        // DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        // recyclerViewProducts.addItemDecoration(itemDecor);
    }

    private void loadAllProducts() {
        // This should fetch all products from the database
        // In a real app, consider doing this on a background thread if it's a large dataset
        List<Product> productsFromDB = dbHelper.getAllProducts(); // You already created this method

        allProductsList.clear();
        if (productsFromDB != null) {
            allProductsList.addAll(productsFromDB);
        }

        if (allProductsList.isEmpty()) {
            tvNoProductsFound.setVisibility(View.VISIBLE);
            recyclerViewProducts.setVisibility(View.GONE);
            productAdapter.updateProducts(new ArrayList<>()); // Clear adapter
        } else {
            tvNoProductsFound.setVisibility(View.GONE);
            recyclerViewProducts.setVisibility(View.VISIBLE);
            productAdapter.updateProducts(new ArrayList<>(allProductsList)); // Display all products initially
        }
        // Apply current search filter if any text is already in search box (e.g. after rotation)
        filterProducts(etSearchProduct.getText().toString());
    }


    private void setupSearch() {
        etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        if (query.isEmpty()) {
            filteredList.addAll(allProductsList);
        } else {
            String lowerCaseQuery = query.toLowerCase(Locale.getDefault());
            for (Product product : allProductsList) {
                // Search in title, brand, barcode, type (add more fields if needed)
                if ((product.getTitle() != null && product.getTitle().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) ||
                        (product.getBrand() != null && product.getBrand().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) ||
                        (product.getBarcode() != null && product.getBarcode().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) ||
                        (product.getType() != null && product.getType().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) ||
                        (String.valueOf(product.getVariant()).contains(lowerCaseQuery)) ) {
                    // You could also search by variant if needed:
                    // String.valueOf(product.getVariant()).contains(lowerCaseQuery)
                    filteredList.add(product);
                }
            }
        }

        productAdapter.updateProducts(filteredList);

        if (filteredList.isEmpty() && !allProductsList.isEmpty()) { // Show "not found" only if there are products but none match
            tvNoProductsFound.setText(R.string.no_products_found); // Or a specific "no results for query" message
            tvNoProductsFound.setVisibility(View.VISIBLE);
            recyclerViewProducts.setVisibility(View.GONE);
        } else if (allProductsList.isEmpty()) { // Show initial "no products" message
            tvNoProductsFound.setText(R.string.no_products_found); // Or your initial empty message
            tvNoProductsFound.setVisibility(View.VISIBLE);
            recyclerViewProducts.setVisibility(View.GONE);
        }
        else {
            tvNoProductsFound.setVisibility(View.GONE);
            recyclerViewProducts.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the list in case data has changed elsewhere (e.g., after adding a new product)
        // If search text exists, it will re-filter
        loadAllProducts();
    }

    @Override
    public void onProductClick(Product clickedProduct) {
        Toast.makeText(getContext(), "Clicked: " + clickedProduct.getTitle() + " (Variant: " + clickedProduct.getVariant() + ")", Toast.LENGTH_SHORT).show();

        String productBarcode = clickedProduct.getBarcode();
        // int productVariant = clickedProduct.getVariant(); // You also have the variant if needed

        if (productBarcode == null || productBarcode.isEmpty()) {
            Toast.makeText(getContext(), "Error: Product barcode not found in clicked item.", Toast.LENGTH_SHORT).show();
            return;
        }

        ProductTransactionsFragment transactionsFragment = ProductTransactionsFragment.newInstance(productBarcode);

        // Navigate to ProductTransactionsFragment
        if (getActivity() != null) { // Good practice to check if Fragment is attached
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_content, transactionsFragment) // IMPORTANT: Use your actual main fragment container ID
                    .addToBackStack(null) // Allows user to navigate back to the product list
                    .commit();
        } else {
            Toast.makeText(getContext(), "Error: Cannot perform navigation.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProductLongClick(Product product) {
        if (product == null) {
            Toast.makeText(getContext(), "Error: Product data for editing is missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        AddProductFragment editProductFragment = AddProductFragment.newInstanceForEdit(product.getId()); // Pass product ID

        if (getActivity() != null) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.main_content, editProductFragment) // Use your main fragment container ID
                    .addToBackStack(null)
                    .commit();
            Toast.makeText(getContext(), "Editing: " + product.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    // You might want to consider using a ViewModel to hold the allProductsList
    // to better handle configuration changes (like screen rotation)
    // and to separate data logic from the UI.
}
