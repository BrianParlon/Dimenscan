package com.example.dimenscan;

public class Dimension {
    private String height;
    private String width;
    private String length;

    public Dimension(){

    }

    public Dimension(String height, String width, String length){
        this.height = height;
        this.width = width;
        this.length = length;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
