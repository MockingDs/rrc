package com.example.rrcapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MembersRetrieveforUsers extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MemberAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_retrievefor_users);

        recyclerView = findViewById(R.id.membersrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MemberAdapter();
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        retrieveMembers();
    }

    private void retrieveMembers() {
        db.collection("members")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Membersmodel> members = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Membersmodel member = document.toObject(Membersmodel.class);
                        members.add(member);
                    }
                    adapter.setMembers(members);
                })
                .addOnFailureListener(e -> {

                });
    }
}


