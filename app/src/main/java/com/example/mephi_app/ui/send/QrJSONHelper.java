package com.example.mephi_app.ui.send;

import com.example.mephi_app.ui.JSONStrategy;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class QrJSONHelper implements JSONStrategy {

    public ArrayList<qr> importFromJSON(String jsonString) {
        ArrayList<qr> qrs;

        try{

            Gson gson = new Gson();
            qrs = new ArrayList<>(Arrays.asList(gson.fromJson(jsonString, qr[].class)));
            if (qrs.isEmpty()){return null;}
            else return qrs;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

}