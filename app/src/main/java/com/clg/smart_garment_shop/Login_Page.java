package com.clg.smart_garment_shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Login_Page extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvForgetPassword, tvSignUp;
    private CircularProgressIndicator progressLogin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        // If already logged in, go directly to MainActivity
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login_page);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgetPassword = findViewById(R.id.tvForgotPassword);
        tvSignUp = findViewById(R.id.tvSignup);
        progressLogin = findViewById(R.id.progressLogin); // NEW

        btnLogin.setOnClickListener(v -> loginUser());

        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, Register_Page.class)));

        tvForgetPassword.setOnClickListener(v ->
                startActivity(new Intent(this, Forget_Password.class)));
    }

    private void loginUser() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!isValidLogin(email, password)) return;

        // Show loader
        progressLogin.setVisibility(View.VISIBLE);
        btnLogin.setText("");
        btnLogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    // Hide loader
                    progressLogin.setVisibility(View.GONE);
                    btnLogin.setText("Login");
                    btnLogin.setEnabled(true);


                    if (task.isSuccessful()) {

                        Toast.makeText(this,
                                "Login Successful",
                                Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        finish();

                    } else {

                        Toast.makeText(this,
                                "Invalid email or password",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean isValidLogin(String email, String password) {

        if (email.isEmpty()) {
            etEmail.setError("Email required");
            etEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            etEmail.requestFocus();
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

        return true;
    }
}
