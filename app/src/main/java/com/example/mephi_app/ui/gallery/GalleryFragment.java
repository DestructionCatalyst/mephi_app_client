package com.example.mephi_app.ui.gallery;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mephi_app.MainActivity;
import com.example.mephi_app.R;
import com.example.mephi_app.ui.IOpensJson;
import com.example.mephi_app.ui.JSONHelper;
import com.example.mephi_app.ui.NetworkTask;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class GalleryFragment extends Fragment implements IOpensJson{
    final private String lnkpost = "getrem/";
    final String FILE_NAME = "reminders";

    private MyAdapter adapter;
    private ArrayList<reminder> reminders;
    private ArrayList<reminder> show;

    MainActivity ma;
    public static LinearLayout ll;
    public static WebView wv;
    public static ListView listView;
    Switch swtch;

    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        //final TextView textView = root.findViewById(R.id.text_reminders);
        galleryViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        if (getActivity() != null) { ma = (MainActivity ) getActivity(); }

        swtch=(Switch) root.findViewById(R.id.switch1);
        if (swtch != null) {
            swtch.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener(){

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        refresh(0);
                    }
                }
            );
        }
        listView = (ListView) root.findViewById(R.id.list);
        ll = (LinearLayout) root.findViewById(R.id.linlay);
        wv = (WebView) root.findViewById(R.id.webview);

        NetworkTask task1 = new NetworkTask(this);
        task1.execute(ma.lnkbase+lnkpost);

        Log.d("Link",ma.lnkbase+lnkpost);

        return root;


    }

    public void open(String jsonStr){//IOpensJson

        JSONHelper helper1 = new JSONHelper(this, new ReminderJSONHelper());
        helper1.execute(jsonStr);
        ma.offline = false;
    }

    @Override
    public void swear(String swearing) {//IOpensJson
        String fullSwearing = "Ошибка открытия памятки первокурсника. "+swearing;
        Log.d("Connection", fullSwearing);
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage(fullSwearing)
                .setTitle("Ошибка!")
                .setPositiveButton("OK",null);
        AlertDialog dialog1 = builder1.create();
        dialog1.show();
    }

    void refresh(int callType){

        if ((callType==0)||(swtch.isChecked())) {

            try {
                show = new ArrayList<reminder>();
                for (reminder cur : reminders) {
                    if (swtch.isChecked()) {
                        if (!cur.check) show.add(cur);
                    } else {
                        show.add(cur);
                    }

                }
                adapter = new MyAdapter(ma, this, show);
                listView.setAdapter(adapter);
            } catch (Exception e) {
                Toast.makeText(ma, "Проверьте соединение с Интернетом!",
                        Toast.LENGTH_SHORT).show();
            }
        }
        if (callType==1){
            FileOutputStream fos;

            try {
                fos = ma.openFileOutput(FILE_NAME, MODE_PRIVATE);
                String write = "";
                for (reminder cur: reminders) {
                    if (cur.check)write+= cur.id+"~";
                }

                fos.write(write.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ma, "Ошибка сохранения данных!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    void  ReplaceMeDaddy(reminder shown){

        if (ll.getVisibility() == View.VISIBLE) {
            ll.setVisibility(View.GONE);
            listView.setVisibility(View.GONE);
            wv.setVerticalScrollBarEnabled(false);

            wv.loadData(getString(R.string.web_start) + "<h4>" + shown.name + "</h4><br>" + shown.text + "<br>Место: " + shown.place + "<br>Сроки: " + shown.getTime() + getString(R.string.web_end), "text/html; charset=utf-8", "utf-8");


            wv.setVisibility(View.VISIBLE);
        }

    }
    public static boolean  Back2Bizniz(){
        if (wv.getVisibility() == View.VISIBLE) {
            ll.setVisibility(View.VISIBLE);
            listView.setVisibility(View.VISIBLE);
            wv.loadData("", "text/html; charset=utf-8", "utf-8");
            wv.setVisibility(View.GONE);
            return true;
        }
        else return false;
    }



    @Override
    public void displayJson(ArrayList reminders){//IOpensJson
        this.reminders = reminders;
        try {
            FileInputStream fin = ma.openFileInput(FILE_NAME);
            byte [] b = new byte[fin.available()];
            fin.read(b);
            String read = new String (b);
            String [] tmp= read.split("~");
            int id = 0;
            ArrayList ids = new ArrayList();
            for (String s : tmp) {
                if (!s.equals("")) id = Integer.parseInt(s);
                ids.add(id);
            }

            for (reminder cur:this.reminders) {
                if (ids.contains(cur.id)){this.reminders.get(this.reminders.indexOf(cur)).check = true;
                }
            }
            refresh(0);
            fin.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(ma, "Ошибка загрузки данных!", Toast.LENGTH_SHORT).show();
        }
        adapter = new MyAdapter(this.getActivity(), this, this.reminders);
        listView.setAdapter(adapter);
    }
}