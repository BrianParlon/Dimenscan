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

public class DeskEntry extends AppCompatActivity implements View.OnClickListener {
    Button submit, reset, test,online;
    EditText length, height, width;
    TextView lengCon, heightCon, widthCon;
    private FirebaseUser mUser;
    private String userId;
    private FirebaseAuth mAuth;
    private String onlineUserId;
    private DatabaseReference reference;

    private Context context;
    private String goLink;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk_entry);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserId = mUser.getUid();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("desk_dimensions").child(onlineUserId);
        userId = mUser.getUid();

        this.context = this;

        online = (Button)findViewById(R.id.filterDesks);
        online.setOnClickListener(this);

        reset = (Button) findViewById(R.id.resetBtn);
        reset.setOnClickListener(this);

        //Users input for their measurements
        length = (EditText) findViewById(R.id.editTextTextPersonName7);
        height = (EditText) findViewById(R.id.editTextTextPersonName11);
        width = (EditText) findViewById(R.id.editTextTextPersonName12);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.filterDesks:
                searching(getApplicationContext());
                Toast.makeText(DeskEntry.this, "Displaying results", Toast.LENGTH_LONG).show();
                break;

            case R.id.resetBtn:
                resetDimensions();
                Toast.makeText(DeskEntry.this, "Dimensions reset", Toast.LENGTH_LONG).show();
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

        TextView textDepth = findViewById(R.id.editTextTextPersonName7);
        TextView textHeight = findViewById(R.id.editTextTextPersonName11);
        TextView textWidth = findViewById(R.id.editTextTextPersonName12);

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

        if(depthVal.contains(".") || heightVal.contains(".") || widthVal.contains("")){
            textWidth.requestFocus();
            Toast.makeText(context, "Values must not contain a decimal", Toast.LENGTH_LONG).show();
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
          //  System.out.println(sbh.toString());
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
//            System.out.println(sbw.toString());
            width = sbw.toString();
        }

       // goLink("https://flanagans.ie/collections/furniture/study/office-desks/?pa_width-cm=" + width + "&pa_depth-cm=" + depth + "&pa_height-cm=" + height);

        Intent deskEntry  = new Intent(context, DeskListing.class);
        deskEntry.putExtra("width", width);
        deskEntry.putExtra("depth", depth);
        deskEntry.putExtra("height", height);
        deskEntry.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(deskEntry);

    }

    private void goLink(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
        goLink = s;
    }

    public String getGoLink() {
        return goLink;
    }

    private void saveDimensions() {

        //Users input for their measurements
        length = (EditText) findViewById(R.id.editTextTextPersonName7);
        height = (EditText) findViewById(R.id.editTextTextPersonName11);
        width = (EditText) findViewById(R.id.editTextTextPersonName12);


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
                Toast.makeText(DeskEntry.this, "Dimensions have been added to your profile", Toast.LENGTH_SHORT).show();

            }
        });

    }





}