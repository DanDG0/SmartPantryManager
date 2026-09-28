package com.example.smartpantry;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        long id = getIntent().getLongExtra("recipe_id", -1);
        Recipe r = new DatabaseHelper(this).getRecipe(id);
        if (r == null) { finish(); return; }

        setTitle(r.name);
        ((TextView) findViewById(R.id.tvDetailName)).setText(r.name);

        StringBuilder ing = new StringBuilder();
        for (RecipeIngredient i : r.ingredients) {
            String q = i.quantity == (long) i.quantity ? String.valueOf((long) i.quantity) : String.valueOf(i.quantity);
            ing.append("\u2022 ").append(q).append(" ").append(i.unit).append(" ").append(i.name).append("\n");
        }
        ((TextView) findViewById(R.id.tvDetailIngredients)).setText(ing.toString().trim());
        ((TextView) findViewById(R.id.tvDetailSteps)).setText(r.steps);
    }
}
