package com.example.rrcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText editTextEmail, editTextUsername, editTextPassword, editTextPhoneNumber;
    private Button buttonSubmit;
    public ProgressBar progressBar;
    private FirebaseAuth mAuth;
    TextView logintxt;

String sessionusername;
    private FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        progressBar = findViewById(R.id.progressbar);
        logintxt = findViewById(R.id.logintext);

        logintxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, UsersLogin.class);
                startActivity(intent);
            }
        });


        buttonSubmit.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String username = editTextUsername.getText().toString().trim();
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                String isuser = "user";
                progressBar.setVisibility(View.VISIBLE);

                if (email.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    editTextEmail.setError("Email is required");
                    editTextEmail.requestFocus();
                    return;
                }
                 if (!email.endsWith("@gmail.com")) {
                    progressBar.setVisibility(View.GONE);
                    editTextEmail.setError("enter correct email address");
                }
                 if (email.split("@").length != 2) {
                    progressBar.setVisibility(View.GONE);
                    editTextEmail.setError("Please enter a valid email address");
                }

                if (password.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                    return;
                }
                if (username.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    editTextUsername.setError("Password is required");
                    editTextUsername.requestFocus();
                    return;
                }
                if (phoneNumber.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    editTextPhoneNumber.setError("Password is required");
                    editTextPhoneNumber.requestFocus();
                    return;
                }
               /* firestore.collection("users")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {

                                       progressBar.setVisibility(View.GONE);
                                        editTextUsername.setError("Username already exists");
                                        editTextUsername.requestFocus();
                                    } else {*/

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                Toast.makeText(Register.this, "Account created", Toast.LENGTH_LONG).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                insertuserdetails(email,username,phoneNumber,isuser);
                               /* if (user != null) {
                                    // Add user data to Firestore
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("email", user.getEmail());
                                    userData.put("username", username);
                                    userData.put("phoneNumber", phoneNumber);
                                    userData.put("isuser", isuser);


                                    firestore.collection("users")
                                            .document(user.getEmail())
                                            .set(userData)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(Register.this, "User data added to Firestore", Toast.LENGTH_SHORT).show();
                                                    Intent in = new Intent(Register.this, UserHome.class);
                                                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(in);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(Register.this, "Failed to add user data to Firestore", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }*/

                                // Inside the onSuccess method of mAuth.createUserWithEmailAndPassword
                                firestore.collection("users")
                                        .document(user.getEmail())
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                if (documentSnapshot.exists()) {
                                                    sessionusername = documentSnapshot.getString("username");
                                                    // Now you have the username, you can proceed to redirect the user to UserHome
                                                    Intent in = new Intent(Register.this, UserHome.class);
                                                    in.putExtra("username", sessionusername); // Pass the username to UserHome
                                                    in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(in);
                                                } else {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(Register.this, "User document does not exist ", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(Register.this, "Failed to fetch user data  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.GONE);
                                if (e instanceof FirebaseAuthUserCollisionException) {
                                    //Toast.makeText(Register.this, "The email already exist"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    firestore.collection("members")
                                            .document(email)  // Assuming user.getEmail() is the Gmail ID
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot.exists()) {


                                                    //    Toast.makeText(Register.this, "The email exist in the collection", Toast.LENGTH_SHORT).show();


                                                    }
                                                }
                                            });
                                    insertuserdetails(email, username, phoneNumber, isuser);


                                }
                                else {

                                    Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


                                /*else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Register.this, "Error checking username existence: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });*/
            }
      //  });

    public  void  insertuserdetails(String email,String username,String phoneNumber,String isuser){

            Map<String, Object> userData = new HashMap<>();
            userData.put("email",email );
            userData.put("username", username);
            userData.put("phoneNumber", phoneNumber);
            userData.put("isuser", isuser);


            firestore.collection("users")
                    .document(email)
                    .set(userData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Register.this, "User data added ", Toast.LENGTH_SHORT).show();
                            Intent in = new Intent(Register.this, UserHome.class);
                            in.putExtra("username", username);
                            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(in);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Register.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });



    }
        }



