package com.clg.smart_garment_shop;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StockFragment extends Fragment {

    RecyclerView rv;
    EditText etSearch;
    List<ProductModel> list = new ArrayList<>();
    List<ProductModel> fullList = new ArrayList<>();
    ProductAdapter adapter;

    public StockFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_stock, container, false);

        rv = view.findViewById(R.id.rvProducts);
        etSearch = view.findViewById(R.id.etSearch);

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ProductAdapter(requireContext(), list);
        rv.setAdapter(adapter);

        loadProducts();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        return view;
    }

    private void loadProducts() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("products")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(query -> {
                    list.clear();
                    fullList.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        try {
                            ProductModel p = doc.toObject(ProductModel.class);
                            p.setProductId(doc.getId());

                            if (p.getTimestamp() == 0) {
                                p.setTimestamp(System.currentTimeMillis());
                            }

                            list.add(p);
                            fullList.add(p);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    private void filter(String text) {
        list.clear();
        for (ProductModel item : fullList) {
            if (item.getProductName() != null &&
                    item.getProductName().toLowerCase().contains(text.toLowerCase())) {
                list.add(item);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
