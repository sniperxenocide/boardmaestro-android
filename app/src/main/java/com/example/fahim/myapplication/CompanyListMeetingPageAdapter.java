package com.example.fahim.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;


public class CompanyListMeetingPageAdapter extends ArrayAdapter<JSONObject> implements View.OnClickListener
{

    private ArrayList<JSONObject> dataSet;
    Context mContext;
    MainActivity mainActivity;
    int status;
    View lastClickedView ;

    ArrayList<ConstraintLayout> layouts = new ArrayList<>();
    ArrayList<ListView> listViews = new ArrayList<>();

    // View lookup cache
    private static class ViewHolder
    {
        TextView name,address;
        ListView listView;
        ConstraintLayout constraintLayout;
    }

    public CompanyListMeetingPageAdapter(ArrayList<JSONObject> data, Context context,MainActivity ma)
    {
        super(context, R.layout.company_list_cell_meetingpage, data);
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

        final JSONObject company = getItem(position);
        final CompanyListMeetingPageAdapter.ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new CompanyListMeetingPageAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.company_list_cell_meetingpage, parent, false);
            viewHolder.name =  convertView.findViewById(R.id.name);
            viewHolder.address =  convertView.findViewById(R.id.title);
            viewHolder.listView =  convertView.findViewById(R.id.listCommittee);
            viewHolder.constraintLayout =  convertView.findViewById(R.id.constraintLayout54);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CompanyListMeetingPageAdapter.ViewHolder) convertView.getTag();
            result = convertView;
        }


        lastPosition = position;

        try
        {
            viewHolder.name.setText(company.getString("name"));

            if(mainActivity.currentCompanyId.equals("")  || mainActivity.currentCompanyId.equals(company.getString("id")))
            {
                clickListenerFunction(company, viewHolder);
                lastClickedView = result;
            }
            layouts.add(viewHolder.constraintLayout);
            listViews.add(viewHolder.listView);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (lastClickedView != v)
                {
                    lastClickedView = v;
                    clickListenerFunction(company,viewHolder);
                }
            }
        });

        return convertView;
    }

    public void clickListenerFunction(JSONObject company, ViewHolder viewHolder)
    {
        try{
            mainActivity.currentCompanyId = company.getString("id");
            new committeeListThread(company.getString("id"),viewHolder.listView).execute("");
        } catch (Exception e){e.printStackTrace();}

    }


    class committeeListThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        String companyId;
        ListView listView;

        committeeListThread(String s,ListView l)
        {
            companyId = s;
            listView = l;
        }
        @Override
        protected String doInBackground(String ...params)
        {
            if(mainActivity.checkConnection())
            {
                try{
                    JSONObject committees = mainActivity.getData(mainActivity.currentCompany.getString("protocol")+mainActivity.currentCompany.getString("domain")+
                            mainActivity.companyCommitteeAPI+companyId);
                    mainActivity.dbHelper.insertData(mainActivity.companyCommitteeAPI+companyId, committees.toString());
                }catch (Exception e){e.printStackTrace();}

            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();

            for (ListView l:listViews) l.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            ArrayList<JSONObject> list = new ArrayList<>();
            try{
                JSONArray committees = (  new JSONObject(mainActivity.dbHelper.getData(mainActivity.companyCommitteeAPI+companyId))  ).getJSONArray("data");
                for(int i=0;i<committees.length();i++) list.add(committees.getJSONObject(i));

                ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
                layoutParams.height = committees.length()*60* (int)(mainActivity.getResources().getDisplayMetrics().density);
                listView.setLayoutParams(layoutParams);
            }catch (Exception e){e.printStackTrace();}
            try{
                CommitteeListMeetingPageAdapter adapter = new CommitteeListMeetingPageAdapter(list, mainActivity.getApplicationContext(),mainActivity);
                listView.setAdapter(adapter);
            }catch (Exception e){e.printStackTrace();}

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
