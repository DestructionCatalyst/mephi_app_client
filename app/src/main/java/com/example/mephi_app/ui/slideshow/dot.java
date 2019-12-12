package com.example.mephi_app.ui.slideshow;

public class dot {
    int id, x,y;
    String name;
    public dot (int id,String name, int x,int y){
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return name;
    }
}
