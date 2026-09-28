package com.example.smartpantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/** SQLite storage: pantry CRUD + seeded recipe collection. */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "pantry.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, quantity REAL NOT NULL, unit TEXT NOT NULL, expiry TEXT)");
        db.execSQL("CREATE TABLE recipe (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, steps TEXT NOT NULL)");
        db.execSQL("CREATE TABLE recipe_ingredient (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, name TEXT NOT NULL, quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, FOREIGN KEY(recipe_id) REFERENCES recipe(id) ON DELETE CASCADE)");
        seedRecipes(db); // pre-loaded on first run
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredient");
        db.execSQL("DROP TABLE IF EXISTS recipe");
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    // ------------------------- PANTRY CRUD -------------------------

    public long addPantryItem(PantryItem p) {
        return getWritableDatabase().insert("pantry", null, toValues(p));
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("pantry", null, null, null, null, null, "name COLLATE NOCASE");
        while (c.moveToNext()) list.add(fromCursor(c));
        c.close();
        return list;
    }

    public PantryItem getPantryItem(long id) {
        Cursor c = getReadableDatabase().query("pantry", null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        PantryItem p = c.moveToFirst() ? fromCursor(c) : null;
        c.close();
        return p;
    }

    public int updatePantryItem(PantryItem p) {
        return getWritableDatabase().update("pantry", toValues(p), "id=?", new String[]{String.valueOf(p.id)});
    }

    public int deletePantryItem(long id) {
        return getWritableDatabase().delete("pantry", "id=?", new String[]{String.valueOf(id)});
    }

    private ContentValues toValues(PantryItem p) {
        ContentValues v = new ContentValues();
        v.put("name", p.name);
        v.put("quantity", p.quantity);
        v.put("unit", p.unit);
        v.put("expiry", p.expiry);
        return v;
    }

    private PantryItem fromCursor(Cursor c) {
        return new PantryItem(c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getDouble(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("unit")),
                c.getString(c.getColumnIndexOrThrow("expiry")));
    }

    // ------------------------- RECIPES -------------------------

    public List<Recipe> getAllRecipes() {
        List<Recipe> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query("recipe", null, null, null, null, null, "name");
        while (c.moveToNext()) {
            Recipe r = new Recipe(c.getLong(0), c.getString(1), c.getString(2));
            r.ingredients = getIngredients(r.id);
            list.add(r);
        }
        c.close();
        return list;
    }

    public Recipe getRecipe(long id) {
        Cursor c = getReadableDatabase().query("recipe", null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        Recipe r = null;
        if (c.moveToFirst()) {
            r = new Recipe(c.getLong(0), c.getString(1), c.getString(2));
            r.ingredients = getIngredients(id);
        }
        c.close();
        return r;
    }

    private List<RecipeIngredient> getIngredients(long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("recipe_ingredient", null, "recipe_id=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        while (c.moveToNext()) {
            list.add(new RecipeIngredient(c.getString(c.getColumnIndexOrThrow("name")),
                    c.getDouble(c.getColumnIndexOrThrow("quantity")),
                    c.getString(c.getColumnIndexOrThrow("unit"))));
        }
        c.close();
        return list;
    }

    // ------------------------- SEED DATA -------------------------

    /** ingredients format: "name,qty,unit;name,qty,unit" */
    private void seed(SQLiteDatabase db, String name, String ingredients, String steps) {
        ContentValues rv = new ContentValues();
        rv.put("name", name);
        rv.put("steps", steps);
        long rid = db.insert("recipe", null, rv);
        for (String part : ingredients.split(";")) {
            String[] f = part.split(",");
            ContentValues iv = new ContentValues();
            iv.put("recipe_id", rid);
            iv.put("name", f[0].trim());
            iv.put("quantity", Double.parseDouble(f[1].trim()));
            iv.put("unit", f[2].trim());
            db.insert("recipe_ingredient", null, iv);
        }
    }

    private void seedRecipes(SQLiteDatabase db) {
        seed(db, "Scrambled Eggs on Toast", "egg,3,pcs;bread,2,pcs;butter,10,g;salt,1,g",
                "1. Beat the eggs with salt.\n2. Melt butter in a pan on low heat.\n3. Stir eggs gently until just set.\n4. Toast the bread and serve the eggs on top.");
        seed(db, "Tomato Pasta", "pasta,200,g;tomato,3,pcs;onion,1,pcs;garlic,2,pcs;olive oil,20,ml",
                "1. Boil pasta until al dente.\n2. Fry chopped onion and garlic in oil.\n3. Add chopped tomatoes and simmer 10 minutes.\n4. Toss with the drained pasta.");
        seed(db, "Plain Pancakes", "flour,200,g;egg,2,pcs;milk,300,ml;butter,20,g",
                "1. Whisk flour, eggs and milk into a smooth batter.\n2. Melt a little butter in a hot pan.\n3. Pour in batter and cook 2 minutes per side.");
        seed(db, "Egg Fried Rice", "rice,300,g;egg,2,pcs;onion,1,pcs;soy sauce,20,ml;oil,15,ml",
                "1. Fry chopped onion in oil.\n2. Push aside and scramble the eggs.\n3. Add cooked rice and soy sauce and stir-fry 4 minutes.");
        seed(db, "Cheese Omelette", "egg,3,pcs;cheese,50,g;butter,10,g;salt,1,g",
                "1. Beat eggs with salt.\n2. Pour into buttered pan.\n3. Add grated cheese, fold and cook 1 minute.");
        seed(db, "Cheese Toastie", "bread,2,pcs;cheese,60,g;butter,10,g",
                "1. Butter the outside of the bread.\n2. Fill with cheese.\n3. Toast in a pan until golden on both sides.");
        seed(db, "Potato Mash", "potato,4,pcs;butter,30,g;milk,100,ml;salt,2,g",
                "1. Peel and boil potatoes until soft.\n2. Drain and mash with butter, milk and salt.");
        seed(db, "Roast Potatoes", "potato,6,pcs;oil,40,ml;salt,2,g",
                "1. Heat oven to 200C.\n2. Toss cut potatoes in oil and salt.\n3. Roast 45 minutes, turning once.");
        seed(db, "Tomato Soup", "tomato,6,pcs;onion,1,pcs;garlic,2,pcs;water,500,ml;salt,2,g",
                "1. Fry onion and garlic.\n2. Add chopped tomatoes, water and salt.\n3. Simmer 20 minutes and blend.");
        seed(db, "Garlic Bread", "bread,4,pcs;butter,40,g;garlic,3,pcs",
                "1. Mix butter with crushed garlic.\n2. Spread on bread.\n3. Bake at 180C for 10 minutes.");
        seed(db, "Rice and Beans", "rice,250,g;beans,400,g;onion,1,pcs;oil,15,ml;salt,2,g",
                "1. Cook the rice.\n2. Fry onion, add beans and salt and heat through.\n3. Serve beans over rice.");
        seed(db, "Banana Milkshake", "banana,2,pcs;milk,400,ml",
                "1. Peel bananas.\n2. Blend with cold milk until smooth.");
        seed(db, "Porridge", "oats,80,g;milk,300,ml;salt,1,g",
                "1. Combine oats, milk and salt in a pot.\n2. Stir over medium heat for 5 minutes until thick.");
        seed(db, "French Toast", "bread,4,pcs;egg,2,pcs;milk,100,ml;butter,15,g",
                "1. Whisk eggs and milk.\n2. Dip bread slices.\n3. Fry in butter until golden on both sides.");
        seed(db, "Onion Omelette", "egg,3,pcs;onion,1,pcs;oil,10,ml;salt,1,g",
                "1. Fry sliced onion until soft.\n2. Add beaten eggs and salt.\n3. Cook until set and fold.");
        seed(db, "Buttered Pasta", "pasta,200,g;butter,30,g;cheese,30,g;salt,2,g",
                "1. Boil pasta.\n2. Toss with butter, grated cheese and salt.");
        seed(db, "Chicken and Rice", "chicken,300,g;rice,250,g;onion,1,pcs;oil,20,ml;salt,2,g",
                "1. Brown chicken pieces and onion in oil.\n2. Add rice, salt and water.\n3. Cover and cook 20 minutes.");
        seed(db, "Carrot and Potato Stew", "carrot,3,pcs;potato,3,pcs;onion,1,pcs;water,500,ml;salt,2,g",
                "1. Fry onion.\n2. Add chopped carrot, potato, water and salt.\n3. Simmer 25 minutes.");
    }
}
