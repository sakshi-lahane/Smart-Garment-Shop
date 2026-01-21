package com.clg.smart_garment_shop;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Product_List extends AppCompatActivity {

    RecyclerView rv;
    EditText etSearch;
    List<ProductModel> list = new ArrayList<>();
    List<ProductModel> fullList = new ArrayList<>();
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        rv = findViewById(R.id.rvProducts);
        etSearch = findViewById(R.id.etSearch);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductAdapter(this, list);
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
    }

    private void loadProducts() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("products")
                .get()
                .addOnSuccessListener(query -> {
                    list.clear();
                    fullList.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        ProductModel p = doc.toObject(ProductModel.class);
                        p.setProductId(doc.getId()); // important

                        // Fallback for old data
                        if (p.getTimestamp() == 0) {
                            p.setTimestamp(System.currentTimeMillis());
                        }

                        list.add(p);
                        fullList.add(p);
                    }

                    adapter.notifyDataSetChanged(); // VERY IMPORTANT
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
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

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
}
