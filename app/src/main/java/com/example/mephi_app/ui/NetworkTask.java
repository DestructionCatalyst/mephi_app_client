package com.example.mephi_app.ui;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkTask extends AsyncTask<String, Void, String> {
    IOpensJson context;

    public NetworkTask(IOpensJson context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... path) {
        String content;
        Log.d("Connection"," Conntecting to server, URL is "+path[0]);
        try{
            content = getContent(path[0]);
        }
        catch (IOException ex){
            content = ex.getMessage();
        }

        return content;
    }

    private String getContent(String path) throws IOException {
        BufferedReader reader=null;
        try {
            URL url=new URL(path);
            HttpURLConnection c=(HttpURLConnection)url.openConnection();
            c.setRequestMethod("GET");
            c.setConnectTimeout(5000);
            c.setReadTimeout(20000);
            c.connect();
            reader= new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder buf=new StringBuilder();
            String line=null;
            while ((line=reader.readLine()) != null) {
                buf.append(line + "\n");
            }
            return(buf.toString());
        }
        catch(Exception e){
            e.printStackTrace();
            /*StackTraceElement [] err = e.getStackTrace();
            String tmp=e.getMessage()+"\n";
            for (int i = 0;i<err.length;i++){
                tmp+=err[i]+"\n   ";
            }*/
            return "Не удалось подключиться к серверу!";
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    @Override
    protected void onPostExecute(String content) {
        super.onPreExecute();
        if (content.startsWith("[{")){
            //Открываем
            Log.d("Connection","Got JSON from the server");
            context.open(content);
        }
        else{
            //Ругаемся
            Log.d("Connection","Error getting JSON from the server");
            context.swear(content);
        }

    }

}
