package com.clg.smart_garment_shop;

import android.os.Bundle;
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
    List<ProductModel> list = new ArrayList<>();
    ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        rv = findViewById(R.id.rvProducts);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductAdapter(this, list);
        rv.setAdapter(adapter);

        loadProducts();
    }

    private void loadProducts() {
        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("products")
                .get()
                .addOnSuccessListener(query -> {
                    list.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        ProductModel p = doc.toObject(ProductModel.class);
                        list.add(p);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
