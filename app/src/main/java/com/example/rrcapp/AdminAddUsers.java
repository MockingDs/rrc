package com.example.rrcapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rrcapp.Mdatamodel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminAddUsers extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextUsername;
    private Button addButton,clearbtn;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_users);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

       clearbtn=findViewById(R.id.clearbtn);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUsername = findViewById(R.id.edittextusername);
        addButton = findViewById(R.id.addmembers);

clearbtn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        editTextEmail.setText("");
        editTextUsername.setText("");
        editTextPassword.setText("");
    }
});

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String username = editTextUsername.getText().toString().trim();

                if (email.trim().isEmpty()){
                    editTextEmail.setError("enter emailAddress");

                }
               else if (password.trim().isEmpty()){
                    editTextPassword.setError("enter password");

                } else if (!email.endsWith("@gmail.com")) {
                    editTextEmail.setError("enter correct email address");
                }
                else if (email.split("@").length != 2) {
                    editTextEmail.setError("Please enter a valid email address");
                }

                else if (username.trim().isEmpty()){
                    editTextUsername.setError("enter username");
                }
               else {
                    showConfirmationDialog(email,username,password);

               }
            }
        });

    }
    private void checkAndAddMemberDetails(String email, String username,String password) {

        db.collection("members")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {

                                Toast.makeText(AdminAddUsers.this, "Username already exists", Toast.LENGTH_SHORT).show();
                            } else {

                                createuser(email,username,password);
                            }
                        } else {

                            Toast.makeText(AdminAddUsers.this, "Failed to check username existence", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void addMemberDetails(String email, String username){

        db.collection("members").document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        Toast.makeText(AdminAddUsers.this, "Member with this email already exists", Toast.LENGTH_SHORT).show();
                    } else {


                        db.collection("members").document(email)

                                .set(new MemberDetails(email, username, "member", 0, 0))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AdminAddUsers.this, "Member details added successfully", Toast.LENGTH_SHORT).show();
                                        editTextEmail.setText("");
                                        editTextUsername.setText("");
                                        editTextPassword.setText("");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AdminAddUsers.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
    }
    public void createuser(String email,String username,String password){


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(AdminAddUsers.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(AdminAddUsers.this, "User created successfully", Toast.LENGTH_SHORT).show();

                           addMemberDetails(email,username);

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                        if (e instanceof FirebaseAuthUserCollisionException) {

                            Toast.makeText(AdminAddUsers.this, "The email already exist", Toast.LENGTH_SHORT).show();
                            addMemberDetails(email, username); // Optionally, add member details here
                        } else {

                            Toast.makeText(AdminAddUsers.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void showConfirmationDialog(String email, String username, String password) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to add this member?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                checkAndAddMemberDetails(email, username, password);
            }
        });
        builder.setNegativeButton("No", null); // Do nothing if user clicks No
        builder.show();
    }

}
