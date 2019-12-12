package com.example.mephi_app.ui.slideshow;

import android.content.Context;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class JSONHelper {

    static ArrayList<dot> importFromJSON(Context context, String jsonString) {

        ArrayList<dot> dots;
        try{
            Gson gson = new Gson();
            dots = new ArrayList<>(Arrays.asList(gson.fromJson(jsonString, dot[].class)));
            return dots;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {

        }

        return null;
    }
    static ArrayList<way> importWaysFromJSON(Context context, String jsonString) {

        ArrayList<way> ways;
        try{
            Gson gson = new Gson();
            ways = new ArrayList<>(Arrays.asList(gson.fromJson(jsonString, way[].class)));
            return ways;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {

        }

        return null;
    }

}
