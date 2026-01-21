package com.clg.smart_garment_shop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    Context context;
    List<ProductModel> list;

    public ProductAdapter(Context context, List<ProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductModel model = list.get(position);

        holder.tvName.setText(model.getProductName());
        holder.tvDetails.setText("Qty: " + model.getQuantity() + " | ₹" + model.getPrice());

        // SAFE DATE
        holder.tvDate.setText(formatDate(model.getTimestamp()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewStock.class);
            intent.putExtra("name", model.getProductName());
            intent.putExtra("category", model.getCategory());
            intent.putExtra("subcategory", model.getSubCategory());
            intent.putExtra("size", model.getSize());
            intent.putExtra("color", model.getColor());
            intent.putExtra("price", String.valueOf(model.getPrice()));
            intent.putExtra("quantity", String.valueOf(model.getQuantity()));
            context.startActivity(intent);
        });

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateStock.class);
            intent.putExtra("id", model.getProductId());
            intent.putExtra("name", model.getProductName());
            intent.putExtra("category", model.getCategory());
            intent.putExtra("subcategory", model.getSubCategory());
            intent.putExtra("size", model.getSize());
            intent.putExtra("color", model.getColor());
            intent.putExtra("price", String.valueOf(model.getPrice()));
            intent.putExtra("quantity", String.valueOf(model.getQuantity()));
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            showDeleteDialog(model.getProductId(), position);
        });
    }

    private void showDeleteDialog(String productId, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Product")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", (dialog, which) -> deleteProduct(productId, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteProduct(String productId, int position) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("products")
                .document(productId)
                .delete()
                .addOnSuccessListener(unused -> {
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails, tvDate;
        ImageView btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvDetails = itemView.findViewById(R.id.tvDetails);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    // Today / Yesterday / Date + Time
    private String formatDate(long timestamp) {
        if (timestamp <= 0) return "";

        Date date = new Date(timestamp);

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        Calendar now = Calendar.getInstance();
        Calendar inputDate = Calendar.getInstance();
        inputDate.setTime(date);

        String time = timeFormat.format(date);

        if (isSameDay(now, inputDate)) {
            return "Today, " + time;
        }

        now.add(Calendar.DAY_OF_YEAR, -1);
        if (isSameDay(now, inputDate)) {
            return "Yesterday, " + time;
        }

        return fullDateFormat.format(date) + ", " + time;
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}
