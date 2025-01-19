package com.example.rrcapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MembersHome extends AppCompatActivity {

CardView profile;

    CardView viewatt;
    CardView logout;

    CardView memberslist;

    String sessionusername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_home);

        profile=findViewById(R.id.cardprofile);
        viewatt=findViewById(R.id.cardViewpercent);
        logout= findViewById(R.id.cardlogout);
        memberslist= findViewById(R.id.cardmemberslist);
        sessionusername = getIntent().getStringExtra("username");
        memberslist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MembersHome.this,MembersRetrieveforMembers.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MembersHome.this,"Admin only can edit profile",Toast.LENGTH_SHORT).show();
            }
        });

        viewatt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MembersHome.this,Usersattendanceview.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MembersHome.this);
                builder.setTitle("Confirm Logout");
                builder.setMessage("Are you sure you want to log out?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth auth= FirebaseAuth.getInstance();
                        auth.signOut();
                        Intent intent = new Intent(MembersHome.this, UsersLogin.class);
                        startActivity(intent);
                        finish();
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
        });



    }
}