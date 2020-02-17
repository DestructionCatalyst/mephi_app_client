package com.example.mephi_app.ui.slideshow;

import android.app.AlertDialog;
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
import com.example.mephi_app.ui.IOpensJson;
import com.example.mephi_app.ui.JSONHelper;
import com.example.mephi_app.ui.NetworkTask;

import java.util.ArrayList;
import java.util.Arrays;

public class SlideshowFragment extends Fragment implements IOpensJson {

    private final String lnkpost = "getdots/", lnkpost1 = "getways/";

    private SlideshowViewModel slideshowViewModel;

    private ArrayList<dot> dots;
    private ArrayList<way> ways;
    private ArrayList<dot> show;

    private MainActivity ma;

    private double [] [] tab;
    private int dotn;

    private AutoCompleteTextView actv,actv1;
    private ArrayAdapter adapter1;
    private Button buttoff;
    private TextView textView;
    private LineView gfx;

    private boolean dotsLoaded = false, dotsDecoded = false;


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

        if (getActivity() != null) { ma = (MainActivity) getActivity(); }
        actv = root.findViewById(R.id.auto_text);
        actv1 = root.findViewById(R.id.auto_text1);
        buttoff = root.findViewById(R.id.button);
        textView = root.findViewById(R.id.textView6);
        gfx = root.findViewById(R.id.draw_field);

        NetworkTask task2 = new NetworkTask(this);
        task2.execute(ma.lnkbase+lnkpost);

        NetworkTask task4 = new NetworkTask(this);
        task4.execute(ma.lnkbase+lnkpost1);



        buttoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ma.offline) {
                    String fromstr = actv.getText().toString();
                    String tostr = actv1.getText().toString();
                    int from = 0, to = 0;
                    ArrayList points = new ArrayList();

                    for (dot d : dots) {
                        if (d.name != null) {
                            if (d.name.equals(fromstr)) {
                                from = d.id;
                            }
                            if (d.name.equals(tostr)) {
                                to = d.id;
                            }
                        }
                    }

                    int[] way = findaway(from - 1, to - 1);
                    if (way != null) {
                        String wayStr = "";
                        if (from == to) {//Никуда не идём
                            points.add(dots.get(from - 1).x - 3);
                            points.add(dots.get(from - 1).y - 3);
                            points.add(dots.get(from - 1).x + 3);
                            points.add(dots.get(from - 1).y - 3);
                            points.add(dots.get(from - 1).x + 3);
                            points.add(dots.get(from - 1).y + 3);
                            points.add(dots.get(from - 1).x - 3);
                            points.add(dots.get(from - 1).y + 3);
                            Log.d("Pathfinder", "Стоим на месте!");
                        } else {
                            if (from - 1 == 0) {//Если стартуем с проходной, обраб. отдельно
                                Log.d("Pathfinder", "" + dots.get(from - 1));
                                wayStr += dots.get(from - 1) + ">";
                                points.add(dots.get(from - 1).x);
                                points.add(dots.get(from - 1).y);
                            }
                            for (int j = 0; j < way.length; j++) {//Проходим по пути
                                if (way[j] > 0) {
                                    Log.d("Pathfinder", "" + dots.get(way[j]));
                                    wayStr += dots.get(way[j]) + ">";
                                    points.add(dots.get(way[j]).x);
                                    points.add(dots.get(way[j]).y);
                                }
                            }
                            Log.d("Pathfinder", "" + dots.get(to - 1));
                            points.add(dots.get(to - 1).x);//Добавляем последнюю точку
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
                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setMessage("Неверно указана начальная и/или конечная точка!")
                                .setTitle("Ошибка!")
                                .setPositiveButton("ОК", null);

                        AlertDialog dialog1 = builder1.create();
                        dialog1.show();
                    }
                }
            }
        });

        return root;
    }

    @Override
    public void open(String jsonStr) {
        if(!dotsLoaded){
            dotsLoaded = true;
            JSONHelper helper1 = new JSONHelper(this, new DotJSONHelper());
            helper1.execute(jsonStr);
        }
        else{
            JSONHelper helper1 = new JSONHelper(this, new WayJSONHelper());
            helper1.execute(jsonStr);
        }
        ma.offline = false;
    }

    @Override
    public void swear(String swearing) {
        if(!dotsLoaded) {
            dotsLoaded = true;
            String fullSwearing = "Ошибка открытия навигации. " + swearing;
            Log.d("Connection", fullSwearing);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
            builder1.setMessage(fullSwearing)
                    .setTitle("Ошибка!")
                    .setPositiveButton("OK", null);
            AlertDialog dialog1 = builder1.create();
            dialog1.show();
        }
    }

    @Override
    public void displayJson(ArrayList a) {
        if(!dotsDecoded){
            dotsDecoded = true;
            this.dots = a;
            if(dots!=null){
                for (int i = 0; i<dots.size(); i++){
                    Log.d("dots", " "+dots.get(i).toString());
                }
                show = new ArrayList<>();
                for (dot d:dots) {
                    if (d.name != null){show.add(d);}
                }
                adapter1 = new ArrayAdapter(ma, android.R.layout.simple_spinner_item, show);
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
        else{
            this.ways = a;
            makeTable();
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