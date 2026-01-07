package com.clg.smart_garment_shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BillItemAdapter extends RecyclerView.Adapter<BillItemAdapter.ViewHolder> {

    List<BillItemModel> list;

    public BillItemAdapter(List<BillItemModel> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_bill_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        BillItemModel item = list.get(position);
        h.tvName.setText(item.name);
        h.tvQty.setText("Qty: " + item.qty);
        h.tvPrice.setText("₹" + item.total);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvPrice;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvItemName);
            tvQty = v.findViewById(R.id.tvQty);
            tvPrice = v.findViewById(R.id.tvPrice);
        }
    }
}
