package com.example.dimenscan;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;

import java.util.Arrays;

public class RoomObject {
    XYSeries objSize;
    double width,height,x,y;
    String objName;

    public RoomObject(double width, double height, String objName, double x, double y) {
        this.width = width;
        this.height = height;
        this.objName = objName;
        this.x = x;
        this.y = y;
    }

    public void updateObjectSize(){
        objSize=new SimpleXYSeries(
                Arrays.asList(x,x+width,x+width,x),
                Arrays.asList(y,y,y+height,y+height),
                objName);

    }
}
