package com.example.fahim.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.support.constraint.ConstraintLayout;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class PdfPageAdapter extends ArrayAdapter<Integer> implements View.OnClickListener
{

    private ArrayList<Integer> dataSet;
    public ArrayList<ImageView> pageList = new ArrayList<>();
    Context mContext;
    PdfReaderActivity activity;
    File file;
    public int Width;
    public int Height;

    private static class ViewHolder
    {
        ImageView pageImage;
        ConstraintLayout container;
    }

    public PdfPageAdapter(ArrayList<Integer> data, Context context, PdfReaderActivity pdfReaderActivity , File file)
    {
        super(context, R.layout.pdf_page_cell, data);
        this.dataSet = data;
        this.mContext=context;
        activity = pdfReaderActivity;
        this.file = file;

        Display currentDisplay = activity.getWindowManager().getDefaultDisplay();
        float dw = currentDisplay.getWidth();
        Width = (int)(dw*0.6);
        Height = (int)(dw*0.9);

    }



    @Override
    public void onClick(View view) {
    }


    private int lastPosition = -1;

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final Integer integer = getItem(position);
        final PdfPageAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new PdfPageAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.pdf_page_cell, parent, false);
            viewHolder.pageImage =  convertView.findViewById(R.id.pageImage);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PdfPageAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }

        if(!pageList.contains(viewHolder.pageImage)) pageList.add(viewHolder.pageImage);

        lastPosition = position;

        try
        {
            ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE);
            PdfRenderer renderer = new PdfRenderer(fileDescriptor);
            PdfRenderer.Page page = renderer.openPage(integer);

            Bitmap bitmap = Bitmap.createBitmap(Width, Height, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            viewHolder.pageImage.setImageBitmap(bitmap);
            viewHolder.pageImage.setOnTouchListener(activity);

            activity.canvas = new Canvas(bitmap);
            activity.LoadAnnotation(position , Width, Height);

            page.close();
            renderer.close();
            viewHolder.pageImage.invalidate();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return convertView;
    }


}
