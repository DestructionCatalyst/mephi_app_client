package com.example.mephi_app.ui.gallery;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;




import com.example.mephi_app.MainActivity;
import com.example.mephi_app.R;

public class MyAdapter extends BaseAdapter {
    MainActivity ctx;
    LayoutInflater lInflater;
    ArrayList<reminder> objects;
    CheckBox cbBuy;
    GalleryFragment fragment;
    FragmentManager myFragmentManager;
    Fragment reminder_info;

    MyAdapter(Context context, GalleryFragment frag, ArrayList<reminder> products) {
        ctx = (MainActivity) context;
        objects = products;
        fragment = frag;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item, parent, false);
        }

        reminder rem = getReminder(position);

       /* String zalupa;
        if (rem.check){zalupa="true";}else zalupa="false";
        */
        ((TextView) view.findViewById(R.id.tvDescr)).setText(rem.name);
        ((TextView) view.findViewById(R.id.tvPrice)).setText(rem.getTime());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.ReplaceMeDaddy(getReminder(position));

            }
        });

        cbBuy = (CheckBox) view.findViewById(R.id.cbBox);
        // присваиваем чекбоксу обработчик
        cbBuy.setOnCheckedChangeListener(myCheckChangeList);
        // пишем позицию
        cbBuy.setTag(position);
        // заполняем данными из напоминаний
        cbBuy.setChecked(rem.check);
        return view;
    }

    // напом. по позиции
    reminder getReminder(int position) {
        return ((reminder) getItem(position));
    }


    // обработчик для чекбоксов
    OnCheckedChangeListener myCheckChangeList = new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView,
                                     boolean isChecked) {

            getReminder((Integer) buttonView.getTag()).check = isChecked;
            fragment.refresh(1);
        }
    };
}
