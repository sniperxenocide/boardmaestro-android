package com.example.fahim.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class NoticeFileAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;
    int status;

    // View lookup cache
    private static class ViewHolder
    {
        TextView fileName;
    }

    public NoticeFileAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma)
    {
        super(context, R.layout.notice_file_cell, data);
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
        final NoticeFileAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new NoticeFileAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.notice_file_cell, parent, false);
            viewHolder.fileName =  convertView.findViewById(R.id.fileName);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NoticeFileAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        try
        {
            viewHolder.fileName.setText(file.getString("original_name"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        result.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mainActivity.findViewById(R.id.listNotice)!=null)   // checking if i am in notice page
                {
                    try
                    {
                        mainActivity.showToast("Loading New File...");
                        String filename = file.getString("location").replace("/","_");
                        File f = new File(mainActivity.getFilesDir(),filename);
                        String url = mainActivity.currentCompany.getString("protocol")+mainActivity.currentCompany.getString("domain")+
                                mainActivity.noticeFileDownloadAPI+file.getString("id")
                                +"?token="+mainActivity.currentUser.getString("token");

                        mainActivity.new fileDownloadThread(url,f,filename , null).execute("");

                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });

        return convertView;
    }

}
