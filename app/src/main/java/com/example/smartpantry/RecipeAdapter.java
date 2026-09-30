package com.example.smartpantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.Holder> {

    public interface Listener { void onClick(Recipe recipe); }

    private final List<Recipe> recipes;
    private final Listener listener;

    public RecipeAdapter(List<Recipe> recipes, Listener listener) {
        this.recipes = recipes; this.listener = listener;
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView name, count;
        Holder(View v) {
            super(v);
            name = v.findViewById(R.id.tvRecipeName);
            count = v.findViewById(R.id.tvIngredientCount);
        }
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        Recipe r = recipes.get(position);
        h.name.setText(r.name);
        h.count.setText(r.ingredients.size() + " ingredients - you have them all");
        h.itemView.setOnClickListener(v -> listener.onClick(r));
    }

    @Override
    public int getItemCount() { return recipes.size(); }
}
