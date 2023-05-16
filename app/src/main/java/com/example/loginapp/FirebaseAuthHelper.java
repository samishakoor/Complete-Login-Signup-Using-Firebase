package com.example.loginapp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthHelper {

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseUser currentUser = mAuth.getCurrentUser();

    public static String getCurrentUserId() {
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }

    public static String getCurrentUserEmail() {
        if (currentUser != null) {
            return currentUser.getEmail();
        } else {
            return null;
        }
    }

}
