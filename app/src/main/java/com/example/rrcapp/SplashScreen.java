package com.example.rrcapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {
    private static final long SPLASH_TIME_OUT = 1000;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;
    String userLevel = "";

    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);


        boolean isLoggedInBefore = sharedPreferences.getBoolean("isLoggedIn", false);
        if (isLoggedInBefore) {
            navigateToAppropriateScreen(userLevel,username);
        } else {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Check if user is a member
                        DocumentReference memberRef = db.collection("members").document(userEmail);
                        memberRef.get().addOnCompleteListener(memberTask -> {
                            if (memberTask.isSuccessful()) {
                                DocumentSnapshot memberDocument = memberTask.getResult();
                                if (memberDocument.exists()) {
                                    String isMember = memberDocument.getString("ismember");
                                    if (isMember != null) {
                                        userLevel = "member";
                                        username = memberDocument.getString("username");
                                        navigateToAppropriateScreen(userLevel, username);
                                    } else {
                                        startActivity(new Intent(SplashScreen.this, UsersLogin.class));
                                        finish();
                                    }
                                } else {
                                    checkIfAdmin(userEmail);
                                }
                            } else {
                                startActivity(new Intent(SplashScreen.this, UsersLogin.class));
                                finish();
                            }
                        });
                    }
                }, SPLASH_TIME_OUT);
            } else {
                startActivity(new Intent(SplashScreen.this, UsersLogin.class));
                finish();
            }
        }
    }

    private void checkIfAdmin(String userEmail) {
        // Check if user is an admin
        DocumentReference adminRef = db.collection("users").document(userEmail);
        adminRef.get().addOnCompleteListener(adminTask -> {
            if (adminTask.isSuccessful()) {
                DocumentSnapshot adminDocument = adminTask.getResult();
                if (adminDocument.exists()) {
                    String isAdmin = adminDocument.getString("isadmin");
                    if (isAdmin != null) {
                        userLevel = "admin";
                         username = adminDocument.getString("username");
                        navigateToAppropriateScreen(userLevel, username);
                    } else {
                        checkIfUser(userEmail);
                    }
                } else {

                    checkIfUser(userEmail);
                }
            } else {
                startActivity(new Intent(SplashScreen.this, UsersLogin.class));
                finish();
            }
        });
    }

    private void checkIfUser(String userEmail) {
        // Check if user is a regular user
        DocumentReference userRef = db.collection("users").document(userEmail);
        userRef.get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                DocumentSnapshot userDocument = userTask.getResult();
                if (userDocument.exists()) {
                    String isUser = userDocument.getString("isuser");
                    if (isUser != null&&isUser.equals("user")) {
                        userLevel = "user";
                         username = userDocument.getString("username");
                        navigateToAppropriateScreen(userLevel, username);
                    } else {

                       checkIfUser(userEmail);
                    }
                } else {

                    startActivity(new Intent(SplashScreen.this, UsersLogin.class));
                    finish();
                }
            } else {

                startActivity(new Intent(SplashScreen.this, UsersLogin.class));
                finish();
            }
        });
    }

    private void navigateToAppropriateScreen(String userLevel, String username) {

        Intent intent;
        switch (userLevel) {
            case "admin":
                intent = new Intent(SplashScreen.this, AdminHome.class);
                break;
            case "member":
                intent = new Intent(SplashScreen.this, MembersHome.class);
                break;
            case "user":
                intent = new Intent(SplashScreen.this, UserHome.class);
                break;
            default:
                // Handle the case when the user's level is unknown or not recognized
                // For example, you can redirect the user to the login screen
                intent = new Intent(SplashScreen.this, UsersLogin.class);
                break;
        }
        // Pass the username as an extra to the intent
        intent.putExtra("username", username);
        startActivity(intent);
        finish();
    }

}
