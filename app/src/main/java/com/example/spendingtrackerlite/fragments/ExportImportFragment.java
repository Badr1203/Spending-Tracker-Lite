package com.example.spendingtrackerlite.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spendingtrackerlite.ExportImportListener;
import com.example.spendingtrackerlite.R;
import com.example.spendingtrackerlite.model.SettingItem;
import com.example.spendingtrackerlite.adapters.ExportImportAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class ExportImportFragment extends Fragment implements ExportImportListener {
    private ActivityResultLauncher<Intent> createBackupLauncher;
    private ActivityResultLauncher<Intent> importLauncher;
    RecyclerView recyclerView;
    ExportImportAdapter adapter;
    ArrayList<SettingItem> items;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createBackupLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri uri = data.getData();
                            exportDatabaseToUri(requireContext(), uri);
                        }
                    }
                });

        // Register result handler for importing
        importLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri uri = data.getData();
                            importDatabaseFromUri(requireContext(), uri);
                        }
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_import_export, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        items = new ArrayList<>();

        // Section 1
        items.add(new SettingItem(SettingItem.TYPE_HEADER, "Database Tools", null));
        items.add(new SettingItem(SettingItem.TYPE_ITEM, "Export", "Export DB into external storage"));
        items.add(new SettingItem(SettingItem.TYPE_ITEM, "Import", "Load DB from backup"));

        // Section 2
        items.add(new SettingItem(SettingItem.TYPE_HEADER, "Preferences", null));
        items.add(new SettingItem(SettingItem.TYPE_ITEM, "Theme", "Change app theme"));
        items.add(new SettingItem(SettingItem.TYPE_ITEM, "Notifications", "Manage alerts and badges"));

        adapter = new ExportImportAdapter(getContext(), items, requireActivity(), this);
        recyclerView.setAdapter(adapter);

        return view;
    }
    @Override
    public void onRequestExportBackup() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_TITLE, "backup.db");
        createBackupLauncher.launch(intent);
    }

    @Override
    public void onRequestImportBackup() {
        // Launch file picker
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_TITLE, "Select backup file");
        importLauncher.launch(intent);
    }

    private void exportDatabaseToUri(Context context, Uri uri) {
        String dbName = "SpendingTracker.db";
        File dbFile = context.getDatabasePath(dbName);

        try (InputStream input = new FileInputStream(dbFile);
             OutputStream output = context.getContentResolver().openOutputStream(uri)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            Toast.makeText(context, "Backup saved successfully.", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Backup failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void importDatabaseFromUri(Context context, Uri uri) {
        String dbName = "SpendingTracker.db";
        File dbFile = context.getDatabasePath(dbName);

        try (InputStream input = context.getContentResolver().openInputStream(uri);
             OutputStream output = new FileOutputStream(dbFile)) {

            byte[] buffer = new byte[1024];
            int length;

            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            Toast.makeText(context, "Import successful!", Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Import failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
