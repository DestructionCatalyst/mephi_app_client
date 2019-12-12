package com.example.mephi_app.ui.gallery;

import android.content.Context;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JSONHelper {

    static ArrayList<reminder> importFromJSON(Context context, String jsonString) {

        ArrayList<reminder> rems;
        try{
            Gson gson = new Gson();
            rems = new ArrayList<>(Arrays.asList(gson.fromJson(jsonString, reminder[].class)));
            return  rems;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        finally {

        }

        return null;
    }

}
