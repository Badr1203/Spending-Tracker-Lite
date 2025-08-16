package com.example.spendingtrackerlite.adapters; // Or your preferred package for adapters

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendingtrackerlite.R;
import com.example.spendingtrackerlite.models.Product;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private OnProductClickListener onProductClickListener; // For item click events

    // Interface for click events
    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onProductLongClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList != null ? productList : new ArrayList<>();
        this.onProductClickListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, onProductClickListener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Method to update the list of products in the adapter
    public void updateProducts(List<Product> newProducts) {
        this.productList.clear();
        if (newProducts != null) {
            this.productList.addAll(newProducts);
        }
        notifyDataSetChanged(); // Consider using DiffUtil for better performance
    }

    public void addProduct(Product product) {
        this.productList.add(product);
        notifyItemInserted(this.productList.size() - 1);
    }

    public void clearProducts() {
        this.productList.clear();
        notifyDataSetChanged();
    }


    // ViewHolder Class
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductTitleBrand;
        TextView tvProductBarcodeVariant;
        TextView tvProductCategory;
        TextView tvProductType;
        TextView tvProductDetails;
        TextView tvProductOrigin;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductTitleBrand = itemView.findViewById(R.id.tv_product_title_brand);
            tvProductBarcodeVariant = itemView.findViewById(R.id.tv_product_barcode_variant);
            tvProductCategory = itemView.findViewById(R.id.tv_product_category);
            tvProductType = itemView.findViewById(R.id.tv_product_type);
            tvProductDetails = itemView.findViewById(R.id.tv_product_details);
            tvProductOrigin = itemView.findViewById(R.id.tv_product_origin);
        }

        public void bind(final Product product, final OnProductClickListener listener) {
            DecimalFormat df = new DecimalFormat("#.##");
            tvProductTitleBrand.setText(String.format(Locale.getDefault(), "%s %s %s %s",
                    product.getBrand(), df.format(product.getQuantity()), product.getUnit(), product.getTitle()));

            tvProductBarcodeVariant.setText(String.format(Locale.getDefault(), "Barcode: %s, Variant: %d",
                    product.getBarcode(), product.getVariant()));

            tvProductCategory.setText(String.format(Locale.getDefault(), "Category: %s", product.getCategory()));
            tvProductType.setText(String.format(Locale.getDefault(), "Type: %s", product.getType()));

            StringBuilder detailsBuilder = new StringBuilder();
            detailsBuilder.append(String.format(Locale.getDefault(), "Qty: %.2f %s",
                    product.getQuantity(), product.getUnit()));
            if (product.getPercentage() != null) {
                detailsBuilder.append(String.format(Locale.getDefault(), ", Pct: %.1f%%", product.getPercentage()));
            }
            tvProductDetails.setText(detailsBuilder.toString());

            // Handle optional Manufacturer and Country
            String manufacturer = product.getManufacturer();
            String country = product.getCountry();
            StringBuilder originBuilder = new StringBuilder();
            boolean hasOriginInfo = false;

            if (!TextUtils.isEmpty(manufacturer)) {
                originBuilder.append("By: ").append(manufacturer);
                hasOriginInfo = true;
            }
            if (!TextUtils.isEmpty(country)) {
                if (hasOriginInfo) {
                    originBuilder.append(" ");
                }
                originBuilder.append("In: ").append(country);
                hasOriginInfo = true;
            }

            if (hasOriginInfo) {
                tvProductOrigin.setText(originBuilder.toString());
                tvProductOrigin.setVisibility(View.VISIBLE);
            } else {
                tvProductOrigin.setVisibility(View.GONE);
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });
            // Set long click listener
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onProductLongClick(product);
                    return true; // Consume the long click
                }
                return false; // Don't consume if no listener
            });
        }
    }
}
