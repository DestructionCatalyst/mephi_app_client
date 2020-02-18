package com.example.mephi_app.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class LoadErrorMessage extends TextView {
    public static final int LOAD_PROGRESS = 1;
    public static final int LOAD_ERROR = 2;
    public static final int LOAD_FINISHED = 0;

    public LoadErrorMessage(Context context){
        super(context);
        //changeStatus(1);
    }
    public LoadErrorMessage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void changeStatus(int newStatus){
        if (newStatus == LOAD_FINISHED)this.setVisibility(View.GONE);

        else if (newStatus == LOAD_PROGRESS){
            this.setVisibility(View.VISIBLE);
            this.setText("Загрузка...");
        }

        else if (newStatus == LOAD_ERROR){
            this.setVisibility(View.VISIBLE);
            this.setText("Ошибка загрузки данных! Проверьте подключение к Интернету!");
            Log.d("LoadErrorMessage", "Error message displayed!");
        }
    }


}
