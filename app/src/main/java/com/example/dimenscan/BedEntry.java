package com.example.dimenscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BedEntry extends AppCompatActivity implements View.OnClickListener {
    Button conv, submit, reset, test;
    EditText length, height, width;
    TextView lengCon, heightCon, widthCon;
    private FirebaseUser mUser;
    private String userId;
    private FirebaseAuth mAuth;
    private String onlineUserId;
    private DatabaseReference reference;
    String lengConv, hghtConv, wthConv ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_entry);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserId = mUser.getUid();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        reference  = FirebaseDatabase.getInstance().getReference().child("bed_dimensions").child(onlineUserId);
        userId = mUser.getUid();

        test = (Button) findViewById(R.id.testBed);
        test.setOnClickListener(this);



        conv = (Button) findViewById(R.id.bConvBtn);
        conv.setOnClickListener(this);

        submit = (Button) findViewById(R.id.bSubmitBtn);
        submit.setOnClickListener(this);

        reset = (Button) findViewById(R.id.bResetBtn);
        reset.setOnClickListener(this);

        //Users input for their measurements
        length = (EditText) findViewById(R.id.bLength);
        height = (EditText) findViewById(R.id.bHeight);
        width = (EditText) findViewById(R.id.bWidth);

        //TextViews for conversion if needed
        lengCon = (TextView) findViewById(R.id.bLengthConv);
        heightCon = (TextView) findViewById(R.id.bHeightConv);
        widthCon = (TextView) findViewById(R.id.bWidthConv);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bConvBtn:
                Toast.makeText(BedEntry.this,"Dimensions converted",Toast.LENGTH_LONG).show();
                conversion();
                break;

            case R.id.testBed:
                Toast.makeText(BedEntry.this,"Testing Online",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, BedListing.class));
                break;

            case R.id.bResetBtn:
                Toast.makeText(BedEntry.this,"Dimensions reset",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this,BedEntry.class));
                break;


            case R.id.bSubmitBtn:
                saveDimensions();
                Toast.makeText(BedEntry.this,"Dimensions submitted",Toast.LENGTH_LONG).show();
                break;

        }
    }



    private void saveDimensions(){

        String leng = length.getText().toString().trim();
        String hght = height.getText().toString().trim();
        String wth = width.getText().toString().trim();
        String id = reference.push().getKey();

        String len = lengCon.getText().toString().trim();
        String hgt = heightCon.getText().toString().trim();
        String wh = widthCon.getText().toString().trim();


        if(lengConv== null&& wthConv ==null &&lengConv ==null ){
            Dimension dim = new Dimension(leng,hght,wth);
            reference.child(id).setValue(dim).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(BedEntry.this, "Dimensions have been added to your profile", Toast.LENGTH_SHORT).show();

                }
            });

        } else{
            Dimension dim = new Dimension(len,hgt,wh);
            reference.child(id).setValue(dim).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(BedEntry.this, "Dimensions have been added to your profile", Toast.LENGTH_SHORT).show();

                }
            });

        }

    }

    private void conversion() {

        Double leng = Double.parseDouble(length.getText().toString().trim());
        Double hght = Double.parseDouble(height.getText().toString().trim());
        Double wth = Double.parseDouble(width.getText().toString().trim());

        leng = leng * 2.54;
        hght = hght * 2.54;
        wth = wth * 2.54;

        lengConv = String.valueOf(leng);
        hghtConv = String.valueOf(hght);
        wthConv = String.valueOf(wth);

        lengCon.setText(lengConv);
        heightCon.setText(hghtConv);
        widthCon.setText(wthConv);




    }
}