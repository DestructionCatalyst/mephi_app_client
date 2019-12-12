package com.example.mephi_app;

public class group {
    public int id, idInst;
    public String name;
    public group(int id, String name, int idInst){
        this.id = id;
        this.idInst = idInst;
        this.name = name;

    }
    @Override
    public  String toString(){
        return name;
    }
}
