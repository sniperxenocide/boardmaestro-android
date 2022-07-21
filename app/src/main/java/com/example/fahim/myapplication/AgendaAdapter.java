package com.example.fahim.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
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


public class AgendaAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;
    public int agenda_id ;
    boolean[] checked;
    ConstraintLayout[] layouts;
    ImageView backArrow;

    private static class ViewHolder
    {
        TextView memo_no, title;
        ImageView update;
        ConstraintLayout container;
        ConstraintLayout agendaCell;
        ConstraintLayout agendaStarEnd;
    }

    public AgendaAdapter(ArrayList<JSONObject> data, Context context, final MainActivity ma , int id)
    {
        super(context, R.layout.agenda_cell, data);
        this.dataSet = data;
        this.mContext=context;
        mainActivity = ma;
        agenda_id = id;

        checked = new boolean[dataSet.size()];
        layouts = new ConstraintLayout[dataSet.size()];


        try{        // download all files of the meeting
            mainActivity.findViewById(R.id.downloadAll).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        String filepackId = dataSet.get(0).getJSONArray("files").getJSONObject(0).getString("file_pack_id");
                        new meetingFileDownloadThread(filepackId,mainActivity).execute("");
                    }catch (Exception e){
                        mainActivity.showToast("No file Exist");
                        e.printStackTrace();}
                }
            });
        }catch (Exception e){e.printStackTrace();}

    }


    @Override
    public void onClick(View view) {
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final JSONObject agenda = getItem(position);
        final AgendaAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new AgendaAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.agenda_cell, parent, false);
            viewHolder.memo_no =  convertView.findViewById(R.id.memo_no);
            viewHolder.title =  convertView.findViewById(R.id.title);
            viewHolder.update =  convertView.findViewById(R.id.update);
            viewHolder.container =  convertView.findViewById(R.id.container);
            viewHolder.agendaCell =  convertView.findViewById(R.id.agendaCellCatcher);
            viewHolder.agendaStarEnd =  convertView.findViewById(R.id.startMeeting);


            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (AgendaAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        if(checked[position]) viewHolder.container.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightBlue));
        else {
            viewHolder.container.setBackgroundColor(mainActivity.getResources().getColor(R.color.white));
            viewHolder.container.setBackgroundResource(R.drawable.cell_background_white);
        }

        try
        {
            viewHolder.memo_no.setText(agenda.getString("serial_no"));
            viewHolder.title.setText(agenda.getString("title"));
            try{
                if (agenda_id == agenda.getInt("id")) {
                    clickListenerFunction(agenda,viewHolder,position);
                    agenda_id  = -1 ;
                }

            }catch (Exception e){e.printStackTrace();}

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        layouts[position] = viewHolder.container;
        viewHolder.agendaCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerFunction(agenda,viewHolder,position);
            }
        });

        try{
            TextView meetingStatus = mainActivity.findViewById(R.id.status);
            if(mainActivity.currentUser.getJSONObject("user").getString("designation").equals("Secretary") && !meetingStatus.getText().toString().equals("completed"))
            {
                viewHolder.agendaStarEnd.setVisibility(View.VISIBLE);
                final ImageView icon = convertView.findViewById(R.id.startIcon);
                final TextView text = convertView.findViewById(R.id.startText);

                try {
                    if(mainActivity.currentAgendaId.equals(""))
                    {
                        if(agenda.getString("status").equals("active") ){
                            icon.setImageResource(R.drawable.power_button);
                            text.setText("Start");
                        }
                        else if(agenda.getString("status").equals("started")){
                            icon.setImageResource(R.drawable.power_off);
                            text.setText("Finish");
                        }
                        else if(agenda.getString("status").equals("completed")){
                            icon.setImageResource(R.drawable.power_grey);
                            text.setText("Finished");
                        }
                    }
                    else {
                        if(mainActivity.currentAgendaId.equals(agenda.getString("id")) && agenda.getString("status").equals("started"))
                        {
                            icon.setImageResource(R.drawable.power_off);
                            text.setText("Finish");
                        }
                        else if(agenda.getString("status").equals("active") ){
                            icon.setImageResource(R.drawable.power_grey);
                            text.setText("Start");
                        }
                        else if(agenda.getString("status").equals("completed")){
                            icon.setImageResource(R.drawable.power_grey);
                            text.setText("Finished");
                        }
                    }

                } catch (Exception e) { e.printStackTrace(); }

                viewHolder.agendaStarEnd.setOnClickListener(new View.OnClickListener() {       // agenda start/ end button
                    @Override
                    public void onClick(View view) {

                        AlertDialog dialog = new AlertDialog.Builder(mainActivity).setTitle("Confirm Your Action")
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Date date = new Date();
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                        String datetime= dateFormat.format(date);
                                        final String url;
                                        final JSONObject param = new JSONObject();
                                        try {
                                            url = mainActivity.currentCompany.getString("protocol") +mainActivity.currentCompany.getString("domain") + mainActivity.agendaStartEndAPI + agenda.getString("id");

                                            if(agenda.getString("status").equals("active") && mainActivity.currentAgendaId.equals("")){
                                                param.put("status","started");
                                                param.put("datetime",datetime);
                                                new agendaStartFinishThread(url,param).execute();
                                            }
                                            else if(agenda.getString("status").equals("started") && mainActivity.currentAgendaId.equals(agenda.getString("id"))){
                                                param.put("status","completed");
                                                param.put("datetime",datetime);
                                                new agendaStartFinishThread(url,param).execute();
                                            }

                                        } catch (Exception e) { e.printStackTrace(); }

                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).create();dialog.setCanceledOnTouchOutside(false);dialog.show();

                    }
                });
            }
            else {
                viewHolder.agendaStarEnd.setVisibility(View.GONE);
            }
        }catch (Exception e){e.printStackTrace();}

        return convertView;
    }

    public void clickListenerFunction(JSONObject agenda,ViewHolder viewHolder, int position)
    {
        for(int i=0;i<checked.length;i++)
        {
            checked[i]=false;
            if(layouts[i]!=null){
                layouts[i].setBackgroundColor(mainActivity.getResources().getColor(R.color.white));
                layouts[i].setBackgroundResource(R.drawable.cell_background_white);
            }
        }
        viewHolder.container.setBackgroundColor(mainActivity.getResources().getColor(R.color.lightBlue));
        checked[position] = true;

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



    static class meetingFileDownloadThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        MainActivity activity;
        String filepackId;
        ArrayList<JSONObject> fileList;
        boolean downloaded = false;

        meetingFileDownloadThread(String FilepackId , MainActivity ma)
        {
            activity = ma;
            filepackId = FilepackId;
            fileList = new ArrayList<>();
        }
        @Override
        protected String doInBackground(String ...params)
        {
            if(!activity.checkConnection()) return "";

            try{
                JSONArray files = activity.getData(activity.currentCompany.getString("protocol")+activity.currentCompany.getString("domain")+activity.filePackAPI+filepackId).getJSONArray("data");
                for(int i=0;i<files.length();i++) fileList.add(files.getJSONObject(i));
            }catch ( Exception e){e.printStackTrace();}

            try{
                for(JSONObject object:fileList)
                {
                    try{
                        String filename = object.getString("location").replace("/","_");
                        File file = new File(activity.getFilesDir(),filename);
                        String url = activity.currentCompany.getString("protocol")+activity.currentCompany.getString("domain")+
                                activity.filepackFileDownloadAPI+object.getString("id") +"?token="+activity.currentUser.getString("token");

                        if(!file.exists())
                        {
                            file.createNewFile();
                            activity.downloadFile(url,file);
                        }

                    }catch (Exception e){e.printStackTrace();}
                }
            }catch (Exception e){e.printStackTrace();}

            downloaded = true;
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();
            if(downloaded) activity.showToast("Download Complete");
            else activity.showToast("Network Error");
        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(activity);
            progress.setTitle("Loading");
            progress.setMessage("Wait while Downloading...");
            progress.setCancelable(false);
            progress.show();
        }

    }

    class agendaStartFinishThread extends  AsyncTask<Void, Void, Void>{
        Boolean success = false;
        ProgressDialog progressDialog;
        final String url;
        final JSONObject param;

        agendaStartFinishThread(String url,JSONObject param){
            this.url = url;
            this.param = param;
        }
        protected void onPreExecute() {
            // Pre Code
            progressDialog = new ProgressDialog(mainActivity);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Wait while loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
        protected Void doInBackground(Void... unused) {
            try{
                if(mainActivity.checkConnection()) {
                    JSONObject object = mainActivity.PostPutMethod(url,param,mainActivity.PUT_METHOD);
                    success = object.getBoolean("status");
                }
            }catch (Exception e){e.printStackTrace();}
            return null;
        }
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            progressDialog.dismiss();
            if(success) {
                mainActivity.showMeetingDetailPage(mainActivity.currentMeetingId, mainActivity.currentCommitteeTitle);
                mainActivity.showToast("Agenda status Updated Successfully");
            }
            else mainActivity.showToast("Failed to start agenda");
        }
    }

}
