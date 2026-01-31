package com.clg.smart_garment_shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;

public class HomeFragment extends Fragment {

    TextView tvTotalSales, tvTotalBills, tvTotalStock, tvLowStock;

    FirebaseFirestore firestore;
    String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvTotalSales = view.findViewById(R.id.tvTotalSales);
        tvTotalBills = view.findViewById(R.id.tvTotalBills);
        tvTotalStock = view.findViewById(R.id.tvTotalStock);
        tvLowStock   = view.findViewById(R.id.tvLowStock);

        firestore = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getUid();

        loadDashboard();

        Button btnCreateBill = view.findViewById(R.id.btnCreateBill);
        Button btnAddStock = view.findViewById(R.id.btnAddStock);

        btnCreateBill.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), Create_Bill_Form.class)));

        btnAddStock.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), Add_Stock.class)));

        return view;
    }

    private void loadDashboard() {

        // ===== BILLS + TODAY SALES =====
        firestore.collection("users")
                .document(uid)
                .collection("bills")
                .addSnapshotListener((value, error) -> {

                    if (value == null) return;

                    tvTotalBills.setText(String.valueOf(value.size()));

                    double todaySales = 0;
                    long todayStart = getTodayStart();

                    for (DocumentSnapshot d : value) {

                        Double amount = d.getDouble("totalAmount");
                        Object createdObj = d.get("createdAt");

                        long createdTime = 0;

                        if (createdObj instanceof Long) {
                            createdTime = (Long) createdObj;
                        } else if (createdObj instanceof Timestamp) {
                            createdTime = ((Timestamp) createdObj).toDate().getTime();
                        } else if (createdObj instanceof Date) {
                            createdTime = ((Date) createdObj).getTime();
                        }

                        if (amount != null && createdTime >= todayStart) {
                            todaySales += amount;
                        }
                    }

                    tvTotalSales.setText("₹ " + String.format("%.2f", todaySales));
                });

        // ===== STOCK + LOW STOCK =====
        firestore.collection("users")
                .document(uid)
                .collection("products")
                .addSnapshotListener((value, error) -> {

                    if (value == null) return;

                    tvTotalStock.setText(String.valueOf(value.size()));

                    int low = 0;

                    for (DocumentSnapshot d : value) {

                        Long qty = d.getLong("quantity");

                        if (qty != null && qty < 5)
                            low++;
                    }

                    tvLowStock.setText(String.valueOf(low));
                });
    }

    private long getTodayStart() {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }
}
