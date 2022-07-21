package com.example.fahim.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class OngoingAgendaAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;
    public int agenda_id ;
    boolean[] checked;
    ConstraintLayout[] layouts;

    private static class ViewHolder
    {
        TextView serial, name;
        ConstraintLayout container;
    }

    public OngoingAgendaAdapter(ArrayList<JSONObject> data, Context context, final MainActivity ma , int id)
    {
        super(context, R.layout.ongoing_agenda_cell, data);
        this.dataSet = data;
        this.mContext=context;
        mainActivity = ma;
        agenda_id = id;

        checked = new boolean[dataSet.size()];
        layouts = new ConstraintLayout[dataSet.size()];

    }


    @Override
    public void onClick(View view) {
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final JSONObject agenda = getItem(position);
        final OngoingAgendaAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new OngoingAgendaAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.ongoing_agenda_cell, parent, false);

            viewHolder.container =  convertView.findViewById(R.id.container);
            viewHolder.serial =  convertView.findViewById(R.id.serial);
            viewHolder.name =  convertView.findViewById(R.id.name);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OngoingAgendaAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;


        try
        {
            viewHolder.serial.setText(agenda.getString("serial_no"));
            viewHolder.name.setText(agenda.getString("title"));

            try{
                if(agenda.getString("status").equals("started")) viewHolder.container.setBackgroundResource(R.drawable.ongoing_agenda_back_red);
                else viewHolder.container.setBackgroundResource(R.drawable.ongoing_agenda_back_green);
            }catch (Exception e){e.printStackTrace();}
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    WebView webView = mainActivity.findViewById(R.id.description);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.loadDataWithBaseURL(null, agenda.getString("description"), "text/html", "utf-8", null);
                }catch (Exception e){e.printStackTrace();}


                ListView listFile = mainActivity.findViewById(R.id.listFile);
                ArrayList<JSONObject> list = new ArrayList<>();
                try{
                    JSONArray files = agenda.getJSONArray("files");
                    for ( int i=0;i<files.length();i++) list.add(files.getJSONObject(i));
                } catch (Exception e){e.printStackTrace();}
                try{
                    LibraryFileAdapter adapter = new LibraryFileAdapter(list, mainActivity.getApplicationContext(),mainActivity, dataSet);
                    listFile.setAdapter(adapter);
                } catch (Exception e){e.printStackTrace();}
            }
        });

        return convertView;
    }



}
