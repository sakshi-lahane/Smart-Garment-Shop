package com.clg.smart_garment_shop;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<HistoryBillModel> list;
    Context context;

    public HistoryAdapter(Context context, List<HistoryBillModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.row_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryBillModel model = list.get(position);

        holder.tvCustomerName.setText(model.customerName);
        holder.tvTotal.setText("₹" + model.finalTotal);
        holder.tvPayment.setText(model.paymentMode);

        // Format date & time
        if (model.timestamp != null) {
            Date date = model.timestamp.toDate();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

            holder.tvDate.setText(dateFormat.format(date));
            holder.tvTime.setText(timeFormat.format(date));
        }

        // Click → Open Invoice
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Invoice.class);
            intent.putExtra("billId", model.billId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCustomerName, tvDate, tvTime, tvTotal, tvPayment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvPayment = itemView.findViewById(R.id.tvPayment);
        }
    }
}
