package com.example.mephi_app.ui.gallery;

import com.example.mephi_app.ui.JSONStrategy;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class ReminderJSONHelper implements JSONStrategy {

    public ArrayList<reminder> importFromJSON(String jsonString) {

        ArrayList<reminder> rems;
        try{
            Gson gson = new Gson();
            rems = new ArrayList<>(Arrays.asList(gson.fromJson(jsonString, reminder[].class)));
            return  rems;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

}
