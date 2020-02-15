package com.example.mephi_app.ui;

import android.os.AsyncTask;

import java.util.ArrayList;

public class JSONHelper extends AsyncTask <String,Void,ArrayList>{
    private JSONStrategy jsonStrat;
    private IOpensJson context;

    public JSONHelper(IOpensJson context, JSONStrategy strat){
        this.context = context;
        this.jsonStrat = strat;
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
