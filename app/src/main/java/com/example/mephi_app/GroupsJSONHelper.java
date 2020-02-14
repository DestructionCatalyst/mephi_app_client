package com.example.mephi_app;

import com.example.mephi_app.ui.JSONStrategy;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class GroupsJSONHelper implements JSONStrategy {
    public ArrayList<group> importFromJSON(String jsonString) {

        ArrayList<group> groups;
        try{
            Gson gson = new Gson();
            groups = new ArrayList<>(Arrays.asList(gson.fromJson(jsonString, group[].class)));
            return groups;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

}