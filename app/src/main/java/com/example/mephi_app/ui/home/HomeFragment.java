package com.example.mephi_app.ui.home;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mephi_app.MainActivity;
import com.example.mephi_app.R;
import com.example.mephi_app.group;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private static MainActivity ma;
    public static Spinner spinner;
    private ArrayList<news> newsArrayList;
    private String JSONString;
    //private  String lnk = "http://192.168.1.7:3000/home/getnews/?inst=";
    private String lnkpost = "getnews?inst=";
    static Switch sw;
    static String FILE_NAME = "group";
    private static ArrayAdapter adapter;
    private MyAdapter adapter1;
    private static TextView text;
    static ListView listView;
    static LinearLayout ll;
    public static WebView wv;
    static boolean ne_lez=false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        if (getActivity() != null) { ma = (MainActivity) getActivity(); }
        spinner = root.findViewById(R.id.spinner2);

        ll = root.findViewById(R.id.linlay1);

        if (!ma.firstLaunch){ma.firstLaunch = true;}
            else open();
        listView = root.findViewById(R.id.listView);

        text = root.findViewById(R.id.textView4);

        wv = root.findViewById(R.id.webview1);

        sw = root.findViewById(R.id.switch2);

        if (sw != null) {
            sw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (sw.getVisibility() == View.VISIBLE) {
                        if (!ne_lez) refresh(isChecked);
                    }
                    else refresh(false);
                    }
                 }
            );
        }
        ProgressTask task = new ProgressTask();
        task.execute(ma.lnkbase+lnkpost+"0");



        return root;
    }


    private class ProgressTask extends AsyncTask<String, Void, String>{
        //@Override
        protected String doInBackground(String... path) {

            String content;
            try{
                content = getContent(path[0]);

            }
            catch (IOException ex){
                content = ex.getMessage();
            }
            //JSONString = content;
            return content;
        }
        //@Override
        protected void onPostExecute(String content) {
            JSONString = content;
            /*AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage(content)
                    .setTitle("Content");
            AlertDialog dialog1 = builder1.create();
            dialog1.show();*/

            newsArrayList = new ArrayList<>();

            // adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, reminders);
            //listView.setAdapter(adapter);

            openJSON(JSONString);


        }

        private String getContent(String path) throws IOException {
            BufferedReader reader=null;
            try {
                URL url=new URL(path);
                HttpURLConnection c=(HttpURLConnection)url.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(10000);
                c.connect();
                reader= new BufferedReader(new InputStreamReader(c.getInputStream()));
                StringBuilder buf=new StringBuilder();
                String line=null;
                while ((line=reader.readLine()) != null) {
                    buf.append(line + "\n");
                }
                return(buf.toString());
                //return reader.readLine();
            }
            catch(Exception e){
                e.printStackTrace();
                return "Error\n"+e.getMessage();
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
            }

        }
    }

    private void openJSON(String jsonStr){
        newsArrayList = JSONHelper.importFromJSON(this.getActivity(), jsonStr);
        if(newsArrayList!=null){
            //Toast.makeText(this, "Данные восстановлены", Toast.LENGTH_LONG).show();
            adapter1 = new MyAdapter(this.getActivity(), this,newsArrayList);
            listView.setAdapter(adapter1);

        }
        else{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());


            builder1.setMessage("Не удалось преобразовать в JSON!")
                    .setTitle("Сообщение!");


            AlertDialog dialog1 = builder1.create();
            dialog1.show();
        }

    }

    public static void open(){
        try {
            adapter = new ArrayAdapter(ma, android.R.layout.simple_spinner_item, ma.groups);
            adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
            spinner.setAdapter(adapter);
            //Toast.makeText(ma, ""+ma.curGroup.id, Toast.LENGTH_SHORT).show();
            spinner.setSelection(ma.curGroup.id);

            AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ma.curGroup = (group)parent.getItemAtPosition(position);
                    FileOutputStream fos;

                    try {
                        fos = ma.openFileOutput(FILE_NAME, MODE_PRIVATE);
                        String write = "";
                        write = ma.curGroup.name;
                        //Toast.makeText(ma, "Сохранено "+write, Toast.LENGTH_SHORT).show();
                        fos.write(write.getBytes());
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(ma, "Ошибка сохранения данных!", Toast.LENGTH_SHORT).show();
                    }
                    if (ma.curGroup.idInst == 0){
                        sw.setChecked(false);
                        sw.setVisibility(View.INVISIBLE);
                        text.setVisibility(View.INVISIBLE);

                    }
                    else {
                        boolean ch = sw.isChecked();
                        listView.setVisibility(View.INVISIBLE);
                        ne_lez = true;
                        sw.setChecked(false);
                        ne_lez = false;
                        sw.setChecked(ch);

                        listView.setVisibility(View.VISIBLE);
                        sw.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };
            spinner.setOnItemSelectedListener(itemSelectedListener);


        } catch (Exception e) {
            Toast.makeText(ma, "Проверьте соединение с Интернетом!",Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
        }



    }
    public  void refresh(boolean targeting){
        if (targeting){
            ProgressTask task = new ProgressTask();
            task.execute(ma.lnkbase+lnkpost+ma.curGroup.idInst);
        }else {
            ProgressTask task = new ProgressTask();
            task.execute(ma.lnkbase+lnkpost+"0");
        }
    }

    public void  ReplaceMeDaddy(news shown){

        if (ll.getVisibility() == View.VISIBLE) {
            ll.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            wv.setVerticalScrollBarEnabled(false);

            wv.loadData(getString(R.string.web_start) + "<h4><br>" + shown.name + "</h4><br>" + shown.text + "<br>Место: " + shown.place + "<br>Время: " + shown.t + getString(R.string.web_end), "text/html; charset=utf-8", "utf-8");


            wv.setVisibility(View.VISIBLE);
        }

    }
    public static boolean Back2Bizniz(){
        if (wv.getVisibility() == View.VISIBLE) {
            ll.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            wv.loadData("", "text/html; charset=utf-8", "utf-8");
            wv.setVisibility(View.GONE);
            return true;
        }
        else return false;
    }
}