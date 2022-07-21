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

import java.util.ArrayList;


public class CommitteeListMeetingPageAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;
    int status;
    public boolean isSelectableCommitteePresent = false;

    ArrayList<ImageView> imageViews = new ArrayList<>();

    // View lookup cache
    private static class ViewHolder
    {
        TextView name;
        ImageView imageView;
    }

    public CommitteeListMeetingPageAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma)
    {
        super(context, R.layout.committee_cell_meetingpage, data);
        this.dataSet = data;
        this.mContext=context;
        mainActivity = ma;

        for (JSONObject object: this.dataSet)
        {
            try{
                if(object.getString("id").equals(mainActivity.currentCommitteeId))
                {
                    isSelectableCommitteePresent = true;
                    break;
                }
            }catch (Exception e){e.printStackTrace();}

        }
    }


    @Override
    public void onClick(View view) {
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final JSONObject committee = getItem(position);
        final CommitteeListMeetingPageAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new CommitteeListMeetingPageAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.committee_cell_meetingpage, parent, false);
            viewHolder.name =  convertView.findViewById(R.id.name);
            viewHolder.imageView =  convertView.findViewById(R.id.tick);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CommitteeListMeetingPageAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        try
        {
            viewHolder.name.setText(committee.getString("title"));



            if(mainActivity.currentCommitteeId.equals(committee.getString("id")))
            {
                clickListenerFunction(committee,viewHolder);
            }
            else if(!isSelectableCommitteePresent)
            {
                clickListenerFunction(committee,viewHolder);
                isSelectableCommitteePresent = true;
            }

            imageViews.add(viewHolder.imageView);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        result.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickListenerFunction(committee,viewHolder);
            }
        });

        return convertView;
    }

    public void clickListenerFunction(JSONObject committee, ViewHolder viewHolder)
    {
        try
        {
            for(ImageView i:imageViews) i.setVisibility(View.INVISIBLE);
            viewHolder.imageView.setVisibility(View.VISIBLE);
        }catch (Exception e){e.printStackTrace();}


        try{
            mainActivity.currentCommitteeId = committee.getString("id");
            new meetingListThread(committee.getString("id"),committee.getString("title")).execute("");
        }catch (Exception e){e.printStackTrace();}




    }


    class meetingListThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        String committeeId;
        String committeeName;

        meetingListThread(String s, String n)
        {
            committeeName = n;
            committeeId = s;
        }
        @Override
        protected String doInBackground(String ...params)
        {
            if(mainActivity.checkConnection())
            {
                try{
                    JSONObject meeting = mainActivity.getData(mainActivity.currentCompany.getString("protocol")+
                            mainActivity.currentCompany.getString("domain")+mainActivity.committeeMeetingAPI+committeeId);
                    mainActivity.dbHelper.insertData(mainActivity.committeeMeetingAPI+committeeId, meeting.toString());
                }catch (Exception e){e.printStackTrace();}
            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();

            ListView listView = mainActivity.findViewById(R.id.list);
            ArrayList<JSONObject> list = new ArrayList<>();
            try{
                JSONArray Meetings = (new JSONObject(mainActivity.dbHelper.getData(mainActivity.committeeMeetingAPI + committeeId)) ).getJSONArray("data");
                for(int i=0;i<Meetings.length();i++) list.add(Meetings.getJSONObject(i));
            } catch (Exception e){ e.printStackTrace();}
            try {
                MeetingAdapter adapter = new MeetingAdapter(list, mainActivity.getApplicationContext(),mainActivity,committeeName , Integer.parseInt(committeeId));
                listView.setAdapter(adapter);
            } catch (Exception e){e.printStackTrace();}

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
