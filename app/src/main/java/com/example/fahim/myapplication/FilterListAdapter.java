package com.example.fahim.myapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;


public class FilterListAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;
    int status;


    public class component{
        ImageView tick;
        int clicked = -1;
    }
    public ArrayList<component> arrayList = new ArrayList<>();

    // View lookup cache
    private static class ViewHolder
    {
        TextView name;
        ImageView tick;
    }

    public FilterListAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma)
    {
        super(context, R.layout.filter_cell, data);
        this.dataSet = data;
        this.mContext=context;
        mainActivity = ma;
    }


    @Override
    public void onClick(View view) {
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final JSONObject file = getItem(position);
        final FilterListAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new FilterListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.filter_cell, parent, false);
            viewHolder.name =  convertView.findViewById(R.id.name);
            viewHolder.tick =  convertView.findViewById(R.id.tick);


            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FilterListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        try
        {
            viewHolder.name.setText(file.getString("title"));

            component c = new component();
            c.clicked = -1;
            c.tick = viewHolder.tick;
            arrayList.add(c);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        result.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                component c = arrayList.get(position);
                if(c.clicked == 1)
                {
                    c.tick.setVisibility(View.INVISIBLE);
                    c.clicked = -1;
                }
                else {
                    c.tick.setVisibility(View.VISIBLE);
                    c.clicked = 1;
                }
            }
        });


        return convertView;
    }
}
