package com.example.dimenscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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

public class BedroomEntry extends AppCompatActivity implements View.OnClickListener {
    Button  submit, reset, test;
    EditText length, height, width;

    private String roomName="bedroom";
    private String bedUrl="empty";


    private FirebaseUser mUser;
    private String userId;
    private FirebaseAuth mAuth;
    private String onlineUserId;
    private DatabaseReference reference;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bedroom_entry);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserId = mUser.getUid();

        this.context = this;
        submit =(Button)findViewById(R.id.findBeds);
        submit.setOnClickListener(this);

        reset =(Button)findViewById(R.id.bResetBtn);
        reset.setOnClickListener(this);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("bed_dimensions").child(onlineUserId);
        userId = mUser.getUid();



        //Users input for their measurements
        length = (EditText) findViewById(R.id.bLength);

        width = (EditText) findViewById(R.id.bWidth);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.findBeds:
                searching(getApplicationContext());
                Toast.makeText(BedroomEntry.this,"Viewing in Room",Toast.LENGTH_LONG).show();
                break;

            case R.id.bResetBtn:
                resetDimensions();
                Toast.makeText(BedroomEntry.this,"Dimensions reset",Toast.LENGTH_LONG).show();
                break;


        }
    }
    private void resetDimensions() {
        TextView textDepth = findViewById(R.id.bLength);
        TextView textWidth = findViewById(R.id.bWidth);

        textDepth.setText("");
        textWidth.setText("");
    }
    private void searching(Context context) {

        TextView textDepth = findViewById(R.id.bLength);
        TextView textWidth = findViewById(R.id.bWidth);

        String depth = textDepth.getText().toString().trim();
        String width = textWidth.getText().toString().trim();
        String deskPrice,deskTitle,deskWidth,deskHeight;

        Intent bedIntent = getIntent();
        deskWidth = bedIntent.getStringExtra("width");
        deskHeight= bedIntent.getStringExtra("depth");
        deskPrice = bedIntent.getStringExtra("price");
        deskTitle = bedIntent.getStringExtra("title");



        Intent roomEntry  = new Intent(context, ViewBed.class);
        roomEntry.putExtra("rWidth", width);
        roomEntry.putExtra("rDepth", depth);
        roomEntry.putExtra("bWidth",deskWidth);
        roomEntry.putExtra("bHeight",deskHeight);
        roomEntry.putExtra("bPrice",deskPrice);
        roomEntry.putExtra("bTitle",deskTitle);

        roomEntry.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(roomEntry);

    }


}


