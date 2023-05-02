package com.example.dimenscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ViewTable extends AppCompatActivity implements View.OnClickListener {
    private FirebaseUser user;
    private String userId;
    private FirebaseAuth mAuth;
    private String onlineUserId;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private XYPlot plot;
    private XYSeries roomSize;
    private RoomObject desk;
    private List<RoomObject> roomObject = new ArrayList<>();


    //All Measurements are in Meters
    private double roomWidth ;
    private double roomHeight ;

//    private double roomWidth ;
//    private double roomHeight;

    private double deskWidth;
    private double deskHeight;
    private String roomName="Office";
    private String bedUrl="empty";
    private String deskPrice,deskTitle;

    //distance from left wall
    private double deskX = 2.0;
    //distance from bottom wall
    private double deskY = 2.5;
    private RoomObject lastTouch = null;

    private double lastTouchX;
    private double lastTouchY;

    Button rotation,create,save,removeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_table);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        rotation = (Button)findViewById(R.id.button10);
        rotation.setOnClickListener(this);

        create = (Button)findViewById(R.id.CreateItem);
        create.setOnClickListener(this);

        save = (Button)findViewById(R.id.saveItem);
        save.setOnClickListener(this);

        removeBtn = findViewById(R.id.remove);
        removeBtn.setOnClickListener(this);

        storageReference= FirebaseStorage.getInstance().getReference("Tables");
        databaseReference = FirebaseDatabase.getInstance().getReference("images").child(userId);



        // create XYPlot object
        plot = (XYPlot) findViewById(R.id.myPlot);


        Intent roomEntry = getIntent();
        roomWidth = Double.parseDouble(roomEntry.getStringExtra("rWidth"));
        roomHeight = Double.parseDouble(roomEntry.getStringExtra("rDepth"));

        deskWidth = Double.parseDouble(roomEntry.getStringExtra("tWidth"));
        deskHeight= Double.parseDouble(roomEntry.getStringExtra("tHeight"));
        deskPrice = roomEntry.getStringExtra("tPrice");
        deskTitle = roomEntry.getStringExtra("tTitle");

        deskHeight = deskHeight/100;
        deskWidth = deskWidth/100;


        // Room dimensions
        roomSize = new SimpleXYSeries(Arrays.asList(0, roomWidth, roomWidth, 0),
                Arrays.asList(0, 0, roomHeight, roomHeight),
                "Room");


//        //Desk dimensions
//        deskSize = new SimpleXYSeries(Arrays.asList(deskX, deskX + deskWidth, deskX + deskWidth, deskX),
//                Arrays.asList(deskY, deskY, deskY + deskHeight, deskY + deskHeight),
//                "Desk");

        desk = new RoomObject(deskWidth, deskHeight, "Table", deskX, deskY);
        desk.updateObjectSize();
        roomObject.add(desk);

        plot.addSeries(roomSize, new LineAndPointFormatter(Color.BLUE, null, null, null));
        plot.addSeries(desk.objSize, new LineAndPointFormatter(Color.BLACK, null, Color.BLACK, null));

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

                        double touchX = lastTouchX / plot.getWidth() * roomWidth;
                        double touchY = roomHeight - lastTouchY / plot.getHeight() * roomHeight;

                        for (RoomObject obj : roomObject) {
                            if (touchX >= obj.x && touchX <= obj.x + obj.width &&
                                    touchY >= obj.y && touchY <= obj.y + obj.height) {
                                lastTouch = obj;
                                desk = obj;
                                break;
                            }
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        double deltaX = (event.getX() - lastTouchX) / plot.getWidth() * roomWidth;
                        double deltaY = -(event.getY() - lastTouchY) / plot.getHeight() * roomHeight;

                        // Check if object is within the plot boundaries
                        if (desk.x + deltaX >= 0 && desk.x + desk.width + deltaX <= roomWidth &&
                                desk.y + deltaY >= 0 && desk.y + desk.height + deltaY <= roomHeight) {
                            desk.x += deltaX;
                            desk.y += deltaY;
                            desk.updateObjectSize();
                            updatePlot();
                            lastTouchX = event.getX();
                            lastTouchY = event.getY();
                        }
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
                Toast.makeText(this, "Item has been rotated", Toast.LENGTH_SHORT).show();
                rotation();
                break;

            case R.id.CreateItem:
                Toast.makeText(ViewTable.this, "Add a new Item", Toast.LENGTH_SHORT).show();
                objectDialog();
                break;

            case R.id.remove:
                Toast.makeText(this, "Remove", Toast.LENGTH_SHORT).show();
                removeTouch();
                break;

                case R.id.saveItem:
                    uploadToStorage();
                break;
        }
    }

    private void removeTouch() {
        if (lastTouch != null) {
            roomObject.remove(lastTouch);
            lastTouch = null;
            updatePlot();
        }
    }

    private Bitmap getPlotBitmap() {
        plot.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(plot.getDrawingCache());
        plot.setDrawingCacheEnabled(false);
        return bitmap;
    }
    private void uploadToStorage() {
        // images to a PNG format and saved to firebaseStorage

        Bitmap plotBitmap = getPlotBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        plotBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference plotImg= storageReference.child("tables/table"+System.currentTimeMillis()+".png");

        UploadTask uploadPlot = plotImg.putBytes(data);
        uploadPlot.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();

                String width = String.valueOf(deskWidth);
                String depth = String.valueOf(deskHeight);
                ParseItem parseItem = new ParseItem(downloadUrl.toString(),roomName,width,depth,bedUrl,deskPrice,deskTitle);

                String uploadId = databaseReference.push().getKey();
                databaseReference.child(uploadId).setValue(parseItem);
                Toast.makeText(ViewTable.this, "Image saved to database.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ViewTable.this, "Error has Occurred.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void objectDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.object_dialog, null);
        EditText widthTxt = dialogView.findViewById(R.id.widthEntry);
        EditText heightTxt = dialogView.findViewById(R.id.heightEntry);
        EditText objectTxt = dialogView.findViewById(R.id.objectNameEntry);

        AlertDialog.Builder objDialog = new AlertDialog.Builder(this);
        objDialog.setTitle("Add Object to Room");
        objDialog.setView(dialogView);
        objDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                double width = Double.parseDouble(widthTxt.getText().toString());
                double height = Double.parseDouble(heightTxt.getText().toString());
                String obj = objectTxt.getText().toString();
                addingNewObject(width, height,obj);
            }
        });
        objDialog.setNegativeButton("Cancel", null);
        objDialog.show();
    }

    private void addingNewObject(double width, double height, String objName) {
        // Calculate the position of the new object
        // by calculating x and y values by the widht and room height it ensures
        // it will be inside the room
        double x = Math.random() * (roomWidth - width);
        double y = Math.random() * (roomHeight - height);


        RoomObject newObject = new RoomObject(width, height, objName, x, y);
        newObject.updateObjectSize();
        roomObject.add(newObject);
        updatePlot();
    }

    private void updatePlot() {
    plot.clear();
    plot.addSeries(roomSize, new LineAndPointFormatter(Color.BLUE, null, null, null));

    for (RoomObject obj : roomObject) {
        obj.updateObjectSize();
        plot.addSeries(obj.objSize, new LineAndPointFormatter(Color.BLACK, null, Color.BLACK, null));
    }

    plot.redraw();
}

    public void rotation(){
        double temporary = desk.width;
        desk.width= desk.height;
        desk.height = temporary;
        desk.updateObjectSize();
        updatePlot();
    }


}