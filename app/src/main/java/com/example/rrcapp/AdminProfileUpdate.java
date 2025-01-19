package com.example.rrcapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminProfileUpdate extends AppCompatActivity {

    private EditText editTextUsername;
    private Button buttonUpdateProfile;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile_update);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextUsername = findViewById(R.id.editTextUsername);
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile);



        buttonUpdateProfile.setOnClickListener(view -> updateProfile());
    }

    private void updateProfile() {
        String newUsername = editTextUsername.getText().toString().trim();

        if (TextUtils.isEmpty(newUsername)) {
            editTextUsername.setError("Username is required");
            return;
        }

        String userEmail = mAuth.getCurrentUser().getEmail();
        if (userEmail != null) {
            DocumentReference userRef = db.collection("users").document(userEmail);
            userRef.update("username", newUsername)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(AdminProfileUpdate.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AdminProfileUpdate.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
