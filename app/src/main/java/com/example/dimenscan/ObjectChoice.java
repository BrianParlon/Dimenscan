package com.example.dimenscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ObjectChoice extends AppCompatActivity implements View.OnClickListener {
    Button desk, table, bed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_or_manual);

        bed =(Button) findViewById(R.id.bedsBtn);
        bed.setOnClickListener(this);

        table =(Button) findViewById(R.id.tableButton);
        table.setOnClickListener(this);

        desk =(Button) findViewById(R.id.deskBtn);
        desk.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){

            case R.id.deskBtn:
                startActivity(new Intent(this, DeskEntry.class));
                break;

            case R.id.tableButton:
                startActivity(new Intent(this,TableEntry.class));
                break;

            case R.id.bedsBtn:
                startActivity(new Intent(this,BedEntry.class));
                break;

        }
    }
}