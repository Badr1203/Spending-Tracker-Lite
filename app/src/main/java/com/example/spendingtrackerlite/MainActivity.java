package com.example.spendingtrackerlite;

import android.os.Bundle;

import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.spendingtrackerlite.fragments.AddProductFragment;
import com.example.spendingtrackerlite.fragments.AddTransactionFragment;
import com.example.spendingtrackerlite.fragments.ExportImportFragment;
import com.example.spendingtrackerlite.fragments.HomeFragment;
import com.example.spendingtrackerlite.fragments.ListProductFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Handling back pressed
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Default back behavior
                    setEnabled(false); // disable callback to let system handle back press
                }
            }
        });

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = getFragment(menuItem);

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_content, selectedFragment)
                            .commit();
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }

            @Nullable
            private Fragment getFragment(@NonNull MenuItem menuItem) {
                Fragment selectedFragment;

                if (menuItem.getItemId() == R.id.nav_home) selectedFragment = new HomeFragment();
                else if (menuItem.getItemId() == R.id.nav_add_product) selectedFragment = new AddProductFragment();
                else if (menuItem.getItemId() == R.id.nav_add_transaction) selectedFragment = new AddTransactionFragment();
                else if (menuItem.getItemId() == R.id.nav_list_product) selectedFragment = new ListProductFragment();
                else if (menuItem.getItemId() == R.id.nav_export_import) selectedFragment = new ExportImportFragment();
                else selectedFragment = null;
                return selectedFragment;
            }
        });

        // Show default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content, new HomeFragment())
                    .commit();
            navView.setCheckedItem(R.id.nav_home);
        }
    }
}
