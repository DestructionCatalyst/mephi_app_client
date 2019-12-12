package com.example.mephi_app.ui.gallery;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class reminder {
    String name,text,place;
    String from,to;
    int id, idPlace;
    Date fromD, toD;
    boolean check;
    reminder(int id, String name, String from, String to, String place, String text, int IDPlace){
        this.id = id;
        this.name = name;
        this.text = text;
        this.place = place;
        this.idPlace = IDPlace;
        this.from = from;
        this.to = to;
        //this.show = /*this.from+" - "+*/this.to;
        //На парсиег в дату пока забьём, проще вручную распарсить
        //SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        //try {
            /*fromD = formatter.parse(from);
            toD = formatter.parse(to);*/

        /*} catch (ParseException e) {
            show = to;
            e.printStackTrace();
        }*/

        //this.check = check;
    }
    public String getTime(){
        return  from+" - "+to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
    /*public int getIDPlace() {
        return IDPlace;
    }

    public void setIDPlace(int IDPlace) {
        this.IDPlace = IDPlace;
    }*/
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }


        @Override
    public  String toString(){
        return name;
    }
}
