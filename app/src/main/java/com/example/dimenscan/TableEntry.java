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

public class TableEntry extends AppCompatActivity implements View.OnClickListener {
    Button submit, reset, test;
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
        setContentView(R.layout.activity_table_entry);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        onlineUserId = mUser.getUid();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        reference  = FirebaseDatabase.getInstance().getReference().child("table_dimensions").child(onlineUserId);
        userId = mUser.getUid();

        this.context = this;

        test = (Button) findViewById(R.id.testTable);
        test.setOnClickListener(this);


        submit = (Button) findViewById(R.id.tSubmitBtn);
        submit.setOnClickListener(this);

        reset = (Button) findViewById(R.id.tResetBtn);
        reset.setOnClickListener(this);

        //Users input for their measurements
        length = (EditText) findViewById(R.id.tLength);
        height = (EditText) findViewById(R.id.tHeight);
        width = (EditText) findViewById(R.id.tWidth);



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.testTable:
                Toast.makeText(TableEntry.this,"Testing Online",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, TableListing.class));
                break;

            case R.id.tResetBtn:
                Toast.makeText(TableEntry.this,"Dimensions reset",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this,TableEntry.class));
                break;


            case R.id.tSubmitBtn:
                searching(getApplicationContext());

                break;

        }
    }

    private void searching(Context context) {

        TextView textDepth = findViewById(R.id.tLength);
        TextView textHeight = findViewById(R.id.tHeight);
        TextView textWidth = findViewById(R.id.tWidth);


        int hUserInput = Integer.parseInt(textHeight.getText().toString().trim());

        String height = String.valueOf(hUserInput);
        StringBuilder sbh = new StringBuilder();
        sbh.append(height);

        while (hUserInput > 60) {

            //System.out.println(userInput);
            hUserInput--;
            sbh.append("," + hUserInput);
            System.out.println(sbh.toString());
            height = sbh.toString();
        }
        int dUserInput = Integer.parseInt(textDepth.getText().toString().trim());
        ;
        String depth = String.valueOf(dUserInput);
        StringBuilder sbl = new StringBuilder();
        sbl.append(depth);

        while (dUserInput > 48) {

            //System.out.println(userInput);
            dUserInput--;
            sbl.append("," + dUserInput);
            System.out.println(sbl.toString());
            depth = sbl.toString();
        }
        int wUserInput = Integer.parseInt(textWidth.getText().toString().trim());
        ;
        String width = String.valueOf(wUserInput);
        StringBuilder sbw = new StringBuilder();
        sbl.append(width);

        while (wUserInput > 84) {

            //System.out.println(userInput);
            wUserInput--;
            sbw.append("," + wUserInput);
            System.out.println(sbw.toString());
            width = sbl.toString();
        }

        // goLink("https://flanagans.ie/collections/furniture/study/office-desks/?pa_width-cm=" + width + "&pa_depth-cm=" + depth + "&pa_height-cm=" + height);

        Intent tableEntry  = new Intent(context, TableListing.class);
        tableEntry.putExtra("width", width);
        tableEntry.putExtra("depth", depth);
        tableEntry.putExtra("height", height);
        tableEntry.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(tableEntry);

    }


    private void saveDimensions(){

        //Users input for their measurements
        length = (EditText) findViewById(R.id.tLength);
        height = (EditText) findViewById(R.id.tHeight);
        width = (EditText) findViewById(R.id.tWidth);

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
                    Toast.makeText(TableEntry.this, "Dimensions have been added to your profile", Toast.LENGTH_SHORT).show();

                }
            });

        }

}