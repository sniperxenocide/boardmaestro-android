package com.example.fahim.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import java.util.ArrayList;


public class NoticeAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;
    public ArrayList<ImageView> imageViews = new ArrayList<>();
    int notice_id = -1;
    boolean[] checked ;
    ImageView[] views;

    private static class ViewHolder
    {
        TextView noticeName, referenceNo;
        ImageView tick;
    }

    public NoticeAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma,int id)
    {
        super(context, R.layout.notice_cell, data);
        this.dataSet = data;
        this.mContext=context;
        mainActivity = ma;
        this.notice_id = id;

        checked = new boolean[dataSet.size()];
        for(boolean b:checked) b = false;
        views = new ImageView[dataSet.size()];

    }



    @Override
    public void onClick(View view) {
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final JSONObject notice = getItem(position);
        final NoticeAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new NoticeAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.notice_cell, parent, false);
            viewHolder.noticeName =  convertView.findViewById(R.id.noticeName);
            viewHolder.referenceNo =  convertView.findViewById(R.id.reference);
            viewHolder.tick =  convertView.findViewById(R.id.tick);
            views[position] = viewHolder.tick;

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NoticeAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;
        if(checked[position] ) viewHolder.tick.setVisibility(View.VISIBLE);
        else viewHolder.tick.setVisibility(View.INVISIBLE);

        try
        {
            viewHolder.noticeName.setText(notice.getString("title"));
            viewHolder.referenceNo.setText(notice.getString("reference_no"));
            try {
                if (notice.getInt("id") == notice_id) {
                    clickListenerFunction(notice,viewHolder,position);
                    notice_id = -1;
                }
            }catch (Exception e){e.printStackTrace();}

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        System.out.println(position+" "+viewHolder+"  **********************");


        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerFunction(notice,viewHolder,position);
            }
        });


        return convertView;
    }

    public void clickListenerFunction(JSONObject n,ViewHolder viewHolder, int position)
    {
        for (int i=0;i<checked.length;i++) {
            checked[i] = false;
            if(views[i]!= null) views[i].setVisibility(View.INVISIBLE);
        }
        viewHolder.tick.setVisibility(View.VISIBLE);
        checked[position] = true;

        WebView webView = mainActivity.findViewById(R.id.description);
        webView.getSettings().setJavaScriptEnabled(true);
        try
        {
           webView.loadDataWithBaseURL(null, n.getString("description"), "text/html", "utf-8", null);
        }catch (Exception e){e.printStackTrace();}


        try{
            new participant_fileListThread(n.getString("id")).execute("");
        }catch (Exception e){e.printStackTrace();}

    }

    class participant_fileListThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        String noticeId;

        participant_fileListThread(String s)
        {
            noticeId = s;
        }
        @Override
        protected String doInBackground(String ...params)
        {
            if(mainActivity.checkConnection())
            {
                try{
                    JSONObject participants = mainActivity.getData( mainActivity.currentCompany.getString("protocol")+ mainActivity.currentCompany.getString("domain")+mainActivity.noticeParticipantsAPI+noticeId);
                    mainActivity.dbHelper.insertData(mainActivity.noticeParticipantsAPI+noticeId, participants.toString());
                }catch (Exception e){e.printStackTrace();}

                try{
                    JSONObject files = mainActivity.getData( mainActivity.currentCompany.getString("protocol") + mainActivity.currentCompany.getString("domain") + mainActivity.noticeFilesAPI+noticeId);
                    mainActivity.dbHelper.insertData( mainActivity.noticeFilesAPI+noticeId, files.toString());
                }catch (Exception e){e.printStackTrace();}
            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();


            try
            {
                ListView listParticipants = mainActivity.findViewById(R.id.listParticipant);
                ArrayList<JSONObject> list = new ArrayList<>();
                try{
                    JSONArray participants = (  new JSONObject(mainActivity.dbHelper.getData(mainActivity.noticeParticipantsAPI +noticeId))  ).getJSONArray("data");
                    for(int i=0;i<participants.length();i++) list.add(participants.getJSONObject(i));
                }catch (Exception e){e.printStackTrace();}
                try{
                    ParticipantAdapter adapter = new ParticipantAdapter(list, mainActivity.getApplicationContext(),mainActivity);
                    listParticipants.setAdapter(adapter);
                }catch (Exception e){e.printStackTrace();}
            } catch (Exception e){e.printStackTrace();}


            try
            {
                ListView listFiles = mainActivity.findViewById(R.id.listFile);
                ArrayList<JSONObject> list = new ArrayList<>();
                try{
                    JSONArray files = (  new JSONObject(mainActivity.dbHelper.getData(mainActivity.noticeFilesAPI + noticeId))  ).getJSONArray("data");
                    for(int i=0;i<files.length();i++) list.add(files.getJSONObject(i));
                }catch (Exception e){e.printStackTrace();}
                try{
                    NoticeFileAdapter adapter = new NoticeFileAdapter(list, mainActivity.getApplicationContext(),mainActivity);
                    listFiles.setAdapter(adapter);
                }catch (Exception e){e.printStackTrace();}
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
