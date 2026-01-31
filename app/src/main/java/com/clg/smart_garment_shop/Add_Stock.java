package com.clg.smart_garment_shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Add_Stock extends AppCompatActivity {

    private TextInputEditText etProductName, etColor, etPrice, etQuantity;
    private MaterialButton btnAddStock, btnCancel;

    private MaterialAutoCompleteTextView spCategory, spSubCategory, spSize;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private static final String TAG = "AddStockDebug";

    private ArrayAdapter<String> categoryAdapter, subCategoryAdapter, sizeAdapter;

    private final Map<String, String[]> sizeMap = new HashMap<>();
    private final Map<String, String[]> subCategoryMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stock);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        initViews();
        setupDropdowns();

        btnAddStock.setOnClickListener(v -> addStock());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etProductName = findViewById(R.id.etProductName);
        spCategory = findViewById(R.id.spCategory);
        spSubCategory = findViewById(R.id.spSubCategory);
        spSize = findViewById(R.id.spSize);
        etColor = findViewById(R.id.etColor);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);

        btnAddStock = findViewById(R.id.btn_add_stock);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupDropdowns() {

        String[] categories = {"Men", "Women", "Kids", "Footwear", "Accessories"};

        categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        spCategory.setAdapter(categoryAdapter);

        subCategoryMap.put("Men", new String[]{"Shirts", "T-Shirts", "Jeans", "Trousers", "Jackets"});
        subCategoryMap.put("Women", new String[]{"Kurtis", "Sarees", "Dresses", "Tops"});
        subCategoryMap.put("Kids", new String[]{"Frocks", "T-Shirts", "Shorts", "Sets"});
        subCategoryMap.put("Footwear", new String[]{"Shoes", "Sandals", "Slippers"});
        subCategoryMap.put("Accessories", new String[]{"Belts", "Caps", "Wallets", "Bags"});

        sizeMap.put("Men", new String[]{"S", "M", "L", "XL", "XXL"});
        sizeMap.put("Women", new String[]{"S", "M", "L", "XL"});
        sizeMap.put("Kids", new String[]{"1Y", "2Y", "3Y", "4Y", "5Y"});
        sizeMap.put("Footwear", new String[]{"5", "6", "7", "8", "9", "10"});
        sizeMap.put("Accessories", new String[]{"Free Size"});

        spCategory.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = spCategory.getText().toString();

            if (subCategoryMap.containsKey(selectedCategory)) {
                subCategoryAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line,
                        subCategoryMap.get(selectedCategory));
                spSubCategory.setAdapter(subCategoryAdapter);
                spSubCategory.setText("");
            }

            if (sizeMap.containsKey(selectedCategory)) {
                sizeAdapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_dropdown_item_1line,
                        sizeMap.get(selectedCategory));
                spSize.setAdapter(sizeAdapter);
                spSize.setText("");
            }
        });
    }

    private void addStock() {

        Log.d(TAG, "Add stock button clicked");

        if (auth.getCurrentUser() == null) {
            Log.e(TAG, "User is null");
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        String productName = etProductName.getText().toString().trim();
        String category = spCategory.getText().toString().trim();
        String subCategory = spSubCategory.getText().toString().trim();
        String size = spSize.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();

        if (productName.isEmpty() || category.isEmpty() || size.isEmpty()
                || priceStr.isEmpty() || qtyStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        int quantity;

        try {
            price = Double.parseDouble(priceStr);
            quantity = Integer.parseInt(qtyStr);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid price or quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        String productId = UUID.randomUUID().toString();
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> productMap = new HashMap<>();
        productMap.put("productId", productId);
        productMap.put("productName", productName);
        productMap.put("category", category);
        productMap.put("subCategory", subCategory);
        productMap.put("size", size);
        productMap.put("color", color);
        productMap.put("price", price);
        productMap.put("quantity", quantity);

            //  Correct Date & Time
        productMap.put("timestamp", System.currentTimeMillis());

        db.collection("users")
                .document(userId)
                .collection("products")
                .document(productId)
                .set(productMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Stock added successfully", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(Add_Stock.this, Product_List.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
