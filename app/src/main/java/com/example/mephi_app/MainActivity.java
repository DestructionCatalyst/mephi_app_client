package com.example.mephi_app;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mephi_app.ui.gallery.GalleryFragment;
import com.example.mephi_app.ui.home.HomeFragment;
import com.example.mephi_app.ui.send.SendFragment;
import com.google.android.material.navigation.NavigationView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IOpensJson {

    public final String lnkbase="http://194.87.232.95:45555/home/";
    private final String lnkpost="getgroups/";
    private final String FILE_NAME = "group";


    private AppBarConfiguration mAppBarConfiguration;

    public group curGroup;
    public ArrayList<group> groups;


    public boolean firstLaunch = false;
    public boolean showingQR;
    public boolean offline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        loadGroups();
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
            flag = flag|HomeFragment.Back2Bizniz();
        }
        catch (NullPointerException ex){}
        if (showingQR){
            flag = flag|true;
            SendFragment.closeQR();
            showingQR = false;
        }
           if (!flag)killApp();
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("MyLogs", "onRestoreInstanceState");
    }

    protected void onResume() {
        super.onResume();
        Log.d("MyLogs", "onResume ");
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("MyLogs", "onSaveInstanceState");
    }

    public void open(String jsonStr){//IOpensJson

        JSONHelper helper1 = new JSONHelper(this, new GroupsJSONHelper());
        helper1.execute(jsonStr);
        offline = false;
    }

    @Override
    public void swear(String swearing) {//IOpensJson
        //HomeFragment swears instead!
        curGroup = new group(0,"(Гость)",0);
        groups = new ArrayList<group>();
        groups.add(curGroup);
        offline = true;
    }

    @Override
    public void displayJson(ArrayList groups) {//IOpensJson
        this.groups = groups;
        try{
            groups.add(0,(new group(0,"(Гость)",0)));
            readFile();
            HomeFragment.openGroups();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadGroups(){
        NetworkTask task1 = new NetworkTask(this);
        task1.execute(lnkbase+lnkpost);
    }

    private  void readFile(){
        String read = "";
        boolean flag = false, found = false;
        try {
            FileInputStream fin = this.openFileInput(FILE_NAME);
            byte [] b = new byte[fin.available()];
            fin.read(b);
            read = new String (b);
            fin.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            flag = true;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if ((flag)||(read.equals(""))||(read.equals("(Гость)"))){
            curGroup = new group(0,"(Гость)",0);
        }
        else {
            for (group tmp: groups) {
               if (tmp.name.equalsIgnoreCase(read)) {
                   curGroup = tmp;
                   found = true;
               }
            }
            if (!found){curGroup = new group(0,"(Гость)",0);}
        }
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }


    private void killApp(){
        try {
            Thread.sleep(250);
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
