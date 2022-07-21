package com.example.fahim.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


public class UpcomingMeetingAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;
    int status;

    // View lookup cache
    private static class ViewHolder
    {
        TextView meetingName,committeeName,address,status,date,time;
    }

    public UpcomingMeetingAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma)
    {
        super(context, R.layout.upcoming_meeting_cell, data);
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
        final UpcomingMeetingAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new UpcomingMeetingAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.upcoming_meeting_cell, parent, false);
            viewHolder.meetingName = (TextView) convertView.findViewById(R.id.meetingName);
            viewHolder.committeeName = (TextView) convertView.findViewById(R.id.committeeName);
            viewHolder.address = convertView.findViewById(R.id.title);
            viewHolder.status = convertView.findViewById(R.id.status);
            viewHolder.date = convertView.findViewById(R.id.fullDate);
            viewHolder.time = convertView.findViewById(R.id.name);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (UpcomingMeetingAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        try
        {

            viewHolder.meetingName.setText(meeting.getString("title"));
            viewHolder.committeeName.setText(meeting.getString("committee"));
            viewHolder.status.setText(meeting.getString("meeting_acceptance_status"));
            viewHolder.address.setText(meeting.getString("location"));

            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpledateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = simpledateformat.parse(meeting.getString("start"), pos);
            String localDate = d.toLocaleString();
            viewHolder.date.setText(localDate.split(" ")[0]+" "+localDate.split(" ")[1].toUpperCase()+" "+localDate.split(" ")[2]);
            viewHolder.time.setText(localDate.split(" ")[3].split(":")[0]+":"+localDate.split(" ")[3].split(":")[1]+" "+localDate.split(" ")[4].toUpperCase());


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        result.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try{
                    mainActivity.showMeetingDetailPage(meeting.getString("id"),meeting.getString("committee"));
                }catch (Exception e){e.printStackTrace();}


            }
        });


        return convertView;
    }

}
