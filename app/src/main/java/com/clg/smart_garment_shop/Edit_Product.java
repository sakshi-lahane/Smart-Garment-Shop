package com.clg.smart_garment_shop;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Edit_Product extends AppCompatActivity {

    private TextInputEditText etName, etCategory, etSize, etColor, etPrice, etQuantity;
    private MaterialButton btnUpdate, btnCancel;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        productId = getIntent().getStringExtra("productId");

        if (productId == null) {
            Toast.makeText(this, "Invalid product", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadProductData();

        btnUpdate.setOnClickListener(v -> updateProduct());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etName = findViewById(R.id.etProductName);
        etCategory = findViewById(R.id.etCategory);
        etSize = findViewById(R.id.etSize);
        etColor = findViewById(R.id.etColor);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void loadProductData() {
        String uid = auth.getUid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("products")
                .document(productId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        etName.setText(snapshot.getString("productName"));
                        etCategory.setText(snapshot.getString("category"));
                        etSize.setText(snapshot.getString("size"));
                        etColor.setText(snapshot.getString("color"));
                        etPrice.setText(String.valueOf(snapshot.getDouble("price")));
                        etQuantity.setText(String.valueOf(snapshot.getLong("quantity")));
                    }
                });
    }

    private void updateProduct() {
        String name = etName.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String size = etSize.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();

        if (name.isEmpty() || category.isEmpty() || size.isEmpty() ||
                color.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        int qty;

        try {
            price = Double.parseDouble(priceStr);
            qty = Integer.parseInt(qtyStr);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid price or quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("productName", name);
        map.put("category", category);
        map.put("size", size);
        map.put("color", color);
        map.put("price", price);
        map.put("quantity", qty);

        String uid = auth.getUid();
        if (uid == null) return;

        db.collection("users")
                .document(uid)
                .collection("products")
                .document(productId)
                .update(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
