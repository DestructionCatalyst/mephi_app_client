package com.example.mephi_app.ui.tools;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.mephi_app.MainActivity;
import com.example.mephi_app.R;
import com.example.mephi_app.group;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_PRIVATE;

public class ToolsFragment extends Fragment {

    private ToolsViewModel toolsViewModel;

    private ArrayAdapter<String> adapter;
    private Spinner spinner;
    private MainActivity ma;

    private String FILE_NAME = "group";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(ToolsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tools, container, false);
        final TextView textView = root.findViewById(R.id.textView2);
        textView.setText("Группа");

        final TextView about = root.findViewById(R.id.about);
        about.setText("О приложении");

        about.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showMessage();
            }
        });

        final TextView sup = root.findViewById(R.id.support);
        sup.setText("Техподдержка");
        sup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMail();
            }
        });

        spinner = root.findViewById(R.id.spinner);

        if (getActivity() != null) { ma = (MainActivity ) getActivity(); }

        open();
        return root;
    }

    private void showMessage(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setMessage("Приложение «Путеводитель по НИЯУ МИФИ»\n" +
                "Версия " +getString(R.string.app_version)+"\n\n"+
                "©\n" +
                "Национальный исследовательский ядерный университет «МИФИ»,\n" +
                "Институт Интеллектуальных Кибернетических Систем (ИИКС)\n" +
                "Кафедра №22 «Кибернетика»\n" +
                "Разработано в рамках курса «Проектная практика»\n" +
                "Руководитель проекта: Немешаев Сергей Александрович\n" +
                "Куратор проекта: Дадтеев Казбек Маирбекович\n" +
                "Разработчик: Комза Владислав Петрович")
                .setTitle("О программе")
                .setPositiveButton("OK", null);
        AlertDialog dialog1 = builder1.create();
        dialog1.show();
    }

    private void openMail(){
        try
        {
            Intent contactintent = new Intent(Intent.ACTION_SENDTO);
            contactintent.setData(android.net.Uri.parse("mailto:" + "mephiapp@gmail.com"));

            startActivity(contactintent);

        }
        catch (ActivityNotFoundException anfe)
        {
            Toast.makeText(ma, "На устройстве не найден почтовый клиент", Toast.LENGTH_SHORT).show();
        }
    }

    private void open(){
            try {
                adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_spinner_item, ma.groups);
                adapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
                spinner.setAdapter(adapter);
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
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                };
                spinner.setOnItemSelectedListener(itemSelectedListener);


            } catch (Exception e) {
                Toast.makeText(ma, "Проверьте соединение с Интернетом!",
                        Toast.LENGTH_SHORT).show();
                //e.printStackTrace();
            }



    }
}