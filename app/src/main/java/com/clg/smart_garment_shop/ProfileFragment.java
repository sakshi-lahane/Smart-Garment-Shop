package com.clg.smart_garment_shop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {

    TextView tvName, tvEmail, tvPhone, tvShopName, tvShopAddress, tvCity, tvState, tvBusinessType;
    Button btnEditProfile, btnLogout;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // If user not logged in, go to Login
        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), Login_Page.class));
            requireActivity().finish();
            return view;
        }

        // Initialize views
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPhone = view.findViewById(R.id.tvPhone);
        tvShopName = view.findViewById(R.id.tvShopName);
        tvShopAddress = view.findViewById(R.id.tvShopAddress);
        tvCity = view.findViewById(R.id.tvCity);
        tvState = view.findViewById(R.id.tvState);
        tvBusinessType = view.findViewById(R.id.tvBusinessType);

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnLogout = view.findViewById(R.id.btnLogout);   // 🔥 FIXED

        loadProfileData();

        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), Edit_Profile.class)));

        btnLogout.setOnClickListener(v -> showLogoutDialog());   // 🔥 FIXED

        return view;   // 🔥 FIXED
    }

    @Override
    public void onResume() {
        super.onResume();
        if (auth.getCurrentUser() != null) {
            loadProfileData();
        }
    }

    // 🔥 LOGOUT CONFIRMATION DIALOG
    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    auth.signOut();
                    Intent i = new Intent(getActivity(), Login_Page.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void loadProfileData() {
        String uid = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {

                        Log.d("PROFILE_DATA", doc.getData().toString());

                        tvName.setText(getSafe(doc.getString("ownerName")));
                        tvEmail.setText(getSafe(doc.getString("email")));
                        tvPhone.setText(getSafe(doc.getString("mobile")));
                        tvShopName.setText(getSafe(doc.getString("shopName")));
                        tvShopAddress.setText(getSafe(doc.getString("shopAddress")));
                        tvCity.setText(getSafe(doc.getString("city")));
                        tvState.setText(getSafe(doc.getString("state")));
                        tvBusinessType.setText(getSafe(doc.getString("businessType")));

                    } else {
                        Toast.makeText(getContext(), "Profile not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private String getSafe(String s) {
        return s == null ? "" : s;
    }
}
