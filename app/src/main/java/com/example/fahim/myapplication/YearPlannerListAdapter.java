
package com.example.fahim.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;


public class YearPlannerListAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;
    int status;

    // View lookup cache
    private static class ViewHolder
    {
        TextView name, date, time, committee_name , location;
    }

    public YearPlannerListAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma)
    {
        super(context, R.layout.year_planner_cell, data);
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

        final JSONObject meeting = getItem(position);
        final YearPlannerListAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new YearPlannerListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.year_planner_cell, parent, false);
            viewHolder.name =  convertView.findViewById(R.id.name);
            viewHolder.date =  convertView.findViewById(R.id.date);
            viewHolder.time =  convertView.findViewById(R.id.time);
            viewHolder.committee_name =  convertView.findViewById(R.id.committee);
            viewHolder.location =  convertView.findViewById(R.id.location);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (YearPlannerListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        try
        {
            viewHolder.name.setText(meeting.getString("title"));
            viewHolder.committee_name.setText(meeting.getJSONObject("committee").getString("title"));
            viewHolder.location.setText(meeting.getString("location"));
            String data = meeting.getString("start");
            viewHolder.date.setText(data.split(" ")[0]);
            viewHolder.time.setText(data.split(" ")[1]);
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
