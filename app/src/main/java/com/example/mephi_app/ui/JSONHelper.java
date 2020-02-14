package com.example.mephi_app.ui;

import android.os.AsyncTask;

import java.util.ArrayList;

public class JSONHelper extends AsyncTask <String,Void,ArrayList>{
    JSONStrategy jsonStrat;
    IOpensFiles context;
    String filename;

    public JSONHelper(IOpensFiles context, JSONStrategy strat, String filename){
        this.context = context;
        this.jsonStrat = strat;
        this.filename = filename;
    }

    @Override
    protected ArrayList doInBackground(String... strings) {
          ArrayList a = jsonStrat.importFromJSON(strings[0]);
        return a;
    }
    @Override
    protected void onPostExecute(ArrayList content) {
        if (context!=null){
            context.openFile(filename);
        }
    }
}
