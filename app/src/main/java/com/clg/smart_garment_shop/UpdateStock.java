package com.clg.smart_garment_shop;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import android.widget.ArrayAdapter;


import java.util.HashMap;
import java.util.Map;

public class UpdateStock extends AppCompatActivity {

    private TextInputEditText etProductName, etColor, etPrice, etQuantity;
    private MaterialAutoCompleteTextView spCategory, spSubCategory, spSize;
    private MaterialButton btnUpdate, btnCancel;

    private FirebaseFirestore firestore;
    private String stockId;

    private Map<String, String[]> sizeMap = new HashMap<>();
    private Map<String, String[]> subCategoryMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_stock);

        firestore = FirebaseFirestore.getInstance();

        initViews();
        setupDropdowns();
        loadIntentData();

        btnUpdate.setOnClickListener(v -> updateStock());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void initViews() {
        etProductName = findViewById(R.id.etProductName);
        etColor = findViewById(R.id.etColor);
        etPrice = findViewById(R.id.etPrice);
        etQuantity = findViewById(R.id.etQuantity);
        spCategory = findViewById(R.id.spCategory);
        spSubCategory = findViewById(R.id.spSubCategory);
        spSize = findViewById(R.id.spSize);

        btnUpdate = findViewById(R.id.btn_add_stock);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupDropdowns() {

        String[] categories = {"Men", "Women", "Kids", "Footwear", "Accessories"};

        ArrayAdapter<String> categoryAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categories);

        spCategory.setAdapter(categoryAdapter);

        subCategoryMap.put("Men", new String[]{"Shirts", "T-Shirts", "Jeans"});
        subCategoryMap.put("Women", new String[]{"Kurtis", "Sarees"});
        subCategoryMap.put("Kids", new String[]{"Frocks", "T-Shirts"});
        subCategoryMap.put("Footwear", new String[]{"Shoes", "Sandals"});
        subCategoryMap.put("Accessories", new String[]{"Belts", "Caps"});

        sizeMap.put("Men", new String[]{"S", "M", "L", "XL"});
        sizeMap.put("Women", new String[]{"S", "M", "L"});
        sizeMap.put("Kids", new String[]{"1Y", "2Y"});
        sizeMap.put("Footwear", new String[]{"6", "7", "8"});
        sizeMap.put("Accessories", new String[]{"Free Size"});

        spCategory.setOnItemClickListener((parent, view, position, id) -> {
            String selected = spCategory.getText().toString();

            if (subCategoryMap.containsKey(selected)) {
                ArrayAdapter<String> subAdapter =
                        new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                                subCategoryMap.get(selected));
                spSubCategory.setAdapter(subAdapter);
                spSubCategory.setText("");
            }

            if (sizeMap.containsKey(selected)) {
                ArrayAdapter<String> sizeAdapter =
                        new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                                sizeMap.get(selected));
                spSize.setAdapter(sizeAdapter);
                spSize.setText("");
            }
        });
    }

    private void loadIntentData() {
        stockId = getIntent().getStringExtra("id");

        etProductName.setText(getIntent().getStringExtra("name"));
        etColor.setText(getIntent().getStringExtra("color"));
        etPrice.setText(getIntent().getStringExtra("price"));
        etQuantity.setText(getIntent().getStringExtra("quantity"));

        spCategory.setText(getIntent().getStringExtra("category"), false);
        spSubCategory.setText(getIntent().getStringExtra("subcategory"), false);
        spSize.setText(getIntent().getStringExtra("size"), false);
    }

    private void updateStock() {
        String name = etProductName.getText().toString().trim();
        String color = etColor.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String quantity = etQuantity.getText().toString().trim();
        String category = spCategory.getText().toString().trim();
        String subcategory = spSubCategory.getText().toString().trim();
        String size = spSize.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || quantity.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null || stockId == null) return;

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("productName", name);
        updateMap.put("category", category);
        updateMap.put("subCategory", subcategory);
        updateMap.put("size", size);
        updateMap.put("color", color);
        updateMap.put("price", Double.parseDouble(price));
        updateMap.put("quantity", Integer.parseInt(quantity));

        firestore.collection("users")
                .document(uid)
                .collection("products")
                .document(stockId)
                .set(updateMap, SetOptions.merge())
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}
