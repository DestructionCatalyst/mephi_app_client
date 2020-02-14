package com.example.mephi_app.ui.home;

import com.example.mephi_app.ui.JSONStrategy;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class NewsJSONHelper implements JSONStrategy {

    public ArrayList<news> importFromJSON(String jsonString)
    {
        ArrayList<news> newss;
        try{
            Gson gson = new Gson();
            newss = new ArrayList<>(Arrays.asList(gson.fromJson(jsonString, news[].class)));
            return newss;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

}
