package com.clg.smart_garment_shop;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewStock extends AppCompatActivity {

    TextView tvName, tvCategory, tvSubCategory, tvSize, tvColor, tvPrice, tvQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stock);

        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvSubCategory = findViewById(R.id.tvSubCategory);
        tvSize = findViewById(R.id.tvSize);
        tvColor = findViewById(R.id.tvColor);
        tvPrice = findViewById(R.id.tvPrice);
        tvQuantity = findViewById(R.id.tvQuantity);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());


        tvName.setText(getIntent().getStringExtra("name"));
        tvCategory.setText(getIntent().getStringExtra("category"));
        tvSubCategory.setText(getIntent().getStringExtra("subcategory"));
        tvSize.setText(getIntent().getStringExtra("size"));
        tvColor.setText(getIntent().getStringExtra("color"));
        tvPrice.setText(getIntent().getStringExtra("price"));
        tvQuantity.setText(getIntent().getStringExtra("quantity"));
    }
}
