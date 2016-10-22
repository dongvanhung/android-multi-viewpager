package com.leejangyoun.multiviewpager;

public class Fruit {

    public enum TYPE {ITEM, MORE, PROGRESS}

    private int no;
    private String title;
    private String thumb;
    private String desc;
    private TYPE type;

    public Fruit( ) {
    }

    public Fruit(TYPE type) {
        this.type = type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }


    public int getNo() {
        return no;
    }

    public String getTitle() {
        return title;
    }

    public String getThumb() {
        return thumb;
    }

    public String getDesc() {
        return desc;
    }

    public TYPE getType() {
        return type;
    }
}