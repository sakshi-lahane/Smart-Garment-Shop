package com.clg.smart_garment_shop;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    RecyclerView rvHistory;
    HistoryAdapter adapter;
    List<HistoryBillModel> list = new ArrayList<>();
    List<HistoryBillModel> filteredList = new ArrayList<>();

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    EditText etSearch;
    TextView tvEmpty, tvTotalSales;

    public HistoryFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        rvHistory = view.findViewById(R.id.rvHistory);
        etSearch = view.findViewById(R.id.etSearch);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvTotalSales = view.findViewById(R.id.tvTotalSales);

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new HistoryAdapter(getContext(), filteredList);
        rvHistory.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadBills();
        setupSearch();

        return view;
    }

    private void loadBills() {
        String uid = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(uid)
                .collection("bills")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    list.clear();
                    filteredList.clear();

                    double totalSales = 0;

                    for (com.google.firebase.firestore.QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        HistoryBillModel model = doc.toObject(HistoryBillModel.class);
                        model.billId = doc.getId(); // important for invoice click
                        list.add(model);

                        if (model.finalTotal != null) {
                            totalSales += model.finalTotal;
                        }
                    }

                    filteredList.addAll(list);
                    adapter.notifyDataSetChanged();

                    tvTotalSales.setText("Total Sales: ₹" + totalSales);

                    if (list.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvHistory.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        rvHistory.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        filteredList.clear();

        for (HistoryBillModel item : list) {
            if (item.customerName != null &&
                    item.customerName.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.notifyDataSetChanged();

        if (filteredList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
        }
    }
}
