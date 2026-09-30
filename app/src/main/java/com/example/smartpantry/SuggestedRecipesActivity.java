package com.example.smartpantry;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/** Runs the strict-matching logic and lists ONLY recipes the user can make now. */
public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecyclerView recycler;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);
        setTitle("Suggested Recipes");
        db = new DatabaseHelper(this);
        recycler = findViewById(R.id.recyclerRecipes);
        empty = findViewById(R.id.tvNoMatch);
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Recipe> suggested = MatchingEngine.getSuggested(db.getAllRecipes(), db.getAllPantryItems());
        empty.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
        recycler.setAdapter(new RecipeAdapter(suggested, recipe -> {
            Intent i = new Intent(this, RecipeDetailActivity.class);
            i.putExtra("recipe_id", recipe.id);
            startActivity(i);
        }));
    }
}
