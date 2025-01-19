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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class memberlogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
String username;
    TextView registertxt;

    ProgressBar progressBar;
    ImageButton imageButton;
    String email,password;
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_USERNAME = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memberlogin);
        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginbtn);
        registertxt= findViewById(R.id.registertext);
        progressBar= findViewById(R.id.progressbar);
        imageButton= findViewById(R.id.userlogin);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedUsername = prefs.getString(KEY_USERNAME, "");
        String savedPassword = prefs.getString(KEY_PASSWORD, "");
        emailEditText.setText(savedUsername);
        passwordEditText.setText(savedPassword);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(memberlogin.this,UsersLogin.class);
                startActivity(intent);
            }
        });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
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
                }
 else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(memberlogin.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        String usermail = user.getEmail();
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        DocumentReference docRef = db.collection("members").document(usermail);
                                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        String ismember = document.getString("ismember");
                                                        if (ismember != null) {
                                                            username = document.getString("username");
                                                            Intent intent = new Intent(memberlogin.this, MembersHome.class);
                                                            intent.putExtra("username", username);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                            finish();
                                                        } else {

                                                            Toast.makeText(memberlogin.this, "You are not a member", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(memberlogin.this, "Member data not found", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(memberlogin.this, "Failed to retrieve member data", Toast.LENGTH_SHORT).show();
                                                }
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        });
                                    } else {

                                        Toast.makeText(memberlogin.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
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