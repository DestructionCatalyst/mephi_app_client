package com.example.mephi_app.ui.slideshow;

import com.example.mephi_app.ui.JSONStrategy;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class WayJSONHelper implements JSONStrategy {
    public ArrayList<way> importFromJSON(String jsonString) {

        ArrayList<way> ways;
        try{
            Gson gson = new Gson();
            ways = new ArrayList<>(Arrays.asList(gson.fromJson(jsonString, way[].class)));
            return ways;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
