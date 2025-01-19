package com.example.rrcapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UsersLogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;

    TextView registertxt;

    ProgressBar progressBar;

String username;
    ImageButton imageButton;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_USERNAME = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_login);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginbtn);
        registertxt= findViewById(R.id.registertext);
        progressBar= findViewById(R.id.progressbar);

        imageButton=findViewById(R.id.memberlogin);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUsername = prefs.getString(KEY_USERNAME, "");
        String savedPassword = prefs.getString(KEY_PASSWORD, "");
        emailEditText.setText(savedUsername);
        passwordEditText.setText(savedPassword);



        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersLogin.this,memberlogin.class);
                startActivity(intent);
            }
        });


        registertxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsersLogin.this,Register.class);
                startActivity(intent);
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    emailEditText.setError("Email is required");
                    emailEditText.requestFocus();
                    return;
                } else if (!email.endsWith("@gmail.com")) {
                    progressBar.setVisibility(View.GONE);
                    emailEditText.setError("enter correct email address");
                } else if (email.split("@").length != 2) {
                    progressBar.setVisibility(View.GONE);
                    emailEditText.setError("Please enter a valid email address");
                } else if (password.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    passwordEditText.setError("Password is required");
                    passwordEditText.requestFocus();
                    return;
                } else {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(UsersLogin.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String currentusermail = user.getEmail();


                                        FirebaseFirestore db = FirebaseFirestore.getInstance();


                                        DocumentReference userRef = db.collection("users").document(currentusermail);


                                        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {

                                                    String isUser = documentSnapshot.getString("isuser");
                                                    String isAdmin = documentSnapshot.getString("isadmin");


                                                    if (isAdmin != null) {
                                                        username = documentSnapshot.getString("username");
                                                        Intent intent = new Intent(UsersLogin.this, AdminHome.class);
                                                        intent.putExtra("username", username);
                                                        startActivity(intent);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        finish();
                                                    } else if (isUser != null) {
                                                        username = documentSnapshot.getString("username");
                                                        Intent intent = new Intent(UsersLogin.this, UserHome.class);
                                                        intent.putExtra("username", username);
                                                        startActivity(intent);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                                                        finish();
                                                    } else {

                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(UsersLogin.this, "Unknown user type", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {

                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(UsersLogin.this, "User data not found", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);

                                                Toast.makeText(UsersLogin.this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                        Toast.makeText(UsersLogin.this, "Login successful.", Toast.LENGTH_SHORT).show();

                                    } else {

                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(UsersLogin.this, "Authentication failed. " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
    protected void onPause() {
        super.onPause();


        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_USERNAME, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }



}
