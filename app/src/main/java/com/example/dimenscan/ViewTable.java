package com.example.dimenscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ViewTable extends AppCompatActivity implements View.OnClickListener {
    private FirebaseUser mUser;
    private String userId;
    private FirebaseAuth mAuth;
    private String onlineUserId;
    private DatabaseReference reference;

    private XYPlot plot;
    private XYSeries roomSize;
    private XYSeries deskSize;

    //All Measurements are in Meters
    private double roomWidth = 5;
    private double roomHeight = 5;

    private double deskWidth = 0.48;
    private double deskHeight = 0.84;

    //distance from left wall
    private double deskX = 2.0;
    //distance from bottom wall
    private double deskY = 2.5;

    private double lastTouchX;
    private double lastTouchY;

    Button rotation,save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_table);

        rotation = (Button)findViewById(R.id.button10);
        rotation.setOnClickListener(this);

        save = (Button)findViewById(R.id.saveButton);
        save.setOnClickListener(this);

        // create XYPlot object
        plot = (XYPlot) findViewById(R.id.myPlot);


        // Room dimensions
        roomSize = new SimpleXYSeries(Arrays.asList(0, roomWidth, roomWidth, 0),
                Arrays.asList(0, 0, roomHeight, roomHeight),
                "Room");


        //Desk dimensions
        deskSize = new SimpleXYSeries(Arrays.asList(deskX, deskX + deskWidth, deskX + deskWidth, deskX),
                Arrays.asList(deskY, deskY, deskY + deskHeight, deskY + deskHeight),
                "Desk");

        // add to the plot
        plot.addSeries(roomSize, new LineAndPointFormatter(Color.BLUE, null, null, null));
        plot.addSeries(deskSize, new LineAndPointFormatter(Color.BLACK, null, Color.BLACK, null));

        // set the range of plot to match the room dimensions
        plot.setRangeBoundaries(0, roomHeight, BoundaryMode.FIXED);
        plot.setDomainBoundaries(0, roomWidth, BoundaryMode.FIXED);

        plot.setDrawingCacheEnabled(true);
        // touch to move the desk
        plot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        lastTouchX = event.getX();
                        lastTouchY = event.getY();

                        break;

                    case MotionEvent.ACTION_MOVE:
                        double deltaX = (event.getX() - lastTouchX) / plot.getWidth() * roomWidth;
                        double deltaY = -(event.getY() - lastTouchY) / plot.getHeight() * roomHeight;
                        deskX += deltaX;
                        deskY += deltaY;
                        updateDeskSize();
                        lastTouchX = event.getX();
                        lastTouchY = event.getY();
                        break;
                }
                return true;
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button10:
                rotation();
                break;
            case R.id.saveButton:
                Toast.makeText(ViewTable.this, "Image saved successfully", Toast.LENGTH_SHORT).show();
                    saveImage();
                break;
        }
    }


    public void saveImage() {

    }



    public void rotation(){

        double temporary = deskWidth;
        deskWidth = deskHeight;
        deskHeight = temporary;
        updateDeskSize();


    }

    private void updateDeskSize() {
        //List of x and y values for desks
        List<Number>  xValues = Arrays.asList(deskX, deskX + deskWidth, deskX + deskWidth, deskX);
        List<Number> yValues; yValues = Arrays.asList(deskY, deskY, deskY + deskHeight, deskY + deskHeight);

        // Remove the previous desk series
        plot.removeSeries(deskSize);

        // Create a new desk size from xand y values
        deskSize = new SimpleXYSeries(xValues, yValues, "Desk");
        // Add desk to plot
        plot.addSeries(deskSize, new LineAndPointFormatter(Color.BLACK, null, Color.BLACK, null));

        plot.redraw();
    }


}