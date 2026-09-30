package com.example.smartpantry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/** Home screen: lists all pantry items (Read) and launches Add/Edit/Delete. */
public class PantryActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecyclerView recycler;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        setTitle("My Pantry");
        db = new DatabaseHelper(this);
        recycler = findViewById(R.id.recyclerPantry);
        empty = findViewById(R.id.tvEmpty);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.fabAdd).setOnClickListener(v ->
                startActivity(new Intent(this, AddEditIngredientActivity.class)));
    }

    /** Reload from the database whenever we return to this screen. */
    @Override
    protected void onResume() {
        super.onResume();
        loadPantry();
    }

    private void loadPantry() {
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        boolean alerts = prefs.getBoolean("expiry_alerts", true);
        List<PantryItem> items = db.getAllPantryItems();
        empty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        recycler.setAdapter(new PantryAdapter(items, alerts, new PantryAdapter.Listener() {
            @Override public void onEdit(PantryItem item) {
                Intent i = new Intent(PantryActivity.this, AddEditIngredientActivity.class);
                i.putExtra("item_id", item.id);
                startActivity(i);
            }
            @Override public void onDelete(PantryItem item) {
                new AlertDialog.Builder(PantryActivity.this)
                        .setTitle("Delete ingredient")
                        .setMessage("Remove " + item.name + " from your pantry?")
                        .setPositiveButton("Delete", (d, w) -> { db.deletePantryItem(item.id); loadPantry(); })
                        .setNegativeButton("Cancel", null).show();
            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_suggested) {
            startActivity(new Intent(this, SuggestedRecipesActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
