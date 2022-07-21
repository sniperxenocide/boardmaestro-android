package com.example.fahim.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import org.json.JSONObject;
import java.util.ArrayList;


public class CompanyAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;

    private static class ViewHolder
    {
        TextView id,name,uid;
    }

    public CompanyAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma)
    {
        super(context, R.layout.company_list_cell, data);
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

        final JSONObject company = getItem(position);
        final CompanyAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new CompanyAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.company_list_cell, parent, false);
            viewHolder.id =  convertView.findViewById(R.id.id);
            viewHolder.name =  convertView.findViewById(R.id.name);
            viewHolder.uid =  convertView.findViewById(R.id.uid);


            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CompanyAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        try
        {
            viewHolder.id.setText(company.getString("id"));
            viewHolder.name.setText(company.getString("name"));
            viewHolder.uid.setText(company.getString("uid"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        result.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            }
        });


        return convertView;
    }
}
