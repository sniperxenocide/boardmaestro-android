package com.example.fahim.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONObject;
import java.util.ArrayList;


public class TopNoticeAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;
    public ArrayList<ImageView> imageViews = new ArrayList<>();

    private static class ViewHolder
    {
        TextView noticeName, referenceNo;
    }

    public TopNoticeAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma)
    {
        super(context, R.layout.top_notice_cell, data);
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

        final JSONObject notice = getItem(position);
        final TopNoticeAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new TopNoticeAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.top_notice_cell, parent, false);
            viewHolder.noticeName =  convertView.findViewById(R.id.noticeName);
            viewHolder.referenceNo =  convertView.findViewById(R.id.reference);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TopNoticeAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        try
        {
            viewHolder.noticeName.setText(notice.getString("title"));
            viewHolder.referenceNo.setText(notice.getString("reference_no"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        result.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainActivity.setContentView(R.layout.notice_page);
                mainActivity.showNoticeData(mainActivity.allNoticeAPI);
            }
        });


        return convertView;
    }
}
