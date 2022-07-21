package com.example.fahim.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class LibraryFileAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    public ArrayList<JSONObject> agendaDataset;
    Context mContext;
    MainActivity mainActivity;
    int status;

    // View lookup cache
    private static class ViewHolder
    {
        TextView fileName,fileSize;
    }

    public LibraryFileAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma , ArrayList<JSONObject> agendaData)
    {
        super(context, R.layout.library_file_cell, data);
        this.dataSet = data;
        this.mContext=context;
        mainActivity = ma;
        agendaDataset = agendaData;
    }


    @Override
    public void onClick(View view) {
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final JSONObject file = getItem(position);
        final LibraryFileAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new LibraryFileAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.library_file_cell, parent, false);
            viewHolder.fileName =  convertView.findViewById(R.id.fileName);
            viewHolder.fileSize =  convertView.findViewById(R.id.size);


            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LibraryFileAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        try
        {
            viewHolder.fileName.setText(file.getString("title"));
            float f = (float)(Integer.parseInt(file.getString("size"))/(1024.0*1024)) ;
            viewHolder.fileSize.setText( String.format("%.02f", f) +" MB" );

            try{
                ListView l = mainActivity.findViewById(R.id.listAgenda);
                if(l != null) {
                    viewHolder.fileSize.setVisibility(View.GONE);
                    viewHolder.fileName.setTypeface(null, Typeface.NORMAL);
                }
            } catch (Exception e){e.printStackTrace();}

            try{
                ListView l = mainActivity.findViewById(R.id.list);
                if(l != null) {
                    viewHolder.fileName.setTypeface(null, Typeface.NORMAL);
                    viewHolder.fileSize.setTypeface(null, Typeface.NORMAL);
                }
            } catch (Exception e){e.printStackTrace();}

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{

                    mainActivity.showToast("Loading New File...");

                    String filename = file.getString("location").replace("/","_");
                    File f = new File(mainActivity.getFilesDir(),filename);
                    String url="";
                    if(mainActivity.findViewById(R.id.listCategory)!=null)   // checking if i am in library page
                    {
                        url = mainActivity.currentCompany.getString("protocol")+mainActivity.currentCompany.getString("domain")+
                                mainActivity.libraryFileDownloadAPI+file.getString("id")
                                +"?token="+mainActivity.currentUser.getString("token");
                    }
                    else if(mainActivity.findViewById(R.id.list)!=null)   // checking if i am in filepack page
                    {
                        url = mainActivity.currentCompany.getString("protocol")+mainActivity.currentCompany.getString("domain")+
                                mainActivity.filepackFileDownloadAPI+file.getString("id")
                                +"?token="+mainActivity.currentUser.getString("token");
                    }
                    else if(mainActivity.findViewById(R.id.listAgenda)!=null)   // checking if i am in meetingDetail page
                    {
                        url = mainActivity.currentCompany.getString("protocol")+mainActivity.currentCompany.getString("domain")+
                                mainActivity.filepackFileDownloadAPI+file.getString("id")
                                +"?token="+mainActivity.currentUser.getString("token");
                    }
                    mainActivity.new fileDownloadThread(url,f,filename,agendaDataset).execute("");

                }catch (Exception e){e.printStackTrace();}

            }
        });


        return convertView;
    }

}
