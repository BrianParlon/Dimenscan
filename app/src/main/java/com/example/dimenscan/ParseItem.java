package com.example.dimenscan;
public class ParseItem {

    private String imgUrl;
    private String title;
    private String depth;
    private String width;


    public ParseItem() {
    }

    public ParseItem(String imgUrl, String title,String width, String depth) {
        this.imgUrl = imgUrl;
        this.title = title;
        this.depth = depth;
        this.width = width;


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


}
