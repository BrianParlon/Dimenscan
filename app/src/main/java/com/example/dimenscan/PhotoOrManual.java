package com.example.dimenscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PhotoOrManual extends AppCompatActivity implements View.OnClickListener {
    Button man, cam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_or_manual);

        man =(Button) findViewById(R.id.camera_dim);
        man.setOnClickListener(this);

         cam =(Button) findViewById(R.id.manual_dim);
         cam.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.manual_dim:
                startActivity(new Intent(this,ManualEntry.class));
                break;
            case R.id.camera_dim:
                startActivity(new Intent(this,CameraWork.class));
        }
    }
}