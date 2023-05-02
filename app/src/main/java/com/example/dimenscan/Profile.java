package com.example.dimenscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
     Button img,newProd,saved,display;
     ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        saved = (Button) findViewById(R.id.savedDimension);
        saved.setOnClickListener(this);

        newProd = (Button) findViewById(R.id.button5);
        newProd.setOnClickListener(this);

        img =(Button)findViewById(R.id.viewImages);
        img.setOnClickListener(this);


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
                startActivity(new Intent(this, purchasesProductView.class));
                break;

                case R.id.displayRoom:
                    startActivity(new Intent(this, RoomEnterDetails.class));

                    break;

            case R.id.viewImages:
                startActivity(new Intent(this, ImagesActivity.class));
                break;



        }
    }
    public void objectDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.room_object, null);
        // Find the EditTexts in the dialog layout
        EditText widthTxt = dialogView.findViewById(R.id.widthEntry);
        EditText heightTxt = dialogView.findViewById(R.id.heightEntry);

        AlertDialog.Builder objDialog = new AlertDialog.Builder(this);
        objDialog.setTitle("Add Room dimensions");
        objDialog.setView(dialogView);
        objDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Parse the width and height entered by the user
                double width = Double.parseDouble(widthTxt.getText().toString());
                double height = Double.parseDouble(heightTxt.getText().toString());


            }
        });
        objDialog.setNegativeButton("Cancel", null);
        objDialog.show();
    }


}