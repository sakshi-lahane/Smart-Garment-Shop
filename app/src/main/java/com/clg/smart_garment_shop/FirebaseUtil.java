package com.clg.smart_garment_shop;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUtil {

    public static FirebaseFirestore getFirestore() {
        return FirebaseFirestore.getInstance();
    }

    public static FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }
}
