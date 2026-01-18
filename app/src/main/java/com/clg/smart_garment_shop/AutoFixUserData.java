package com.clg.smart_garment_shop;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AutoFixUserData {

    public static void fixIfMissing(Context context) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) return;

        String userId = auth.getCurrentUser().getUid();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        // User document does not exist → create it
                        Map<String, Object> data = new HashMap<>();
                        data.put("shopName", "My Shop");
                        data.put("ownerName", "Owner");
                        data.put("createdAt", System.currentTimeMillis());

                        firestore.collection("users")
                                .document(userId)
                                .set(data);
                    } else {
                        // User exists → check missing fields
                        Map<String, Object> updates = new HashMap<>();

                        if (!doc.contains("shopName")) {
                            updates.put("shopName", "My Shop");
                        }

                        if (!doc.contains("ownerName")) {
                            updates.put("ownerName", "Owner");
                        }

                        if (!updates.isEmpty()) {
                            firestore.collection("users")
                                    .document(userId)
                                    .update(updates);
                        }
                    }
                });
    }
}
