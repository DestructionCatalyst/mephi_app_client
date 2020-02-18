package com.example.mephi_app.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.example.mephi_app.IOpensJson;
import com.example.mephi_app.JSONHelper;
import com.example.mephi_app.NetworkTask;
import com.example.mephi_app.ui.LoadErrorMessage;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment implements IOpensJson {
    private final String lnkpost = "getnews?inst=";
    private final static String FILE_NAME = "group";

    private HomeViewModel homeViewModel;

    private static MainActivity ma;

    private static Spinner spinner;
    private static Switch sw;
    private static TextView text;
    private static ListView listView;
    private static LinearLayout ll;
    private static WebView wv;
    private LoadErrorMessage lem;

    private ArrayList<news> newsArrayList;


    private static ArrayAdapter groupAdapter;
    private MyAdapter newsAdapter;

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
            else openGroups();

        listView = root.findViewById(R.id.listView);

        text = root.findViewById(R.id.textView4);

        wv = root.findViewById(R.id.webview1);

        sw = root.findViewById(R.id.switch2);

        lem = root.findViewById(R.id.lem_news);

        if (sw != null) {
            sw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (sw.getVisibility() == View.VISIBLE) {
                        refresh(isChecked);
                    }
                    else refresh(false);
                    }
                 }
            );
        }

        AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ma.curGroup = (group)parent.getItemAtPosition(position);
                changeGroup();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinner.setOnItemSelectedListener(itemSelectedListener);

        lem.changeStatus(LoadErrorMessage.LOAD_PROGRESS);
        NetworkTask task1 = new NetworkTask(this);
        task1.execute(ma.lnkbase+lnkpost+"0");

        Log.d("Link",ma.lnkbase+lnkpost);

        return root;
    }

    @Override
    public void open(String jsonStr){//IOpensJson

        JSONHelper helper1 = new JSONHelper(this, new NewsJSONHelper());
        helper1.execute(jsonStr);
        if(ma.offline){
            ma.loadGroups();
        }
        ma.offline = false;
    }


    @Override
    public void swear(String swearing) {//IOpensJson
        String fullSwearing = "Ошибка открытия страницы мероприятий. "+swearing;
        Log.d("Connection", fullSwearing);
        lem.changeStatus(LoadErrorMessage.LOAD_ERROR);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(fullSwearing)
                .setTitle("Ошибка!")
                .setPositiveButton("OK",null);
        AlertDialog dialog1 = builder1.create();
        dialog1.show();
    }

    @Override
    public void displayJson(ArrayList a) {//IOpensJson
        newsArrayList = a;

        news tmp;

        for (int i = 0; i<newsArrayList.size()/2; i++){//Перевернуть массив новостей, чтобы сначала были новые
            tmp = newsArrayList.get(i);
            newsArrayList.set(i, newsArrayList.get(newsArrayList.size()-1-i));
            newsArrayList.set(newsArrayList.size()-1-i, tmp);
        }
        newsAdapter = new MyAdapter(this.getActivity(), this,newsArrayList);
        listView.setAdapter(newsAdapter);
        lem.changeStatus(LoadErrorMessage.LOAD_FINISHED);
    }

    public static void openGroups(){
        try {
            groupAdapter = new ArrayAdapter(ma, android.R.layout.simple_spinner_item, ma.groups);
            groupAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
            spinner.setAdapter(groupAdapter);
            spinner.setSelection(ma.curGroup.id);
        } catch (Exception e) {
            Toast.makeText(ma, "Проверьте соединение с Интернетом!",Toast.LENGTH_SHORT).show();
        }

    }

    private void refresh(boolean targeting){
        NetworkTask task1 = new NetworkTask(this);
        if ((targeting)&&(!ma.offline)){
            task1.execute(ma.lnkbase+lnkpost+ma.curGroup.idInst);
        }else {
            task1.execute(ma.lnkbase+lnkpost+"0");
        }
    }

    void  ReplaceMeDaddy(news shown){

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

    private void changeGroup(){
        FileOutputStream fos;

        try {
            fos = ma.openFileOutput(FILE_NAME, MODE_PRIVATE);
            String write = "";
            write = ma.curGroup.name;
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
            listView.setVisibility(View.INVISIBLE);
            refresh(sw.isChecked());
            listView.setVisibility(View.VISIBLE);
            sw.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
        }
    }

}