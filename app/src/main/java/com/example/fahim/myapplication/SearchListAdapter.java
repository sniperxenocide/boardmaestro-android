
package com.example.fahim.myapplication;

import android.content.Context;
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
import java.util.ArrayList;


public class SearchListAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;
    int status;

    // View lookup cache
    private static class ViewHolder
    {
        TextView category, name, serial ;
        ImageView icon;
    }

    public SearchListAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma)
    {
        super(context, R.layout.search_list_cell, data);
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

        final JSONObject data = getItem(position);
        final SearchListAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new SearchListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.search_list_cell, parent, false);

            viewHolder.category =  convertView.findViewById(R.id.category);
            viewHolder.name =  convertView.findViewById(R.id.name);
            viewHolder.serial =  convertView.findViewById(R.id.serial);
            viewHolder.icon =  convertView.findViewById(R.id.icon);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SearchListAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        try
        {
            if(data.getString("category").equals("Meeting"))
            {
                try{
                    viewHolder.category.setText(data.getString("category"));
                    viewHolder.name.setText(data.getString("title"));
                    viewHolder.serial.setText(data.getString("serial_no"));
                    viewHolder.icon.setBackgroundResource(R.drawable.meetings_selected);
                } catch (Exception e){e.printStackTrace();}

            }
            else if(data.getString("category").equals("Agenda"))
            {
                try{
                    viewHolder.category.setText(data.getString("category"));
                    viewHolder.name.setText(data.getString("title"));
                    viewHolder.serial.setText(data.getString("serial_no"));
                    viewHolder.icon.setBackgroundResource(R.drawable.meetings_selected);
                } catch (Exception e){e.printStackTrace();}
            }
            else if(data.getString("category").equals("Poll"))
            {
                try{
                    viewHolder.category.setText(data.getString("category"));
                    viewHolder.name.setText(data.getString("title"));
                    viewHolder.serial.setText(data.getString("serial_no"));
                    viewHolder.icon.setBackgroundResource(R.drawable.meetings_selected);
                } catch (Exception e){e.printStackTrace();}
            }
            else if(data.getString("category").equals("Committee"))
            {
                try{
                    viewHolder.category.setText(data.getString("category"));
                    viewHolder.name.setText(data.getString("title"));
                    viewHolder.serial.setText("");
                    viewHolder.icon.setBackgroundResource(R.drawable.group_meeting);
                } catch (Exception e){e.printStackTrace();}
            }
            else if(data.getString("category").equals("Correspondence"))
            {
                try{
                    viewHolder.category.setText("Notice");
                    viewHolder.name.setText(data.getString("title"));
                    viewHolder.serial.setText(data.getString("reference_no"));
                    viewHolder.icon.setBackgroundResource(R.drawable.bell_on);
                } catch (Exception e){e.printStackTrace();}
            }
            else if(data.getString("category").equals("Correspondence File"))
            {
                try{
                    viewHolder.category.setText(data.getString("category"));
                    viewHolder.name.setText(data.getString("original_name"));
                    float f = (float)((data.getInt("size"))/(1024.0*1024)) ;
                    viewHolder.serial.setText( String.format("%.02f", f) +" MB" );
                    viewHolder.icon.setBackgroundResource(R.drawable.clip_blue);
                } catch (Exception e){e.printStackTrace();}
            }
            else if(data.getString("category").equals("File"))
            {
                try{
                    viewHolder.category.setText(data.getString("category"));
                    viewHolder.name.setText(data.getString("title"));
                    float f = (float)((data.getInt("size"))/(1024.0*1024)) ;
                    viewHolder.serial.setText( String.format("%.02f", f) +" MB" );
                    viewHolder.icon.setBackgroundResource(R.drawable.clip_blue);
                } catch (Exception e){e.printStackTrace();}
            }
            else if(data.getString("category").equals("Library"))
            {
                try{
                    viewHolder.category.setText(data.getString("category"));
                    viewHolder.name.setText(data.getString("title"));
                    viewHolder.serial.setText(data.getString("serial_no"));
                    viewHolder.icon.setBackgroundResource(R.drawable.meetings_selected);
                } catch (Exception e){e.printStackTrace();}
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        result.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try
                {
                    if(data.getString("category").equals("Meeting"))
                    {
                        try{
                            mainActivity.showMeetingDetailPage(data.getString("id"),"");

                        } catch (Exception e){e.printStackTrace();}

                    }
                    else if(data.getString("category").equals("Agenda"))
                    {
                        try{
                            mainActivity.showMeetingDetailPage(data.getString("id"),"");
                        } catch (Exception e){e.printStackTrace();}
                    }
                    else if(data.getString("category").equals("Poll"))
                    {
                        try{

                        } catch (Exception e){e.printStackTrace();}
                    }
                    else if(data.getString("category").equals("Committee"))
                    {
                        try{

                        } catch (Exception e){e.printStackTrace();}
                    }
                    else if(data.getString("category").equals("Correspondence"))
                    {
                        try{
                            mainActivity.setContentView(R.layout.notice_page);
                            ListView listView = mainActivity.findViewById(R.id.listNotice);
                            ArrayList<JSONObject> list = new ArrayList<>();
                            list.add(data);
                            try{
                                NoticeAdapter adapter = new NoticeAdapter(list, mainActivity.getApplicationContext(), mainActivity,  data.getInt("id"));
                                listView.setAdapter(adapter);
                            }catch (Exception e){e.printStackTrace();}

                        } catch (Exception e){e.printStackTrace();}
                    }
                    else if(data.getString("category").equals("Correspondence File"))
                    {
                        try{
                            String filename = data.getString("location").replace("/","_");
                            File file = new File(mainActivity.getFilesDir(),filename);
                            String url = mainActivity.currentCompany.getString("protocol")+mainActivity.currentCompany.getString("domain")+
                                    mainActivity.noticeFileDownloadAPI+data.getString("id")
                                    +"?token="+mainActivity.currentUser.getString("token");
                            mainActivity.new fileDownloadThread(url,file,filename , null).execute("");

                        } catch (Exception e){e.printStackTrace();}
                    }
                    else if(data.getString("category").equals("File"))
                    {
                        try{
                            String filename = data.getString("location").replace("/","_");
                            File file = new File(mainActivity.getFilesDir(),filename);
                            String url = mainActivity.currentCompany.getString("protocol")+mainActivity.currentCompany.getString("domain")+
                                    mainActivity.filepackFileDownloadAPI+data.getString("id")
                                    +"?token="+mainActivity.currentUser.getString("token");
                            mainActivity.new fileDownloadThread(url,file,filename , null).execute("");
                        } catch (Exception e){e.printStackTrace();}
                    }
                    else if(data.getString("category").equals("Library"))
                    {
                        try{

                        } catch (Exception e){e.printStackTrace();}
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }



            }
        });


        return convertView;
    }
}
