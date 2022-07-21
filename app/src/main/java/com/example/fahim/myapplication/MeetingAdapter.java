package com.example.fahim.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class MeetingAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    private MainActivity mainActivity;
    int status;
    String committee_name;
    int committee_id;
    boolean flag = true;

    boolean[] checked;
    ImageView[] imageviews;

    // View lookup cache
    private static class ViewHolder
    {
        TextView meetingName,committeeName,status1,status2, mDate, mMonthYear,mTime;
        ImageView tick;
    }

    public MeetingAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma,String committee_name,int committeeID)
    {
        super(context, R.layout.meeting_cell, data);
        this.dataSet = data;
        this.mContext=context;
        mainActivity = ma;
        this.committee_name = committee_name;
        this.committee_id = committeeID;

        checked = new boolean[dataSet.size()];
        imageviews = new ImageView[dataSet.size()];


        try
        {
            if (data.isEmpty())
            {
                ListView l = mainActivity.findViewById(R.id.listFile);
                l.setAdapter(null);
            }
        } catch (Exception e){e.printStackTrace();}
    }


    @Override
    public void onClick(View view) {
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final JSONObject meeting = getItem(position);
        final MeetingAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new MeetingAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.meeting_cell, parent, false);
            viewHolder.meetingName = (TextView) convertView.findViewById(R.id.meetingName);
            viewHolder.committeeName = (TextView) convertView.findViewById(R.id.committeeName);
            viewHolder.status1 = convertView.findViewById(R.id.status1);
            viewHolder.status2 = convertView.findViewById(R.id.status);
            viewHolder.mDate = convertView.findViewById(R.id.category);
            viewHolder.mMonthYear = convertView.findViewById(R.id.monthYear);
            viewHolder.mTime = convertView.findViewById(R.id.mTime);
            viewHolder.tick = convertView.findViewById(R.id.tick);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MeetingAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        if(checked[position]) viewHolder.tick.setVisibility(View.VISIBLE);
        else viewHolder.tick.setVisibility(View.INVISIBLE);



        try
        {

            viewHolder.meetingName.setText(meeting.getString("title"));
            viewHolder.committeeName.setText(committee_name);
            viewHolder.status1.setText(meeting.getString("status"));
            if(meeting.getString("status").equals("completed"))  viewHolder.status1.setTextColor(Color.GREEN);
            else if(meeting.getString("status").equals("published")) viewHolder.status1.setTextColor(mainActivity.getResources().getColor(R.color.skyblue));
            else if(meeting.getString("status").equals("started")) viewHolder.status1.setTextColor(Color.BLUE);

            viewHolder.status2.setText(meeting.getString("meeting_acceptance_status"));
            if(viewHolder.status2.getText().toString().equals("Invited")) viewHolder.status2.setBackgroundResource(R.drawable.status_background_blue);


            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpledateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date d = simpledateformat.parse(meeting.getString("start"), pos);
            String localDate = d.toLocaleString();
            viewHolder.mDate.setText(localDate.split(" ")[0]);
            viewHolder.mMonthYear.setText(localDate.split(" ")[1].toUpperCase()+" "+localDate.split(" ")[2]);
            viewHolder.mTime.setText(localDate.split(" ")[3].split(":")[0]+":"+localDate.split(" ")[3].split(":")[1]+" "+localDate.split(" ")[4].toUpperCase());

            ListView listView = mainActivity.findViewById(R.id.listFile);
            if (flag && listView!=null) {
                flag = false;
                clickListenerFunction(meeting,viewHolder,position);
            }
            imageviews[position] = viewHolder.tick;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        result.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickListenerFunction(meeting,viewHolder,position);
            }
        });

        return convertView;
    }

    public void clickListenerFunction(final JSONObject meeting , ViewHolder viewHolder, int position)
    {
        ListView listView = mainActivity.findViewById(R.id.listFile);
        if(listView == null)       // meeting detail page
        {
            try{
                mainActivity.showMeetingDetailPage(meeting.getString("id"),committee_name);
            }catch (Exception e){e.printStackTrace();}
        }
        else          // file pack page
        {
//            for(ImageView i:imageViews) i.setVisibility(View.INVISIBLE);
//            viewHolder.tick.setVisibility(View.VISIBLE);

            for(int i=0;i<checked.length;i++)
            {
                checked[i]=false;
                if(imageviews[i]!=null) imageviews[i].setVisibility(View.INVISIBLE);
            }
            viewHolder.tick.setVisibility(View.VISIBLE);
            checked[position] = true;

            try {
                new filepackListThread(meeting.getString("file_pack_id")).execute("");
            }catch (Exception e){e.printStackTrace();}

        }
    }

    static  class agendaListThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        String meetingId;
        String meetingStatus;
        MainActivity mainActivity;

        agendaListThread(String s, MainActivity ma , String st)
        {
            meetingId = s;
            this.mainActivity = ma;
            meetingStatus = st;
        }
        @Override
        protected String doInBackground(String ...params)
        {
            if(mainActivity.checkConnection())
            {
                try{
                    JSONObject agenda = mainActivity.getData(mainActivity.currentCompany.getString("protocol")+mainActivity.currentCompany.getString("domain")
                            +mainActivity.meetingAgendaAPI + meetingId);

                    mainActivity.dbHelper.insertData(mainActivity.meetingAgendaAPI+meetingId , agenda.toString());
                }catch (Exception e){e.printStackTrace();}
            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();

            ListView listAgendaA = mainActivity.findViewById(R.id.listAgenda);
            ListView listAgendaB = mainActivity.findViewById(R.id.listOngoingAgenda);
            ArrayList<JSONObject> listA =new ArrayList<>();
            ArrayList<JSONObject> listB =new ArrayList<>();
            try{
                JSONArray agendas = (new JSONObject( mainActivity.dbHelper.getData(mainActivity.meetingAgendaAPI + meetingId) )).getJSONArray("data");
                mainActivity.currentAgendaId = "";
                for(int i=0;i<agendas.length();i++){
                    if(meetingStatus.equals("completed")){
                        listA.add(agendas.getJSONObject(i));
                    }
                    else
                    {
                        if(agendas.getJSONObject(i).getString("status").equals("active"))
                        {
                            listA.add(agendas.getJSONObject(i));
                        }
                        else if(agendas.getJSONObject(i).getString("status").equals("started"))
                        {
                            listA.add(agendas.getJSONObject(i));
                            listB.add(agendas.getJSONObject(i));
                            try{
                                mainActivity.currentAgendaId = agendas.getJSONObject(i).getString("id"); // A agenda is active
                                mainActivity.findViewById(R.id.OngoingAgendaContainer).setVisibility(View.VISIBLE);
                            }catch (Exception e){e.printStackTrace();}
                        }
                        else if(agendas.getJSONObject(i).getString("status").equals("completed"))
                        {
                            listB.add(agendas.getJSONObject(i));
                            mainActivity.findViewById(R.id.OngoingAgendaContainer).setVisibility(View.VISIBLE);
                        }
                    }
                }
            } catch (Exception e){e.printStackTrace();}
            try{
                AgendaAdapter adapterA = new AgendaAdapter(listA, mainActivity.getApplicationContext(),mainActivity, listA.get(0).getInt("id"));
                listAgendaA.setAdapter(adapterA);
            } catch (Exception e){e.printStackTrace();}
            try{
                OngoingAgendaAdapter adapterB = new OngoingAgendaAdapter(listB, mainActivity.getApplicationContext(),mainActivity, 0);
                listAgendaB.setAdapter(adapterB);
            }catch(Exception e){e.printStackTrace();}

        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(mainActivity);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

    }

    class filepackListThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        String filepackId;

        filepackListThread(String s)
        {
            filepackId = s;
        }
        @Override
        protected String doInBackground(String ...params)
        {
            if(mainActivity.checkConnection())
            {
                try{  // /api/meeting/id/
                    JSONObject filePack = mainActivity.getData(mainActivity.currentCompany.getString("protocol") + mainActivity.currentCompany.getString("domain") + mainActivity.filePackAPI+filepackId);
                    mainActivity.dbHelper.insertData(mainActivity.filePackAPI+filepackId, filePack.toString());
                }catch (Exception e){e.printStackTrace();}
            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();

            ListView listV = mainActivity.findViewById(R.id.listFile);
            ArrayList<JSONObject> list = new ArrayList<>();
            try {
                JSONArray files = (new JSONObject(mainActivity.dbHelper.getData(mainActivity.filePackAPI + filepackId))).getJSONArray("data");
                for (int i = 0; i < files.length(); i++) list.add(files.getJSONObject(i));
            } catch (Exception e){e.printStackTrace();}
            try
            {
                LibraryFileAdapter adapter = new LibraryFileAdapter(list, mainActivity.getApplicationContext(),mainActivity,null);
                listV.setAdapter(adapter);
            }catch (Exception e){e.printStackTrace();}

        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(mainActivity);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

    }
}
