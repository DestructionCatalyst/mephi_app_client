package com.example.mephi_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mephi_app.ui.gallery.GalleryFragment;
import com.example.mephi_app.ui.home.HomeFragment;
import com.example.mephi_app.ui.send.SendFragment;
import com.google.android.material.navigation.NavigationView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MyLogs";
    private AppBarConfiguration mAppBarConfiguration;
    public group curGroup;
    String JSONString;
    public ArrayList<group> groups;
    //private String lnk="http://192.168.1.7:3000/home/getgroups/";
    public String lnkbase="http://192.168.1.7:3000/home/";
    private String lnkpost="getgroups/";
    private String FILE_NAME = "group";
    public boolean firstLaunch = false;
    private FragmentManager myFragmentManager;
    public boolean showingQR;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFragmentManager = this.getSupportFragmentManager();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);





        //FloatingActionButton fab = findViewById(R.id.fab);
       /* fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_reminders, R.id.nav_navigation, R.id.nav_qr,
                R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        ProgressTask task = new ProgressTask();
        task.execute(lnkbase+lnkpost);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);
        FragmentTransaction fragmentTransaction = myFragmentManager
                .beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        return  true;
    }*/



    @Override
    public void onBackPressed() {
        /*if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {*/
        boolean flag = false;
        try {
            flag = GalleryFragment.Back2Bizniz();
        }
        catch (NullPointerException e){}
        try{
            flag = HomeFragment.Back2Bizniz();
        }
        catch (NullPointerException ex){}
        if (showingQR){
            flag = true;
            SendFragment.closeQR();
            showingQR = false;
        }
           if (!flag)finish();
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(LOG_TAG, "onRestoreInstanceState");
    }

    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume ");
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState");
    }

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

            if (content == null)swearAndExit();


            groups = new ArrayList<>();

            open(JSONString);


        }

        private String getContent(String path) throws IOException {
            BufferedReader reader=null;
            try {
                URL url=new URL(path);
                HttpURLConnection c=(HttpURLConnection)url.openConnection();
                c.setRequestMethod("GET");
                c.setReadTimeout(5000);
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

                return null;
            }
            finally {
                if (reader != null) {
                    reader.close();
                }
            }

        }
    }
    public void open(String jsonStr){
        groups = JSONHelper.importFromJSON(this, jsonStr);
        try{
        groups.add(0,(new group(0,"(Гость)",0)));
        readFile();

        HomeFragment.open();
       } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  void readFile(){
        String read = "";
        boolean flag = false, found = false;
        //curGroup = new group(0,"(Гость)",0);
        try {
            FileInputStream fin = this.openFileInput(FILE_NAME);
            byte [] b = new byte[fin.available()];
            fin.read(b);
            read = new String (b);
            //Toast.makeText(this, read, Toast.LENGTH_SHORT).show();
            fin.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            flag = true;
        }
        catch (IOException e) {
            e.printStackTrace();
            //Toast.makeText(this, "Ошибка загрузки данных!", Toast.LENGTH_SHORT).show();
        }
        if ((flag)||(read == "")||(read == "(Гость)")){
            curGroup = new group(0,"(Гость)",0);
        }
        else {
            for (group tmp: groups) {
               if (tmp.name.equalsIgnoreCase(read)) {
                   curGroup = tmp;
                   found = true;

                   //Toast.makeText(this, "Найдено!", Toast.LENGTH_SHORT).show();
               }
            }
            if (!found){curGroup = new group(0,"(Гость)",0);}
            //else Toast.makeText(this, "Непустая группа!", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, "Группа: "+curGroup.name+", Институт: "+curGroup.idInst, Toast.LENGTH_SHORT).show();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
    private void swearAndExit(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Нет подключения к серверу. Проверьте, подключено ли ваше устройство к мобильной или WI-Fi сети.")
                .setTitle("Проверьте подключение к Интернету")
                .setCancelable(false)
                .setNegativeButton("Выход",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                killApp();
                            }
                        });
        AlertDialog dialog1 = builder1.create();
        dialog1.show();
        //finish();
    }
    private void killApp(){
        try {
            Thread.sleep(1000);
            System.exit(404);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
