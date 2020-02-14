package com.example.mephi_app.ui.slideshow;

import com.example.mephi_app.ui.JSONStrategy;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class DotJSONHelper implements JSONStrategy {

    public ArrayList<dot> importFromJSON(String jsonString) {

        ArrayList<dot> dots;
        try{
            Gson gson = new Gson();
            dots = new ArrayList<>(Arrays.asList(gson.fromJson(jsonString, dot[].class)));
            return dots;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

}
