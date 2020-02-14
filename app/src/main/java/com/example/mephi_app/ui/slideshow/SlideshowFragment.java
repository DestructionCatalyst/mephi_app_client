package com.example.mephi_app.ui.slideshow;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.mephi_app.MainActivity;
import com.example.mephi_app.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class SlideshowFragment extends Fragment {

    private SlideshowViewModel slideshowViewModel;
    //Spinner spinner_from;
    //EditText text_from;
    ArrayList<String> tmp;
    ArrayList<dot> dots;
    ArrayList<way> ways;
    ArrayAdapter adapter;
    MainActivity ma;
    String lnkpost = "getdots/", lnkpost1 = "getways/";
    String JSONString;
    private ArrayAdapter adapter1;
    AutoCompleteTextView actv,actv1;
    Button buttoff;
    double [] [] tab;
    int dotn;
    TextView textView;
    LineView gfx;
    ArrayList<dot> show;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        //final TextView textView = root.findViewById(R.id.text_slideshow);
        slideshowViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });
        tmp = new ArrayList<>();
        tmp.add("Проходная");
        tmp.add("Корпус Г, центр.вход");
        if (getActivity() != null) { ma = (MainActivity) getActivity(); }
        actv = root.findViewById(R.id.auto_text);
        actv1 = root.findViewById(R.id.auto_text1);
        buttoff = root.findViewById(R.id.button);
        textView = root.findViewById(R.id.textView6);
        gfx = root.findViewById(R.id.draw_field);

        ProgressTask task = new ProgressTask();
        task.execute(ma.lnkbase+lnkpost);
        ProgressTask task1 = new ProgressTask();
        task1.execute(ma.lnkbase+lnkpost1);


        buttoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fromstr = actv.getText().toString();
                String tostr = actv1.getText().toString();
                int from=0, to=0;
                ArrayList points = new ArrayList();
                //Log.d("Pathfinder","---"+fromstr);
                //Log.d("Pathfinder","---"+tostr);
                for (dot d:dots) {
                    if (d.name != null) {
                        if (d.name.equals(fromstr)) {
                            from = d.id;
                            //Log.d("Pathfinder",d.name+" - "+d.id);
                        }
                        if (d.name.equals(tostr)) {
                            to = d.id;
                            //Log.d("Pathfinder",d.name+" - "+d.id);
                        }
                        //Log.d("Pathfinder",""+d.id);
                    }
                }
                int [] way = findaway(from-1,to-1);
                if(way != null) {
                    String wayStr = "";
                    if (from == to){
                        points.add(dots.get(from - 1).x-3);
                        points.add(dots.get(from - 1).y-3);
                        points.add(dots.get(from - 1).x+3);
                        points.add(dots.get(from - 1).y-3);
                        points.add(dots.get(from - 1).x+3);
                        points.add(dots.get(from - 1).y+3);
                        points.add(dots.get(from - 1).x-3);
                        points.add(dots.get(from - 1).y+3);
                        Log.d("Pathfinder", "Стоим на месте!");
                    }
                    else {
                        if (from - 1 == 0) {
                            Log.d("Pathfinder", "" + dots.get(from - 1));
                            wayStr += dots.get(from - 1) + ">";
                            points.add(dots.get(from - 1).x);
                            points.add(dots.get(from - 1).y);
                        }
                        for (int j = 0; j < way.length; j++) {
                            if (way[j] > 0) {
                                Log.d("Pathfinder", "" + dots.get(way[j]));
                                wayStr += dots.get(way[j]) + ">";
                                points.add(dots.get(way[j]).x);
                                points.add(dots.get(way[j]).y);
                            }
                        }
                        Log.d("Pathfinder", "" + dots.get(to - 1));
                        points.add(dots.get(to - 1).x);
                        points.add(dots.get(to - 1).y);
                        wayStr += dots.get(to - 1);
                        textView.setText(wayStr);


                    }
                    float[] floatArray = new float[points.size()];
                    for (int index = 0; index < points.size(); index++) {
                        floatArray[index] = (int) points.get(index);
                    }
                    gfx.MyInvalidate(floatArray);
                    ma.hideKeyboard();
                }
                else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Неверно указана начальная и/или конечная точка!")
                            .setTitle("Ошибка!")
                            .setPositiveButton("ОК", null);

                    AlertDialog dialog1 = builder1.create();
                    dialog1.show();
                }
            }
        });

        return root;
    }

    private class ProgressTask extends AsyncTask<String, Void, String> {
        String pth;
        @Override
        protected String doInBackground(String... path) {
            pth = path[0];

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
        @Override
        protected void onPostExecute(String content) {
            JSONString = content;
            /*AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage(content)
                    .setTitle("Content");
            AlertDialog dialog1 = builder1.create();
            dialog1.show();*/

            //newsArrayList = new ArrayList<>();

            if (pth.endsWith("getdots/")) {
                openJSON(JSONString);
            }
            else {
                ways = JSONHelper.importWaysFromJSON(ma, JSONString);
                //Toast.makeText(ma, ways.get(0).toString(), Toast.LENGTH_LONG).show();
                makeTable();
            }


        }

        private String getContent(String path) throws IOException {
            BufferedReader reader=null;
            try {
                URL url=new URL(path);
                HttpURLConnection c=(HttpURLConnection)url.openConnection();
                c.setRequestMethod("GET");
                c.setConnectTimeout(30000);
                c.setReadTimeout(50000);
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
        dots = JSONHelper.importFromJSON(this.getActivity(), jsonStr);
        if(dots!=null){
            //Toast.makeText(this, "Данные восстановлены", Toast.LENGTH_LONG).show();
            for (int i = 0; i<dots.size(); i++){
               Log.d("dots", " "+dots.get(i).toString());
            }
            show = new ArrayList<>();
            for (dot d:dots) {
                if (d.name != null){show.add(d);}
            }
            adapter1 = new ArrayAdapter(ma, android.R.layout.simple_spinner_item, show);
            //spinner_from.setAdapter(adapter1);
            actv.setAdapter(adapter1);
            actv1.setAdapter(adapter1);

        }
        else{
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());


            builder1.setMessage("Не удалось преобразовать в JSON!")
                    .setTitle("Сообщение!");


            AlertDialog dialog1 = builder1.create();
            dialog1.show();
        }

    }

    private void makeTable(){
        dotn = dots.size();
        tab = new double [dotn][dotn];
        for (int i = 0;i<dotn; i++){
            for (int j = 0; j<dotn;j++) {
                tab[i][j] = Double.MAX_VALUE/4-1;
            }

        }
        for (way w:ways) {
            try{
                tab[w.idStart-1][w.idEnd-1] = w.len;
                tab[w.idEnd-1][w.idStart-1] = w.len;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = 0;i<dotn; i++){
            for (int j = 0; j<dotn;j++) {
                Log.d("ways_tab",i+"x"+j+":"+tab[i][j]);
            }

        }
    }

    private int [] findaway(int from, int to){
        Log.d("Pathfinder", "from = "+from+" , to = "+to);
        if ((from < 0)||(to < 0)){
            Log.d("Pathfinder", "В Саратовской области найден таинственный автобус, едущий в никуда");
            return null;
        }
        else if (from == to){
            int [] arr = new int[2];
            arr[0] = from;
            arr[1] = to;
            return arr;
        }
        else{
            int unfin = dotn;
            boolean [] finish = new boolean[dotn];
            double [] dist = new double[dotn];
            int [] prev = new int[dotn];
            int [] way = new int[dotn];
            double mindist = Double.MAX_VALUE/4-1;
            int k = -1, f=-1;
            boolean [] localfinish;
            int localunfin;

            Arrays.fill(dist, Double.MAX_VALUE/4-1); // устанаавливаем расстояние до всех вершин INF
            Arrays.fill(prev, 0);
            Arrays.fill(way, 0);
            dist[from] = 0; // для начальной вершины положим 0

            for (;;) {
                int v = -1;
                for (int nv = 0; nv < dotn; nv++) // перебираем вершины
                    if (!finish[nv] && dist[nv] < Double.MAX_VALUE/4-1 && (v == -1 || dist[v] > dist[nv]) ) // выбираем самую близкую непомеченную вершину
                        v = nv;
                        if (v == -1) break; // ближайшая вершина не найдена
                finish[v] = true; // помечаем ее
                for (int nv = 0; nv < dotn; nv++)
                    if (!finish[nv] && tab[v][nv] < Double.MAX_VALUE/4-1 ) { // для всех непомеченных смежных
                        if (dist[nv]>dist[v] + tab[v][nv]) {
                            dist[nv] = dist[v] + tab[v][nv];// улучшаем оценку расстояния (релаксация)
                            prev[nv] = v;
                        }
                    }


            }
            int z = to, i = 0;
            do{
                way[i] = prev [z];
                z = prev[z];
                i++;
            }
            while (z != from);
            int tmp;
            for (int j = 0; j<way.length/2; j++) {
                tmp = way[j];
                way[j] = way[way.length-1-j];
                way[way.length-1-j]=tmp;
            }
            if (from == 0)Log.d("Pathfinder", ""+dots.get(from));
            for (int j = 0; j<way.length;j++) {
                if (way[j]>0)
                    Log.d("Pathfinder", ""+dots.get(way[j]));
            }
            Log.d("Pathfinder", ""+dots.get(to));
            Log.d("Pathfinder", "-------------------");
            Log.d("Pathfinder", ""+dist[9]);
            Log.d("Pathfinder", "-------------------");
            return way;
        }

    }

}