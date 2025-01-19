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

public class AdminHome extends AppCompatActivity {



    CardView cardprofile;
    CardView cardmarkatt;
    CardView cardattview;
    CardView cardusers;
    CardView cardlogout;


  String sessionusername;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        sessionusername = getIntent().getStringExtra("username");




        cardprofile=findViewById(R.id.cardprofile);
        cardmarkatt=findViewById(R.id.cardmarkattendance);
        cardattview=findViewById(R.id.cardviewpercent);
        cardlogout=findViewById(R.id.cardlogout);
        cardusers=findViewById(R.id.carduserlist);



        cardprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(AdminHome.this,AdminProfileUpdate.class);
                startActivity(in);
            }
        });

        cardusers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHome.this, Adminuserlist.class);
                startActivity(intent);

            }
        });
        cardmarkatt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AdminHome.this,AdminMarkAtt.class);
                startActivity(in);
            }
        });
        cardattview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(AdminHome.this,AdminAttendanceView.class);
                startActivity(in);
            }
        });
        cardlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AdminHome.this);
                builder.setTitle("Confirm " +
                        "Logout");
                builder.setMessage("Are you sure you want to log out?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth auth= FirebaseAuth.getInstance();
                        auth.signOut();
                        Intent intent = new Intent(AdminHome.this, UsersLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
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