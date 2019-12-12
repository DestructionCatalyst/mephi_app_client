package com.example.mephi_app.ui.slideshow;

public class way {
    int id, idStart, idEnd;
    double len;

    public way(int id, int idStart,int idEnd,double len){
        this.id = id;
        this.idStart = idStart;
        this.idEnd = idEnd;
        this.len = len;

    }

    @Override
    public String toString(){
        return id+" "+idStart+" "+idEnd+" "+len;
    }
}
