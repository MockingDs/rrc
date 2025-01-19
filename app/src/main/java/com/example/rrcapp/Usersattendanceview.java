package com.example.rrcapp;

import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Usersattendanceview extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    int totalClasses;

    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usersattendanceview);


        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        tableLayout = findViewById(R.id.tableLayout);


        fetchUserAttendanceDetails(currentUser.getEmail());
    }

    private void fetchUserAttendanceDetails(String userEmail) {


        DocumentReference totalClassRef = db.collection("TotalClass").document("totalclass");


        totalClassRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        totalClasses = document.getLong("totalclass").intValue();
                    }
                }
            }
        });
        db.collection("members")
                .document(userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            String username = document.getString("username");
                            int present = document.getLong("present").intValue();
                            int absent = document.getLong("absent").intValue();
                           // int totalClasses = present + absent;
                            double percentage = ((double) present / totalClasses) * 100 ;


                            TableRow dataRow = new TableRow(Usersattendanceview.this);
                            TextView usernameTextView = new TextView(Usersattendanceview.this);
                            usernameTextView.setText(username);
                            usernameTextView.setPadding(8, 8, 8, 8);
                            dataRow.addView(usernameTextView);

                            TextView presentTextView = new TextView(Usersattendanceview.this);
                            presentTextView.setText(String.valueOf(present));
                            presentTextView.setPadding(8, 8, 8, 8);
                            dataRow.addView(presentTextView);

                            TextView absentTextView = new TextView(Usersattendanceview.this);
                            absentTextView.setText(String.valueOf(absent));
                            absentTextView.setPadding(8, 8, 8, 8);
                            dataRow.addView(absentTextView);

                            TextView totalClassesTextView = new TextView(Usersattendanceview.this);
                            totalClassesTextView.setText(String.valueOf(totalClasses));
                            totalClassesTextView.setPadding(8, 8, 8, 8);
                            dataRow.addView(totalClassesTextView);

                            TextView percentageTextView = new TextView(Usersattendanceview.this);
                            percentageTextView.setText(String.format("%.2f", percentage) + "%");
                            percentageTextView.setPadding(8, 8, 8, 8);
                            dataRow.addView(percentageTextView);


                            tableLayout.addView(dataRow);
                        } else {
                            Toast.makeText(Usersattendanceview.this, "No attendance record found for the current user", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Usersattendanceview.this, "Error fetching attendance data: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
