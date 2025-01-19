package com.example.rrcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class AdminUpdateMembers extends AppCompatActivity {

    EditText eventEditText;
    Button updateButton;
    String emailID;
    String username;
     DatePicker datePicker;
    String day;
    String month;
    String year;

    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_update_members);

        

        updateButton = findViewById(R.id.button);
        datePicker=findViewById(R.id.date);

        emailID = getIntent().getStringExtra("email");
        username=getIntent().getStringExtra("username");


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the present and absent counts from EditText fields


                day = String.format("%02d", datePicker.getDayOfMonth());
                month = String.format("%02d", datePicker.getMonth() + 1);
                year = String.valueOf(datePicker.getYear());
               Toast.makeText( AdminUpdateMembers.this,day+"-"+month+"-"+year,Toast.LENGTH_SHORT).show();
                selectedDate=   String.valueOf(day+"-"+month+"-"+year);



                    checkeventexist(selectedDate);
            }
        });
    }

    private void updateDocument(String selectedDate, String username) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("dates").document(selectedDate)
                .update("absentees", FieldValue.arrayRemove(username))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(AdminUpdateMembers.this," removed from absentees", Toast.LENGTH_SHORT).show();

                    }
                });
        db.collection("dates").document(selectedDate)
                .update("presents", FieldValue.arrayUnion(username))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Username added to presentees successfully
                        Toast.makeText(AdminUpdateMembers.this, username + " added to presents", Toast.LENGTH_SHORT).show();
                    }
                });

        DocumentReference docRef = db.collection("members").document(emailID);
        docRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        int existingPresentCount = documentSnapshot.getLong("present").intValue();
                        int existingAbsentCount = documentSnapshot.getLong("absent").intValue();


                        int updatedPresentCount = ++existingPresentCount;
                        int updatedabsentcount = --existingAbsentCount;


                        Map<String, Object> updates = new HashMap<>();
                        updates.put("present", updatedPresentCount);
                        updates.put("absent",updatedabsentcount);


                        docRef.update(updates)
                                .addOnSuccessListener(aVoid -> {

                                    Toast.makeText(AdminUpdateMembers.this, "Document updated successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent= new Intent(AdminUpdateMembers.this,Adminuserlist.class);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(AdminUpdateMembers.this, "Failed to update document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {

                        Toast.makeText(AdminUpdateMembers.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(AdminUpdateMembers.this, "Failed to fetch document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void updateDocumentConfirmation(String selectedDate, String username) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Member Attendance");
        builder.setMessage("Are you sure you want to update member attendance?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                updateDocument(selectedDate,username);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked No, do nothing
                dialog.dismiss();
            }
        });
        builder.show();
    }

   /* private void updateDocument(int presentCount, int absentCount) {
        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get reference to the document in Firestore
        DocumentReference docRef = db.collection("members").document(emailID);

        // Create a map to update Firestore document
        Map<String, Object> updates = new HashMap<>();
        updates.put("present", presentCount);
        updates.put("absent", absentCount);

        // Update Firestore document
        docRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Document updated successfully
                    Toast.makeText(AdminUpdateMembers.this, "Document updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(AdminUpdateMembers.this, "Failed to update document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }*/
   public void checkeventexist(final String selectedDate) {
       FirebaseFirestore db = FirebaseFirestore.getInstance();
       db.collection("dates").document(selectedDate)
               .get()
               .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                       if (task.isSuccessful()) {
                           DocumentSnapshot document = task.getResult();
                           if (document.exists()) {

                               List<?> absentees = (List<?>) document.get("absentees");
                               if ( absentees.contains(username)) {

                                   updateDocumentConfirmation(selectedDate, username);
                               }
                               else {

                                   Toast.makeText(AdminUpdateMembers.this, "this user was present", Toast.LENGTH_SHORT).show();
                               }
                           }
                           else {

                               Toast.makeText(AdminUpdateMembers.this, "attendance does not  exist on this date", Toast.LENGTH_SHORT).show();
                           }
                       }
                       else {

                           Toast.makeText(AdminUpdateMembers.this, "Failed to check event existence", Toast.LENGTH_SHORT).show();
                       }
                   }
               });
   }


}
