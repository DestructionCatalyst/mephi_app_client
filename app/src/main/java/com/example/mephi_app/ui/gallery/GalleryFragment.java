package com.example.mephi_app.ui.gallery;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mephi_app.MainActivity;
import com.example.mephi_app.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


public class GalleryFragment extends Fragment {
    private String lnk="http://192.168.1.7:3000/home/getrem/", JSONString;
    private String lnkpost = "getrem/";
    private MyAdapter  adapter;
    private ArrayList<reminder> reminders;
    private ArrayList<reminder> show;
    public static ListView listView;
    Switch swtch;
    MainActivity ma;
    String FILE_NAME = "reminders";
    Bundle is = new Bundle();

    public static LinearLayout ll;
    public static WebView wv;


    Fragment reminder_info;
    FragmentManager myFragmentManager;
    //private static final String FILE_NAME = "data.json";

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

        ProgressTask task = new ProgressTask();
        task.execute(ma.lnkbase+lnkpost);
        //LinearLayout lin =(LinearLayout) root.findViewById(R.id.linlay);
        //lin.setVisibility(View.GONE);



        return root;


    }

    /*@Override
    public void onDestroyView() {
        super.onDestroyView();
        onSaveInstanceState(is);
    }*/


    private class ProgressTask extends AsyncTask<String, Void, String> {
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

            reminders = new ArrayList<>();

           // adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, reminders);
            //listView.setAdapter(adapter);

            open(JSONString);


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
    public void open(String jsonStr){
        reminders = JSONHelper.importFromJSON(this.getActivity(), jsonStr);
        if(reminders!=null){


            //Toast.makeText(this, "Данные восстановлены", Toast.LENGTH_LONG).show();

            try {
                FileInputStream fin = ma.openFileInput(FILE_NAME);
                byte [] b = new byte[fin.available()];//Непонятная хня
                fin.read(b);
                String read = new String (b);
                //Toast.makeText(ma, "Загружена строка: "+read, Toast.LENGTH_SHORT).show();
                String [] tmp= read.split("~");
                int id = 0;
                ArrayList ids = new ArrayList();
                for (int i=0; i<tmp.length; i++){
                    if (tmp[i]!= "")id = Integer.parseInt(tmp[i]);
                    ids.add(id);
                }
                //Toast.makeText(ma, ids.toString(), Toast.LENGTH_SHORT).show();
                for (reminder cur:reminders) {
                    if (ids.contains(cur.id)){reminders.get(reminders.indexOf(cur)).check = true;
                        //Toast.makeText(ma, "Загружен элемент "+cur.id, Toast.LENGTH_SHORT).show();
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
            adapter = new MyAdapter(this.getActivity(), this,reminders);
            listView.setAdapter(adapter);

        }
        else{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());


            builder1.setMessage("Не удалось преобразовать в JSON!")
                    .setTitle("Сообщение!");


            AlertDialog dialog1 = builder1.create();
            dialog1.show();
        }

    }
    public void refresh(int callType){
        String bigZalupa = "";
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
                //Toast.makeText(ma, "Сохранено "+write, Toast.LENGTH_SHORT).show();
                fos.write(write.getBytes());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(ma, "Ошибка сохранения данных!", Toast.LENGTH_SHORT).show();
            }
        }
        /*for (reminder cur : reminders) {
            String zalupa;
            if (cur.check){zalupa="true";}else zalupa="false";
            bigZalupa+=zalupa+" ";
        }
        Toast.makeText(ma, bigZalupa,
                Toast.LENGTH_SHORT).show();*/
    }

    public void  ReplaceMeDaddy(reminder shown){

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


}