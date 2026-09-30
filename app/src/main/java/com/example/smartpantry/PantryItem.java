package com.example.smartpantry;

/** A single ingredient the user currently has at home. */
public class PantryItem {
    public long id;
    public String name;
    public double quantity;
    public String unit;   // g, kg, ml, l, pcs
    public String expiry; // yyyy-MM-dd or empty

    public PantryItem() {}
    public PantryItem(long id, String name, double quantity, String unit, String expiry) {
        this.id = id; this.name = name; this.quantity = quantity; this.unit = unit; this.expiry = expiry;
    }
}
