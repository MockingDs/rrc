package com.example.rrcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminMarkAtt extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AttendanceAdapter attendanceAdapter;
    private List<Attendancedata> attendanceList;

    private EditText eventname;
    String eventName;
    FirebaseFirestore db;
    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_mark_att);

        eventname= findViewById(R.id.eventname);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        attendanceList = new ArrayList<>();
        attendanceAdapter = new AttendanceAdapter(this, attendanceList);
        recyclerView.setAdapter(attendanceAdapter);

        fetchAttendanceData();

        Button saveButton = findViewById(R.id.button);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventName=eventname.getText().toString();

              if (eventName.trim().isEmpty()){
                  eventname.setError("fill eventname");
              }
              else {
                  checkEventName(eventName);
              }

            }
        });
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
         currentDate = dateFormat.format(calendar.getTime()).toString();
    }

    private void fetchAttendanceData() {
         db = FirebaseFirestore.getInstance();
        db.collection("members")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    String email = document.getString("emailID");
                                    String username = document.getString("username");
                                    String id = document.getId();
                                    int present = document.getLong("present").intValue();
                                    int absent = document.getLong("absent").intValue();

                                    Attendancedata attendanceData = new Attendancedata(email, username, present, absent);
                                    attendanceList.add(attendanceData);
                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                            }
                            attendanceAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(AdminMarkAtt.this, "Error getting attendance data: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveAttendance() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Update");
        builder.setMessage("Are you sure you want to update attendance?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                updateAttendance(eventName);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                showToast("Attendance update cancelled.");
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void updateAttendance(String eventname) {
        List<String> absentees = new ArrayList<>();
        List<String> presents = new ArrayList<>();

        List<Integer> selectedPositions = attendanceAdapter.getSelectedPositions();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference totalClassRef = db.collection("TotalClass").document("totalclass");
        totalClassRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Long totalClassCount = documentSnapshot.getLong("totalclass");
                    if (totalClassCount != null) {
                        totalClassRef.update("totalclass", totalClassCount + 1);
                    }
                }
            }
        });

        for (int position : selectedPositions) {

            String emailID = attendanceList.get(position).getEmailID();
            String username = attendanceList.get(position).getUsername();
           presents.add(username);

            DocumentReference docRef = db.collection("members").document(emailID);


            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Long presentCount = documentSnapshot.getLong("present");
                        if (presentCount != null) {
                            docRef.update("present", presentCount + 1);
                        }
                    }
                }
            });
        }

        int totalUsers = attendanceList.size();

        for (int i = 0; i < totalUsers; i++) {
            if (!selectedPositions.contains(i)) {

                String emailID = attendanceList.get(i).getEmailID();
                String username = attendanceList.get(i).getUsername();
                absentees.add(username);


                DocumentReference docRef = db.collection("members").document(emailID);


                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Long absentCount = documentSnapshot.getLong("absent");
                            if (absentCount != null) {
                                docRef.update("absent", absentCount + 1); // Increment "absent" field by 1
                            }
                        }
                    }
                });
            }
        }
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("absentees", absentees);
        eventData.put("presents",presents);
        eventData.put("eventname",eventname);

        db.collection("dates").document(currentDate)
                .set(eventData)
                .addOnSuccessListener(aVoid -> {
                    // Document updated successfully
                    showToast("Attendance updated successfully!");
                    attendanceAdapter.clearSelectedPositions();

                    // Intent back to home activity
                    Intent intent = new Intent(this, AdminHome.class);
                    startActivity(intent);
                   
                })
                .addOnFailureListener(e -> {

                    showToast("Failed to update attendance: " + e.getMessage());
                });

        showToast("Attendance updated successfully!");
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void checkEventName(String eventName) {
        db.collection("dates").document(currentDate)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                showToast("Attendance taken on this date");

                            } else {

                                saveAttendance();
                            }
                        } else {
                            showToast("Failed to check event name: " + task.getException().getMessage());
                        }
                    }
                });
    }

}
