package com.example.mephi_app.ui.home;

import android.content.Context;

import com.example.mephi_app.ui.gallery.reminder;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class JSONHelper {

    static ArrayList<news> importFromJSON(Context context, String jsonString) {

        ArrayList<news> newss;
        try{
            Gson gson = new Gson();
            newss = new ArrayList<>(Arrays.asList(gson.fromJson(jsonString, news[].class)));
            return newss;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {

        }

        return null;
    }

}
