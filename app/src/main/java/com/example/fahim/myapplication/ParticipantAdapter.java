package com.example.fahim.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONObject;
import java.util.ArrayList;


public class ParticipantAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;

    private static class ViewHolder
    {
        TextView memberName, designation, status;
        ImageView proPic;
    }

    public ParticipantAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma)
    {
        super(context, R.layout.participants_notice_cell, data);
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

        final JSONObject participant = getItem(position);
        final ParticipantAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ParticipantAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.participants_notice_cell, parent, false);
            viewHolder.memberName =  convertView.findViewById(R.id.participantName);
            viewHolder.designation =  convertView.findViewById(R.id.designation);
            viewHolder.status =  convertView.findViewById(R.id.status);
            viewHolder.proPic =  convertView.findViewById(R.id.propic);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ParticipantAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        try
        {
            viewHolder.memberName.setText(participant.getString("name"));
            viewHolder.designation.setText(participant.getString("designation"));
            //viewHolder.status.setText("Approved");

            try{
                String url = mainActivity.currentCompany.getString("protocol")+
                        mainActivity.currentCompany.getString("domain")+
                        mainActivity.avatarAPI+participant.getString("id")+"?token="+
                        mainActivity.currentUser.getString("token");
                Picasso.get().load(url).transform(new RoundedTransformation(100, 0)).into(viewHolder.proPic);
            } catch (Exception e){e.printStackTrace();}

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

class RoundedTransformation implements com.squareup.picasso.Transformation {
    private final int radius;
    private final int margin;

    public RoundedTransformation(final int radius, final int margin) {
        this.radius = radius;
        this.margin = margin;
    }

    @Override
    public Bitmap transform(final Bitmap source) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP));

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawRoundRect(new RectF(margin, margin, source.getWidth() - margin,
                source.getHeight() - margin), radius, radius, paint);

        if (source != output) {
            source.recycle();
        }
        return output;
    }

    @Override
    public String key() {
        return "rounded(r=" + radius + ", m=" + margin + ")";
    }
}
