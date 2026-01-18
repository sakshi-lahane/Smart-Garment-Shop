package com.clg.smart_garment_shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BillItemAdapter extends RecyclerView.Adapter<BillItemAdapter.ViewHolder> {

    public interface OnBillChangeListener {
        void onBillChanged();
    }

    private List<BillItemModel> itemList;
    private OnBillChangeListener listener;

    public BillItemAdapter(List<BillItemModel> itemList, OnBillChangeListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillItemModel item = itemList.get(position);

        holder.tvProductName.setText(item.getName());
        holder.tvSubCategory.setText(item.getSubCategory() != null ? item.getSubCategory() : "");
        holder.tvCategory.setText(item.getCategory());
        holder.tvPrice.setText("₹" + item.getPrice());
        holder.tvQty.setText(String.valueOf(item.getQuantity()));

        // ➕ PLUS
        holder.btnPlus.setOnClickListener(v -> {
            if (item.getQuantity() < item.getStockLimit()) {
                item.setQuantity(item.getQuantity() + 1);
                notifyItemChanged(position);
                listener.onBillChanged();
            }
        });

        // ➖ MINUS
        holder.btnMinus.setOnClickListener(v -> {
            int newQty = item.getQuantity() - 1;

            if (newQty <= 0) {
                itemList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, itemList.size());
                listener.onBillChanged();
            } else {
                item.setQuantity(newQty);
                notifyItemChanged(position);
                listener.onBillChanged();
            }
        });

        // ❌ REMOVE
        holder.btnRemove.setOnClickListener(v -> {
            itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());
            listener.onBillChanged();
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName, tvSubCategory, tvCategory, tvPrice, tvQty;
        ImageView btnPlus, btnMinus, btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvSubCategory = itemView.findViewById(R.id.tvSubCategory);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvQty);

            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
