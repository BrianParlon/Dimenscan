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

public class BedEntry extends AppCompatActivity implements View.OnClickListener {
    Button  submit, reset, test;
    EditText length, height, width;

    private FirebaseUser mUser;
    private String userId;
    private FirebaseAuth mAuth;
    private String onlineUserId;
    private DatabaseReference reference;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_entry);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserId = mUser.getUid();

        this.context = this;

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("bed_dimensions").child(onlineUserId);
        userId = mUser.getUid();

        test = (Button) findViewById(R.id.findBeds);
        test.setOnClickListener(this);

        reset = (Button) findViewById(R.id.bResetBtn);
        reset.setOnClickListener(this);

        //Users input for their measurements
        length = (EditText) findViewById(R.id.bLength);
        height = (EditText) findViewById(R.id.bHeight);
        width = (EditText) findViewById(R.id.bWidth);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.findBeds:
                searching(getApplicationContext());
                Toast.makeText(BedEntry.this,"Testing Online",Toast.LENGTH_LONG).show();
                break;

            case R.id.bResetBtn:
                resetDimensions();
                Toast.makeText(BedEntry.this,"Dimensions reset",Toast.LENGTH_LONG).show();
                break;


        }
    }
    private void resetDimensions() {
        TextView textDepth = findViewById(R.id.editTextTextPersonName7);
        TextView textHeight = findViewById(R.id.editTextTextPersonName11);
        TextView textWidth = findViewById(R.id.editTextTextPersonName12);

        textDepth.setText("");
        textHeight.setText("");
        textWidth.setText("");
    }
    private void searching(Context context) {

        TextView textDepth = findViewById(R.id.bLength);
        TextView textHeight = findViewById(R.id.bHeight);
        TextView textWidth = findViewById(R.id.bWidth);

        String depthVal = textDepth.getText().toString().trim();
        String heightVal = textHeight.getText().toString().trim();
        String widthVal = textWidth.getText().toString().trim();

        if(depthVal.isEmpty()){
            textDepth.setError("Depth must be set!");
            textDepth.requestFocus();
            return;
        }
        if(heightVal.isEmpty()){
            textHeight.setError("Height must be set!");
            textDepth.requestFocus();
        return;
        }

        if(widthVal.isEmpty()){
            textWidth.setError("Width must be set!");
            textWidth.requestFocus();
        return;
        }


        int hUserInput = Integer.parseInt(textHeight.getText().toString().trim());

        String height = String.valueOf(hUserInput);
        StringBuilder sbh = new StringBuilder();
        sbh.append(height);

        while (hUserInput > 60) {

            //System.out.println(userInput);
            hUserInput--;
            sbh.append("," + hUserInput);
//            System.out.println(sbh.toString());
            height = sbh.toString();
        }
        int dUserInput = Integer.parseInt(textDepth.getText().toString().trim());

        String depth = String.valueOf(dUserInput);
        StringBuilder sbl = new StringBuilder();
        sbl.append(depth);

        while (dUserInput > 48) {

            //System.out.println(userInput);
            dUserInput--;
            sbl.append("," + dUserInput);
//            System.out.println(sbl.toString());
            depth = sbl.toString();
        }
        int wUserInput = Integer.parseInt(textWidth.getText().toString().trim());
        ;
        String width = String.valueOf(wUserInput);
        StringBuilder sbw = new StringBuilder();
        sbw.append(width);

        while (wUserInput > 60) {

            //System.out.println(userInput);
            wUserInput--;
            sbw.append("," + wUserInput);
            //System.out.println(sbw.toString());
            width = sbw.toString();
        }
        //System.out.println("https://flanagans.ie/collections/furniture/bedroom/beds/?pa_width-cm=" + width + "&pa_depth-cm=" + depth + "&pa_height-cm=" + height+"&instock_products=in");

        // goLink("https://flanagans.ie/collections/furniture/study/office-desks/?pa_width-cm=" + width + "&pa_depth-cm=" + depth + "&pa_height-cm=" + height);

        Intent bedEntry  = new Intent(context, BedListing.class);
        bedEntry.putExtra("width", width);
        bedEntry.putExtra("depth", depth);
        bedEntry.putExtra("height", height);
        bedEntry.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(bedEntry);

    }



    private void saveDimensions(){
        //Users input for their measurements
        length = (EditText) findViewById(R.id.bLength);
        height = (EditText) findViewById(R.id.bHeight);
        width = (EditText) findViewById(R.id.bWidth);

        String leng = length.getText().toString().trim();
        String hght = height.getText().toString().trim();
        String wth = width.getText().toString().trim();
        String id = reference.push().getKey();





        if (leng.isEmpty()) {
            length.setError("LENGTH REQUIRED");
            length.requestFocus();
            return;
        }
        if (hght.isEmpty()) {
            height.setError("HEIGHT REQUIRED");
            height.requestFocus();
            return;
        }
        if (wth.isEmpty()) {
            width.setError("WIDTH REQUIRED");
            width.requestFocus();
            return;

        }

        Dimension dim = new Dimension(leng,hght,wth);
        reference.child(id).setValue(dim).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(BedEntry.this, "Dimensions have been added to your profile", Toast.LENGTH_SHORT).show();

            }
        });

    }

    }


