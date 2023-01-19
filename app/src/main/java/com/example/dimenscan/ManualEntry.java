package com.example.dimenscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ManualEntry extends AppCompatActivity implements View.OnClickListener {
    Button conv, submit, reset;
    EditText length, height, width;
    TextView lengCon, heightCon, widthCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        conv = (Button) findViewById(R.id.convBtn);
        conv.setOnClickListener(this);

        submit = (Button) findViewById(R.id.submitBtn);
        submit.setOnClickListener(this);

        reset = (Button) findViewById(R.id.resetBtn);
        reset.setOnClickListener(this);

        //Users input for their measurements
        length = (EditText) findViewById(R.id.editTextTextPersonName7);
        height = (EditText) findViewById(R.id.editTextTextPersonName11);
        width = (EditText) findViewById(R.id.editTextTextPersonName12);

        //TextViews for conversion if needed
        lengCon = (TextView) findViewById(R.id.lengthConv);
        heightCon = (TextView) findViewById(R.id.heightConv);
        widthCon = (TextView) findViewById(R.id.widthConv);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.convBtn:
                Toast.makeText(ManualEntry.this,"Dimensions converted",Toast.LENGTH_LONG).show();
                conversion();
                break;

            case R.id.resetBtn:
                Toast.makeText(ManualEntry.this,"Dimensions reset",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this,ManualEntry.class));
                break;
            case R.id.submitBtn:
                Toast.makeText(ManualEntry.this,"Dimensions submitted",Toast.LENGTH_LONG).show();
                break;

        }
    }

    private void conversion() {

        Double leng = Double.parseDouble(length.getText().toString().trim());
        Double hght = Double.parseDouble(height.getText().toString().trim());
        Double wth = Double.parseDouble(width.getText().toString().trim());

        leng = leng * 2.54;
        hght = hght * 2.54;
        wth = wth * 2.54;

        String lengConv = String.valueOf(leng);
        String hghtConv = String.valueOf(hght);
        String wthConv = String.valueOf(wth);

        lengCon.setText(lengConv);
        heightCon.setText(hghtConv);
        widthCon.setText(wthConv);




    }


}