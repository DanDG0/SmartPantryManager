package com.example.smartpantry;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/** Create or Update a pantry item, with input validation. */
public class AddEditIngredientActivity extends AppCompatActivity {

    private static final String[] UNITS = {"g", "kg", "ml", "l", "pcs"};
    private EditText etName, etQty, etExpiry;
    private Spinner spUnit;
    private DatabaseHelper db;
    private long editId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        db = new DatabaseHelper(this);
        etName = findViewById(R.id.etName);
        etQty = findViewById(R.id.etQuantity);
        etExpiry = findViewById(R.id.etExpiry);
        spUnit = findViewById(R.id.spUnit);
        spUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, UNITS));

        editId = getIntent().getLongExtra("item_id", -1);
        if (editId != -1) {
            setTitle("Edit Ingredient");
            PantryItem p = db.getPantryItem(editId);
            if (p != null) {
                etName.setText(p.name);
                etQty.setText(String.valueOf(p.quantity));
                etExpiry.setText(p.expiry);
                for (int i = 0; i < UNITS.length; i++) if (UNITS[i].equals(p.unit)) spUnit.setSelection(i);
            }
        } else {
            setTitle("Add Ingredient");
        }
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
    }

    private void save() {
        String name = etName.getText().toString().trim();
        String qtyText = etQty.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        // ---- validation ----
        if (name.isEmpty()) { etName.setError("Name is required"); return; }
        if (!name.matches("[A-Za-z ]+")) { etName.setError("Letters only"); return; }
        if (qtyText.isEmpty()) { etQty.setError("Quantity is required"); return; }
        double qty;
        try { qty = Double.parseDouble(qtyText); }
        catch (NumberFormatException e) { etQty.setError("Enter a valid number"); return; }
        if (qty <= 0) { etQty.setError("Must be greater than 0"); return; }
        if (!expiry.isEmpty() && !expiry.matches("\\d{4}-\\d{2}-\\d{2}")) {
            etExpiry.setError("Use format yyyy-MM-dd"); return;
        }

        PantryItem p = new PantryItem(editId, name, qty, spUnit.getSelectedItem().toString(), expiry);
        if (editId == -1) {
            db.addPantryItem(p);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        } else {
            db.updatePantryItem(p);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
