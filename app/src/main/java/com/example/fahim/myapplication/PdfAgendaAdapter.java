package com.example.fahim.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.text.method.ScrollingMovementMethod;
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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class PdfAgendaAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    PdfReaderActivity activity;
    boolean[] checked;
    ConstraintLayout[] layouts;
    String initName;


    private static class ViewHolder
    {
        TextView serial, title , filename;
        ConstraintLayout container;
    }

    public PdfAgendaAdapter(ArrayList<JSONObject> data, Context context, final PdfReaderActivity pdfReaderActivity , String name)
    {
        super(context, R.layout.pdf_agenda_cell, data);
        this.dataSet = data;
        this.mContext=context;
        activity = pdfReaderActivity;
        initName = name;

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
        final PdfAgendaAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new PdfAgendaAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.pdf_agenda_cell, parent, false);
            viewHolder.serial =  convertView.findViewById(R.id.serial);
            viewHolder.title =  convertView.findViewById(R.id.title);
            viewHolder.filename =  convertView.findViewById(R.id.filename);
            viewHolder.container =  convertView.findViewById(R.id.container);
            layouts[position] = viewHolder.container;

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PdfAgendaAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        if(checked[position]) viewHolder.container.setBackgroundColor(activity.getResources().getColor(R.color.lightBlue));
        else {
            viewHolder.container.setBackgroundColor(activity.getResources().getColor(R.color.white));
            viewHolder.container.setBackgroundResource(R.drawable.cell_background_white);
        }

        try
        {
            viewHolder.serial.setText(agenda.getString("serial_no"));
            viewHolder.title.setText(agenda.getString("title"));
            viewHolder.filename.setText(agenda.getString("file_name_actual"));
            try{
                if (initName.equals(agenda.getString("filename")) )
                {
                    clickListenerFunction(agenda,viewHolder,position);
                    initName = "";
                }

            }catch (Exception e){e.printStackTrace();}

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListenerFunction(agenda,viewHolder,position);
            }
        });

        return convertView;
    }

    public void clickListenerFunction(JSONObject agenda,ViewHolder viewHolder, int position)
    {
        for(int i=0;i<checked.length;i++)
        {
            checked[i]=false;
            if(layouts[i]!=null){
                layouts[i].setBackgroundColor(activity.getResources().getColor(R.color.white));
                layouts[i].setBackgroundResource(R.drawable.cell_background_white);
            }
        }
        viewHolder.container.setBackgroundColor(activity.getResources().getColor(R.color.lightBlue));
        checked[position] = true;

        try{
            File f = new File(activity.getFilesDir(),agenda.getString("filename"));
            String url = MainActivity.currentCompany.getString("protocol")+MainActivity.currentCompany.getString("domain")+
                    MainActivity.filepackFileDownloadAPI + agenda.getString("file_id")
                    +"?token=" + MainActivity.currentUser.getString("token");

            new fileDownloadThread(url,f,agenda.getString("filename")).execute("");
        }catch (Exception e){e.printStackTrace();}


    }


    public void downloadFile(String downloadUrl, File outputFile) {
        try
        {
            URL url = new URL(downloadUrl);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("GET");
            c.connect();
            if (!outputFile.exists())  outputFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream();

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1)  fos.write(buffer, 0, len1);

            fos.close();
            is.close();
        } catch (Exception e){e.printStackTrace();}
    }

    class fileDownloadThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        String url;
        File file;
        String filename;
        fileDownloadThread(String url,File file,String filename)
        {
            this.url=url;
            this.file=file;
            this.filename=filename;
        }
        @Override
        protected String doInBackground(String ...params)
        {

            try{
                if(!file.exists())
                {
                    file.createNewFile();
                    downloadFile(url,file);
                }

            }catch (Exception e){e.printStackTrace();}
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();

            if(file.exists()){
                try
                {
                    activity.loadNewFile(filename);
                }catch (Exception e){e.printStackTrace();}

            }
            else activity.showToast("File Doesn't Exist");

        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(activity);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

    }



}
