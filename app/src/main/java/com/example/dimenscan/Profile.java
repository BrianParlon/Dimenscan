package com.example.dimenscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    private Boolean isOpen=false;
     Button btnPicture,newProd,saved,display;
     ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        saved = (Button) findViewById(R.id.savedDimension);
        saved.setOnClickListener(this);

        newProd = (Button) findViewById(R.id.button5);
        newProd.setOnClickListener(this);


        display = (Button) findViewById(R.id.displayRoom);
        display.setOnClickListener(this);


        user = FirebaseAuth.getInstance().getCurrentUser();
        reference  = FirebaseDatabase.getInstance().getReference("users");
        userId = user.getUid();

        TextView accName =(TextView) findViewById(R.id.textView2);
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String accountName = userProfile.getAccountName();
                    accName.setText("User Logged in " +accountName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this,"Error",Toast.LENGTH_LONG).show();
            }
        });

        Button lgOut = (Button) findViewById(R.id.logout);
        lgOut.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));
                break;

            case R.id.button5:

                startActivity(new Intent(this, ObjectChoice.class));
                break;
            case R.id.savedDimension:
                startActivity(new Intent(this, RecycleView.class));
                break;

                case R.id.displayRoom:
                startActivity(new Intent(this, ViewTable.class));
                break;



        }
    }


}