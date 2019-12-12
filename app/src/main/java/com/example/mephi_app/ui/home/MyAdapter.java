package com.example.mephi_app.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.mephi_app.R;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<news> objects;
    CheckBox cbBuy;
    HomeFragment fragment;

    MyAdapter(Context context, HomeFragment frag, ArrayList<news> products) {
        ctx = context;
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
            view = lInflater.inflate(R.layout.newsitem, parent, false);
        }

        news pnews = getNews(position);

       /* String zalupa;
        if (rem.check){zalupa="true";}else zalupa="false";
        */
        ((TextView) view.findViewById(R.id.tvName)).setText(pnews.name);
        ((TextView) view.findViewById(R.id.tvTime)).setText(pnews.t);
        ((TextView) view.findViewById(R.id.tvPlace)).setText(pnews.place);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.ReplaceMeDaddy(getNews(position));

            }
        });

        return view;
    }

    // напом. по позиции
    news getNews(int position) {
        return ((news) getItem(position));
    }




}
