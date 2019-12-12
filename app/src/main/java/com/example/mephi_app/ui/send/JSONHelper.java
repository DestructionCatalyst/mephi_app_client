package com.example.mephi_app.ui.send;

import android.content.Context;


import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;

public class JSONHelper {


    public static qr importFromJSON(Context context, String jsonString) {

        ArrayList<qr> qrs;
        try{

            Gson gson = new Gson();
            qrs = new ArrayList<>(Arrays.asList(gson.fromJson(jsonString, qr[].class)));
            if (qrs.isEmpty()){return null;}
            else return qrs.get(0);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }


        return null;
    }

}