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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminAttendanceView extends AppCompatActivity {

    private TableLayout tableLayout;
    int  totalClasses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance_view);

        tableLayout = findViewById(R.id.tableLayout);


        fetchAttendanceData();
    }

    private void fetchAttendanceData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();



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
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {


                            int totalPresent = 0;


                            TableRow headingsRow = new TableRow(AdminAttendanceView.this);
                          /*  String[] headings = {"Username", "Present", "Absent", "Total Classes", "Percentage"};
                            for (String heading : headings) {
                                TextView headingTextView = new TextView(AdminAttendanceView.this);
                                headingTextView.setText(heading);
                                headingTextView.setPadding(8, 8, 8, 8);
                                headingsRow.addView(headingTextView);
                            }*/
                            //tableLayout.addView(headingsRow);


                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String username = document.getString("username");
                                int present = document.getLong("present").intValue();
                                int absent = document.getLong("absent").intValue();






                                double percentage = ((double) present / totalClasses) * 100;


                                TableRow dataRow = new TableRow(AdminAttendanceView.this);
                                TextView usernameTextView = new TextView(AdminAttendanceView.this);
                                usernameTextView.setText(username);
                                usernameTextView.setPadding(8, 8, 8, 8);
                                dataRow.addView(usernameTextView);

                                TextView presentTextView = new TextView(AdminAttendanceView.this);
                                presentTextView.setText(String.valueOf(present));
                                presentTextView.setPadding(8, 8, 8, 8);
                                dataRow.addView(presentTextView);

                                TextView absentTextView = new TextView(AdminAttendanceView.this);
                                absentTextView.setText(String.valueOf(absent));
                                absentTextView.setPadding(8, 8, 8, 8);
                                dataRow.addView(absentTextView);

                                TextView totalClassesTextView = new TextView(AdminAttendanceView.this);
                                totalClassesTextView.setText(String.valueOf(totalClasses));
                                totalClassesTextView.setPadding(8, 8, 8, 8);
                                dataRow.addView(totalClassesTextView);

                                TextView percentageTextView = new TextView(AdminAttendanceView.this);
                                percentageTextView.setText(String.format("%.2f", percentage) + "%");
                                percentageTextView.setPadding(8, 8, 8, 8);
                                dataRow.addView(percentageTextView);


                                tableLayout.addView(dataRow);
                            }
                        } else {
                            Toast.makeText(AdminAttendanceView.this, "Error getting attendance data: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
