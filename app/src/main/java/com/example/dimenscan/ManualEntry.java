package com.example.dimenscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ManualEntry extends AppCompatActivity implements View.OnClickListener {
    Button conv, submit;
    EditText length, height, width;
    TextView lengCon, heightCon, widthCon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        conv = (Button) findViewById(R.id.button3);
        conv.setOnClickListener(this);

        submit = (Button) findViewById(R.id.button4);
        submit.setOnClickListener(this);

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
            case R.id.button3:
                Toast.makeText(ManualEntry.this,"Error",Toast.LENGTH_LONG).show();
                conversion();
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