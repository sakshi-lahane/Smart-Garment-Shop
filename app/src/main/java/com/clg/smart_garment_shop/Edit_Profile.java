package com.clg.smart_garment_shop;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Edit_Profile extends AppCompatActivity {

    EditText etName, etPhone, etShopName, etShopAddress, etCity, etState, etBusinessType;
    Button btnSave, btnCancel;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etShopName = findViewById(R.id.etShopName);
        etShopAddress = findViewById(R.id.etShopAddress);
        etCity = findViewById(R.id.etCity);
        etState = findViewById(R.id.etState);
        etBusinessType = findViewById(R.id.etBusinessType);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);


        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        loadProfile();

        btnSave.setOnClickListener(v -> saveProfile());
        btnCancel.setOnClickListener(v -> finish());

    }


    private void loadProfile() {
        String uid = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        etName.setText(doc.getString("ownerName"));
                        etPhone.setText(doc.getString("mobile"));
                        etShopName.setText(doc.getString("shopName"));
                        etShopAddress.setText(doc.getString("shopAddress"));
                        etCity.setText(doc.getString("city"));
                        etState.setText(doc.getString("state"));
                        etBusinessType.setText(doc.getString("businessType"));
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void saveProfile() {
        String uid = auth.getCurrentUser().getUid();

        String ownerName = etName.getText().toString().trim();
        String mobile = etPhone.getText().toString().trim();
        String shopName = etShopName.getText().toString().trim();
        String shopAddress = etShopAddress.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String state = etState.getText().toString().trim();
        String businessType = etBusinessType.getText().toString().trim();

        if (ownerName.isEmpty()) {
            etName.setError("Owner name required");
            etName.requestFocus();
            return;
        }

        if (mobile.isEmpty() || !mobile.matches("\\d{10}")) {
            etPhone.setError("Enter valid 10-digit mobile");
            etPhone.requestFocus();
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("ownerName", ownerName);
        map.put("mobile", mobile);
        map.put("shopName", shopName);
        map.put("shopAddress", shopAddress);
        map.put("city", city);
        map.put("state", state);
        map.put("businessType", businessType);

        firestore.collection("users")
                .document(uid)
                .update(map)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
