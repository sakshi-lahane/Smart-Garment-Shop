package com.clg.smart_garment_shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register_Page extends AppCompatActivity {

    // UI
    private TextInputEditText etShopName, etOwnerName, etEmail,
            etMobile, etPassword, etConfirmPassword;
    private MaterialButton btnCreateAccount;
    private ProgressBar progressRegister;
    private TextView tvLogin;

    // Firebase
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Auto login
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_register_page);

        // UI
        etShopName = findViewById(R.id.etShopName);
        etOwnerName = findViewById(R.id.etOwnerName);
        etEmail = findViewById(R.id.etEmail);
        etMobile = findViewById(R.id.etMobile);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        progressRegister = findViewById(R.id.progressRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnCreateAccount.setOnClickListener(v -> createAccount());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, Login_Page.class));
            finish();
        });
    }

    // ================= CREATE ACCOUNT =================

    private void createAccount() {

        String shop = etShopName.getText().toString().trim();
        String owner = etOwnerName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (!isValidForm(shop, owner, email, mobile, password, confirmPassword)) {
            return;
        }

        btnCreateAccount.setEnabled(false);
        progressRegister.setVisibility(View.VISIBLE);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    progressRegister.setVisibility(View.GONE);
                    btnCreateAccount.setEnabled(true);

                    if (task.isSuccessful()) {

                        String uid = auth.getCurrentUser().getUid();

                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("shopName", shop);
                        userMap.put("ownerName", owner);
                        userMap.put("email", email);
                        userMap.put("mobile", mobile);
                        userMap.put("createdAt", System.currentTimeMillis());

                        firestore.collection("users")
                                .document(uid)
                                .set(userMap)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(this,
                                            "Account created successfully",
                                            Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this,
                                            "Firestore error: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });

                    } else {

                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(this,
                                    "Email already registered. Please login.",
                                    Toast.LENGTH_LONG).show();

                            startActivity(new Intent(this, Login_Page.class));
                            finish();

                        } else {
                            Toast.makeText(this,
                                    "Registration failed. Try again.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // ================= VALIDATION =================

    private boolean isValidForm(String shop, String owner,
                                String email, String mobile,
                                String password, String confirmPassword) {

        if (shop.isEmpty()) {
            etShopName.setError("Shop name required");
            etShopName.requestFocus();
            return false;
        }

        if (owner.isEmpty()) {
            etOwnerName.setError("Owner name required");
            etOwnerName.requestFocus();
            return false;
        }

        if (!owner.matches("[a-zA-Z ]+")) {
            etOwnerName.setError("Only letters allowed");
            etOwnerName.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email required");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email");
            etEmail.requestFocus();
            return false;
        }

        if (mobile.isEmpty()) {
            etMobile.setError("Mobile number required");
            etMobile.requestFocus();
            return false;
        }

        if (!mobile.matches("\\d{10}")) {
            etMobile.setError("Enter 10 digit number");
            etMobile.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password required");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Minimum 6 characters");
            etPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }
}
