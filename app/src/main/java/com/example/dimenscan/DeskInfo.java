package com.example.dimenscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.nio.InvalidMarkException;

public class DeskInfo extends AppCompatActivity implements View.OnClickListener {

    private TextView deskName,deskWidth,deskDepth;
    private ImageView deskImage;
    private String dTitle, dImg,dWidth,dDepth;
    private Button viewRoom;
    private Context context;
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

        viewRoom = (Button)findViewById(R.id.roomView);
        viewRoom.setOnClickListener(this);

        Picasso.get().load(dImg).into(deskImage);
        deskName.setText(dTitle);
        deskWidth.setText(dWidth);
        deskDepth.setText(dDepth);





    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.roomView:
                Toast.makeText(DeskInfo.this, "Viewing table in room", Toast.LENGTH_LONG).show();
                viewDeskRoom();

                break;
        }
    }

    public void viewDeskRoom(){
        Intent deskRoom = new Intent(context, ViewTable.class);
        deskRoom.putExtra("depth",deskDepth.getText());
        deskRoom.putExtra("width",deskWidth.getText());
        context.startActivity(deskRoom);
    }
}