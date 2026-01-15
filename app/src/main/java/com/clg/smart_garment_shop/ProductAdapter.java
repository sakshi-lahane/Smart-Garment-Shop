package com.clg.smart_garment_shop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    Context context;
    List<ProductModel> list; // ✅ Correct type

    public ProductAdapter(Context context, List<ProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        ProductModel p = list.get(i);

        h.tvName.setText(p.getProductName());
        h.tvDetails.setText(p.getCategory() + " | " + p.getSize() + " | ₹" + p.getPrice());

        h.btnDelete.setOnClickListener(v -> deleteProduct(p.getProductId()));

        h.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, Edit_Product.class);
            intent.putExtra("productId", p.getProductId());
            context.startActivity(intent);
        });
    }

    private void deleteProduct(String productId) {
        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null) return;

        new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", (d, w) -> {
                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(uid)
                            .collection("products")
                            .document(productId)
                            .delete();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDetails;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvName);
            tvDetails = v.findViewById(R.id.tvDetails);
            btnEdit = v.findViewById(R.id.btnEdit);
            btnDelete = v.findViewById(R.id.btnDelete);
        }
    }
}
