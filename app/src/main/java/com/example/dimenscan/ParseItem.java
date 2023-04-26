package com.example.dimenscan;

import android.widget.TextView;

public class ParseItem {

    private String imgUrl;
    private String title;
    private String depth;
    private String width;
    private String deskUrl;
    private String price;
    private String height;


    public ParseItem() {
    }

    public ParseItem(String imgUrl, String title,String width, String depth,String deskUrl) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.depth = depth;
        this.width = width;
        this.deskUrl = deskUrl;



    }

    public ParseItem(String title, String imgUrl, String width, String depth, String deskUrl, String height,String price) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.depth = depth;
        this.width = width;
        this.deskUrl = deskUrl;
        this.height = height;
        this.price = price;
    }


    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {

        this.imgUrl = imgUrl;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width= width;
    }

public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth= depth;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeskUrl() {
        return deskUrl;
    }

    public void setDeskUrl(String deskUrl) {
        this.deskUrl= deskUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price= price;
    }
    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height= height;
    }


}
