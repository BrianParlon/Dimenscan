package com.example.dimenscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.nio.InvalidMarkException;

public class DeskInfo extends AppCompatActivity {

    private TextView deskName,deskWidth,deskDepth;
    private ImageView deskImage;
    private String dTitle, dImg,dWidth,dDepth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desk_info);

        deskName =findViewById(R.id.deskTitle);
        deskImage =findViewById(R.id.deskPicture);

        deskDepth =findViewById(R.id.deskDepth);
        deskWidth =findViewById(R.id.deskWidth);

        Intent deskIntent = getIntent();
        dTitle=deskIntent.getStringExtra("title");
        dImg= deskIntent.getStringExtra("imageUrl");
        dWidth=deskIntent.getStringExtra("width");
        dDepth=deskIntent.getStringExtra("depth");

        Picasso.get().load(dImg).into(deskImage);
        deskName.setText(dTitle);
        deskWidth.setText(dWidth);
        deskDepth.setText(dDepth);



    }
}