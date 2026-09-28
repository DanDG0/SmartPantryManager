package com.example.smartpantry;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/** Custom adapter binding pantry rows from SQLite to the RecyclerView. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.Holder> {

    public interface Listener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private final List<PantryItem> items;
    private final Listener listener;
    private final boolean highlightExpiring;

    public PantryAdapter(List<PantryItem> items, boolean highlightExpiring, Listener listener) {
        this.items = items; this.highlightExpiring = highlightExpiring; this.listener = listener;
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView name, detail; Button delete;
        Holder(View v) {
            super(v);
            name = v.findViewById(R.id.tvName);
            detail = v.findViewById(R.id.tvDetail);
            delete = v.findViewById(R.id.btnDelete);
        }
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        PantryItem p = items.get(position);
        h.name.setText(p.name);
        String expiry = (p.expiry == null || p.expiry.isEmpty()) ? "" : "  |  Expires " + p.expiry;
        h.detail.setText(trim(p.quantity) + " " + p.unit + expiry);
        h.detail.setTextColor(highlightExpiring && expiresSoon(p.expiry) ? Color.RED : Color.DKGRAY);
        h.itemView.setOnClickListener(v -> listener.onEdit(p));
        h.delete.setOnClickListener(v -> listener.onDelete(p));
    }

    @Override
    public int getItemCount() { return items.size(); }

    private static String trim(double d) {
        return d == (long) d ? String.valueOf((long) d) : String.valueOf(d);
    }

    /** True if the expiry date is within the next 3 days (or already past). */
    private static boolean expiresSoon(String expiry) {
        if (expiry == null || expiry.isEmpty()) return false;
        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(expiry);
            long days = (d.getTime() - new Date().getTime()) / (1000L * 60 * 60 * 24);
            return days <= 3;
        } catch (Exception e) { return false; }
    }
}
