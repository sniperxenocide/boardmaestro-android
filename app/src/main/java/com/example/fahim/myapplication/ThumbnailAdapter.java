package com.example.fahim.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class ThumbnailAdapter extends ArrayAdapter<Bitmap> implements View.OnClickListener
{

    private ArrayList<Bitmap> dataSet;
    Context mContext;
    PdfReaderActivity activity;
    int page_no = -1;
    public boolean[] checked ;
    public ConstraintLayout[] layouts;

    private static class ViewHolder
    {
        ImageView pageImage;
        ConstraintLayout container;
    }

    public ThumbnailAdapter(ArrayList<Bitmap> data, Context context,PdfReaderActivity pdfReaderActivity,int pageNo)
    {
        super(context, R.layout.thumbnail_cell, data);
        this.dataSet = data;
        this.mContext=context;
        activity = pdfReaderActivity;
        this.page_no = pageNo;

        checked = new boolean[dataSet.size()];
        for(boolean b:checked) b = false;
        layouts = new ConstraintLayout[dataSet.size()];

    }



    @Override
    public void onClick(View view) {
    }

    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final Bitmap bitmap = getItem(position);
        final ThumbnailAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ThumbnailAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.thumbnail_cell, parent, false);
            viewHolder.pageImage =  convertView.findViewById(R.id.page);
            viewHolder.container =  convertView.findViewById(R.id.thumbnailCell);


            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ThumbnailAdapter.ViewHolder) convertView.getTag();
            result = convertView;

        }

        layouts[position] = viewHolder.container;
        lastPosition = position;
        if(checked[position] ) viewHolder.container.setBackgroundColor(activity.getResources().getColor(R.color.lightBlue));
        else viewHolder.container.setBackgroundColor(Color.TRANSPARENT);

        try
        {
            viewHolder.pageImage.setImageBitmap(bitmap);
            try {
                if (page_no==0) {
                    clickListenerFunction(viewHolder,position);
                    page_no = -1;
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
                clickListenerFunction(viewHolder,position);
            }
        });


        return convertView;
    }

    public void clickListenerFunction(ViewHolder viewHolder, int position)
    {
        for (int i=0;i<checked.length;i++) {
            checked[i] = false;
            if(layouts[i]!= null) layouts[i].setBackgroundColor(Color.TRANSPARENT);
        }
        viewHolder.container.setBackgroundColor(activity.getResources().getColor(R.color.lightBlue));
        checked[position] = true;

        activity.currentPage = position;
        EditText getPage = activity.findViewById(R.id.getPage);
        getPage.setText(Integer.toString(activity.currentPage+1));
        activity.pdfPageListView.setSelection(position);


    }

}
