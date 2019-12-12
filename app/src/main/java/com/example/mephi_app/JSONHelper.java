package com.example.mephi_app;

import android.content.Context;

import com.example.mephi_app.group;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;

public class JSONHelper {

    public static ArrayList<group> importFromJSON(Context context, String jsonString) {

        ArrayList<group> groups;
        try{
            Gson gson = new Gson();
            groups = new ArrayList<>(Arrays.asList(gson.fromJson(jsonString, group[].class)));
            return groups;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {

        }

        return null;
    }

}