package com.example.rrcapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class Adminuserlist extends AppCompatActivity implements  Madapter.OnItemClickListener{

    private RecyclerView recyclerView;
    private Madapter mAdapter;
    private List<Mdatamodel> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminuserlist);

      //  FloatingActionButton fab = findViewById(R.id.fab_confirm_selection);
     //   fab.setVisibility(View.GONE);

        recyclerView = findViewById(R.id.userlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();
        mAdapter = new Madapter(this, userList,this);
        recyclerView.setAdapter(mAdapter);

      /*  if (mAdapter.isSelectionMode()) {
            fab.setVisibility(View.VISIBLE);
        }
        else {
            fab.setVisibility(View.GONE);
        }

// OnClickListener for FAB
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Disable selection mode
                mAdapter.setSelectionMode(false);
                // Hide checkboxes
                // You may need to update the list or adapter to remove selections
                mAdapter.clearSelections();
                // Hide FAB

                fab.setVisibility(View.GONE);
            }
        });*/

        loadUsersFromFirestore();

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Adminuserlist.this, AdminAddUsers.class);
                startActivity(intent);
            }
        });

    }

    private void loadUsersFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("members")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userList.clear(); // Clear previous data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String emailId = document.getString("emailID");
                                String userName = document.getString("username");
                                Mdatamodel user = new Mdatamodel(emailId, userName);
                                userList.add(user);
                            }
                            mAdapter.notifyDataSetChanged(); // Notify adapter of data change
                        } else {
                            Toast.makeText(Adminuserlist.this, "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.deleteusermenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.userdeleteicon) {
            deleteSelectedUsers();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteSelectedUsers() {
        List<Integer> selectedPositions = mAdapter.getSelectedPositions();
        if (selectedPositions.isEmpty()) {
            Toast.makeText(this, "No users selected", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Deleting the members includes all their data. This data cannot be retrieved. Are you sure you want to delete?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                performDeleteOperation(selectedPositions); // Perform the delete operation
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void performDeleteOperation(List<Integer> selectedPositions) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        for (int position : selectedPositions) {
            Mdatamodel user = userList.get(position);
            batch.delete(db.collection("members").document(user.getEmailId()));
        }

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Adminuserlist.this, "Selected users deleted successfully", Toast.LENGTH_SHORT).show();
                    loadUsersFromFirestore();
                    Intent in = new Intent(Adminuserlist.this,AdminHome.class);
                    startActivity(in);
                } else {
                    Toast.makeText(Adminuserlist.this, "Failed to delete users: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public void onItemClick(int position) {

        Mdatamodel clickedUser = userList.get(position);


        String emailID = clickedUser.getEmailId();
        String username= clickedUser.getUserName();


        Intent intent = new Intent(this, AdminUpdateMembers.class);
        intent.putExtra("email", emailID);
        intent.putExtra("username",username );
        startActivity(intent);
    }


}
