package com.example.dimenscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

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

    Button rotation,create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_table);

        rotation = (Button)findViewById(R.id.button10);
        rotation.setOnClickListener(this);

        create = (Button)findViewById(R.id.CreateItem);
        create.setOnClickListener(this);

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
            case R.id.CreateItem:
                Toast.makeText(ViewTable.this, "Image saved successfully", Toast.LENGTH_SHORT).show();
                objectDialog();
                break;
        }
    }


    public void objectDialog() {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.object_dialog, null);

        // Find the EditTexts in the dialog layout
        EditText widthTxt = dialogView.findViewById(R.id.widthEntry);
        EditText heightTxt = dialogView.findViewById(R.id.heightEntry);
        EditText objectTxt = dialogView.findViewById(R.id.objectNameEntry);

        // Build the dialog
        AlertDialog.Builder objDialog = new AlertDialog.Builder(this);
        objDialog.setTitle("Add Object");
        objDialog.setView(dialogView);
        objDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Parse the width and height entered by the user
                double width = Double.parseDouble(widthTxt.getText().toString());
                double height = Double.parseDouble(heightTxt.getText().toString());
                String obj = objectTxt.getText().toString();

                // Add the new object to the plot
                addingNewObject(width, height,obj);
            }
        });
        objDialog.setNegativeButton("Cancel", null);
        // Show the dialog
        objDialog.show();
    }

    private void addingNewObject(double width, double height, String objName) {
        // Calculate the position of the new object
        // by calculating x and y values by the widht and room height it ensures
        // it will be inside the room
        double x = Math.random() * (roomWidth - width);
        double y = Math.random() * (roomHeight - height);

        // Create the XYSeries for the new object
        XYSeries objectSize = new SimpleXYSeries(
                Arrays.asList(x, x + width, x + width, x),
                Arrays.asList(y, y, y + height, y + height),
                objName );

        // Add the new object to the plot
        plot.addSeries(objectSize, new LineAndPointFormatter(
                ContextCompat.getColor(this, R.color.teal_200),
                null, R.color.teal_200, null));
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