package com.example.spendingtrackerlite.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendingtrackerlite.R;
import com.example.spendingtrackerlite.models.SettingItem;
import com.example.spendingtrackerlite.ExportImportListener;

import java.util.List;

public class ExportImportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ExportImportListener actionListerner;
    private final List<SettingItem> itemList;
    private final Context context;
    private final Activity activity;

    public ExportImportAdapter(Context context, List<SettingItem> itemList, Activity activity, ExportImportListener listener) {
        this.context = context;
        this.itemList = itemList;
        this.activity = activity;
        this.actionListerner = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).type;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SettingItem.TYPE_HEADER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_section_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_setting, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SettingItem item = itemList.get(position);

        if (item.type == SettingItem.TYPE_HEADER) {
            ((HeaderViewHolder) holder).textHeader.setText(item.title);
        } else {
            ((ItemViewHolder) holder).textTitle.setText(item.title);
            ((ItemViewHolder) holder).textDescription.setText(item.description);

            // ✅ Add click actions here
            holder.itemView.setOnClickListener(v -> {
                switch (item.title) {
                    case "Export":
                        if (actionListerner != null) {
                            actionListerner.onRequestExportBackup();
                        }
                        Toast.makeText(context, "Exporting database...", Toast.LENGTH_LONG).show();
                        break;
                    case "Import":
                        if (actionListerner != null) {
                            actionListerner.onRequestImportBackup();
                        }
                        Toast.makeText(context, "Importing database...", Toast.LENGTH_LONG).show();
                        break;
                }
            });
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView textHeader;

        HeaderViewHolder(View itemView) {
            super(itemView);
            textHeader = itemView.findViewById(R.id.textHeader);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textDescription;

        ItemViewHolder(View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
        }
    }

}
