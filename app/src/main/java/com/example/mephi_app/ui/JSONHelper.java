package com.example.mephi_app.ui;

import android.os.AsyncTask;

import java.util.ArrayList;

public class JSONHelper extends AsyncTask <String,Void,ArrayList>{
    private JSONStrategy jsonStrat;
    private IOpensJson context;
    private String filename;

    public JSONHelper(IOpensJson context, String filename, JSONStrategy strat){
        this.context = context;
        this.jsonStrat = strat;
        this.filename = filename;
    }

    @Override
    protected ArrayList doInBackground(String... strings) {
        return jsonStrat.importFromJSON(strings[0]);
    }
    @Override
    protected void onPostExecute(ArrayList content) {
        if (context!=null){
            context.displayJson(content);
        }

    }
}
