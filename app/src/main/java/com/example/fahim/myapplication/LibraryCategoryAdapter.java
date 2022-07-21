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
import java.util.Iterator;


public class LibraryCategoryAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;
    int status;
    public ArrayList<ImageView> imageViews = new ArrayList<>();

    // View lookup cache
    private static class ViewHolder
    {
        TextView catName;
        ImageView tick;
    }

    public LibraryCategoryAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma)
    {
        super(context, R.layout.category_cell, data);
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

        final JSONObject category = getItem(position);
        final LibraryCategoryAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new LibraryCategoryAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.category_cell, parent, false);
            viewHolder.catName =  convertView.findViewById(R.id.catName);
            viewHolder.tick =  convertView.findViewById(R.id.tick);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LibraryCategoryAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        try
        {
            viewHolder.catName.setText(category.getString("name"));
            if(imageViews.size() == 0) { clickListenerFunction(category,viewHolder); }
            imageViews.add(viewHolder.tick);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        result.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickListenerFunction(category,viewHolder);
            }
        });


        return convertView;
    }


    public void clickListenerFunction(JSONObject category,ViewHolder viewHolder)
    {
        for(ImageView e:imageViews) e.setVisibility(View.INVISIBLE);
        viewHolder.tick.setVisibility(View.VISIBLE);

        try{
            new fileListThread(category.getString("id")).execute("");
        }catch (Exception e){e.printStackTrace();}
    }

    class fileListThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        String categoryId;

        fileListThread(String s)
        {
            categoryId = s;
        }
        @Override
        protected String doInBackground(String ...params)
        {
            if(mainActivity.checkConnection())
            {
                try {
                    JSONObject files = mainActivity.getData(mainActivity.currentCompany.getString("protocol")+ mainActivity.currentCompany.getString("domain") + mainActivity.libraryCategoryFilesAPI+ categoryId);
                    mainActivity.dbHelper.insertData( mainActivity.libraryCategoryFilesAPI + categoryId , files.toString());
                } catch (Exception e) { e.printStackTrace(); }
            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();

            ListView listFile = mainActivity.findViewById(R.id.listFile);
            ArrayList<JSONObject> list = new ArrayList<>();
            try {
                JSONArray files = (new JSONObject(mainActivity.dbHelper.getData(mainActivity.libraryCategoryFilesAPI + categoryId))).getJSONArray("data");
                for (int i = 0; i < files.length(); i++) list.add(files.getJSONObject(i));
            } catch (Exception e){e.printStackTrace();}
            try{
                LibraryFileAdapter adapter = new LibraryFileAdapter(list, mainActivity.getApplicationContext(),mainActivity,null);
                listFile.setAdapter(adapter);
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
