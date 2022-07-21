package com.example.fahim.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener , Serializable {

    public static final String GET_METHOD  = "GET";
    public static final String POST_METHOD  = "POST";
    public static final String PUT_METHOD = "PUT";
    public static final int READ_TIMEOUT       = 10000;
    public static final int CONNECTION_TIMEOUT = 10000;
    public static String REMEMBER_ME = "remember_me";
    public static String VALID_CREDENTIALS = "credentials";
    public static String DEVICE_TOKEN = "" ;
    public static boolean isDeviceTokenNeeded = false;
    public static int LAST_PAGE ;
    private FirebaseAnalytics mFirebaseAnalytics;
    public static String currentMeetingId = "";
    public static String currentCommitteeTitle = "";
    public static String currentAgendaId = "";
    public static String currentCompanyId = "";
    public static String currentCommitteeId = "";

    public static JSONArray Credentials=null;
    public static JSONObject currentCompany;
    public static JSONObject currentUser;
    public static String currentPassword;
    public static DbHelper dbHelper;

    public static String selectCompanyURL = "https://auth.boardmaestro.com/api/company/by/";  //1544591516 , 1552280725
    public static String deviceRegistrationAPI = "/api/user/device/add";
    public static String companyListAPI = "/api/mobile/user/company";
    public static String companyCommitteeAPI = "/api/v2/user/committee?company=";             //GET /api/v2/user/committee?company={companyId}
    public static String loginAPI = "/api/authenticate";
    public static String changePasswordAPI = "/api/user/profile/change/password";
    public static String avatarAPI = "/api/user/avatar/";                                     // /api/user/avatar/{userID}
    public static String meetingAPI = "/api/meeting/id/";                                     // /api/meeting/{meetingId}
    public static String upcomingMeetingAPI = "/api/user/meeting/upcoming";
    public static String committeeMeetingAPI = "/api/v2/user/meeting?committee=";             // /api/v2/user/meeting?committee={committeeId}
    public static String topNoticeAPI = "/api/user/notice/top";
    public static String allNoticeAPI = "/api/user/correspondence/all/";                      // /api/user/correspondence/all/{start}/{limit}
    public static String noticeParticipantsAPI = "/api/correspondence/detail/";               // /api/correspondence/detail/{correspondence}
    public static String noticeFilesAPI = "/api/correspondence/file/all/";                    // api/correspondence/file/all/{correspondence}
    public static String noticeFileDownloadAPI = "/api/correspondence/file/download/";        // /api/correspondence/file/download/{fileId}
    public static String libraryCategoryAPI = "/api/library/categories";
    public static String libraryCategoryFilesAPI = "/api/library/category/files/";            //   api/library/category/files/{category}
    public static String libraryFileDownloadAPI = "/api/library/file/download/";              // /api/correspondence/file/download/{fileId}
    public static String filePackAPI = "/api/filepack/files/";                                //  api/filepack/files/{filepack}
    public static String filepackFileDownloadAPI = "/api/filepack/file/download/";            // api/filepack/file/download/{file}
    public static String meetingAgendaAPI = "/api/meeting/agenda/";                           // /api/meeting/agenda/{meeting}
    public static String yearPlannerAPI = "/api/planner/plans/";                              // api/planner/plans/{start?}/{end?}
    public static String searchAPI = "/api/search/advanced/";
    public static String checkConnectionAPI = "/api/check/";
    public static String meetingStartFinishAPI = "/api/meeting/status/change/";               //PUT    /api/meeting/status/change/{meetingID}
    public static String meetingAttendenceRequestAPI = "/api/meeting/request/attendance/";    //GET    /api/meeting/request/attendance/{meetingId}
    public static String meetingAttendenceMemberAPI = "/api/user/meeting/attend/";            //POST   /api/user/meeting/attend/{meetingId}
    public static String agendaStartEndAPI = "/api/agenda/update/status/";                    //PUT    /api/agenda/update/status/{agenda}





    public static String[] Days = {"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};
    public static String[] Months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    public static String[] MON = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    public static String[] companyNames;
    public static  PopupWindow popupWindow ;


    public void companySelectionWindow(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Enter Company UID")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        if(task.equals("")) {
                            showToast("Incorrect UID");
                            companySelectionWindow(MainActivity.this);
                        }
                        else new comapnySelectionThread(task,0).execute("");
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        exitApp();
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    public void addCompanyButtonFunction(View view) {
        EditText uid = findViewById(R.id.companyUID);
        try{
            String u = uid.getText().toString();
            new comapnySelectionThread(u,1).execute("");
        }catch (Exception e){showToast("Incorrect UID");uid.setText("");}
    }
    class comapnySelectionThread extends AsyncTask<String,String,String> {
        String UID;
        int fromWhere;
        ProgressDialog progress;
        JSONObject temp;

        comapnySelectionThread(String s,int i)
        {
            UID = s;
            fromWhere = i;
        }
        @Override
        protected String doInBackground(String ...params)
        {
            temp = getCompanyDomain(UID);
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();
            if (temp == null)
            {
                showToast("Incorrect UID");
                if(fromWhere ==0 ) companySelectionWindow(MainActivity.this);
            }
            else {
                showToast("Company Added");
                if(fromWhere ==0 )
                {
                    currentCompany = temp;
                }
                else if(fromWhere == 1)
                {
                    JSONObject j = new JSONObject();
                    try
                    {
                        j.put("company",temp);
                        j.put("user",   new JSONObject());
                        j.put("pass","");
                        Credentials.put(j);
                        dbHelper.insertData(VALID_CREDENTIALS,Credentials.toString());

                        settingsButtonFunction(new View(getApplicationContext()));
                    } catch (Exception e){e.printStackTrace();}
                }
                System.out.println(temp);
            }
        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

    }


    public void loginButtonFunction(View view) {
        hideKeyboard(view);
        new loginThread(view).execute("");
    }
    class loginThread extends AsyncTask<String,String,String> {
        EditText name,pass;
        ProgressDialog progress;
        String username,password;
        CheckBox cb;

        JSONObject tempUser;
        String tempPassword;
        View view;

        loginThread(View v)
        {
            name = findViewById(R.id.UserName);
            pass = findViewById(R.id.Password);
            username = name.getText().toString();
            password = pass.getText().toString();

            cb = findViewById(R.id.checkBox);
            if(cb.isChecked()) dbHelper.insertData(REMEMBER_ME,"true");
            else dbHelper.insertData(REMEMBER_ME,"false");
            view = v;
        }
        @Override
        protected String doInBackground(String ...params)
        {

            tempUser = getUser(username,password);
            tempPassword = password;
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();

            try
            {
                if(tempUser != null)
                {
                    currentUser = tempUser;
                    currentPassword = tempPassword;
                }

                if(username.equals(currentUser.getJSONObject("user").getString("email")) && password.equals(currentPassword))
                {
                    try
                    {
                        JSONObject o = new JSONObject();
                        o.put("company",currentCompany);
                        o.put("user",currentUser);
                        o.put("pass",currentPassword);
                        if (Credentials == null)
                        {
                            Credentials = new JSONArray();
                            Credentials.put(o);
                        }
                        else
                        {
                            for (int i=0;i<Credentials.length();i++)
                            {
                                if(  Credentials.getJSONObject(i).getJSONObject("company").equals(currentCompany))
                                {
                                    Credentials.getJSONObject(i).put("user",currentUser);
                                    Credentials.getJSONObject(i).put("pass",currentPassword);
                                }
                            }
                        }


                        dbHelper.insertData(VALID_CREDENTIALS,Credentials.toString());
                    }
                    catch (Exception e){e.printStackTrace();}
                    System.out.println(currentUser);

                    if(tempUser == null) showToast("Login Successful (OFFLINE) ");
                    else showToast("Login Successful");

                    homeButtonFunction(view);

                    try{
                        new sendDeviceIDThread().execute("");
                    }catch (Exception e){e.printStackTrace();}
                }
                else
                {
                    showToast("Login Failed");
                }
            } catch (Exception e){
                showToast("Login Failed");
                e.printStackTrace();}

        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

    }
    class sendDeviceIDThread extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String ...params)
        {
            if(isDeviceTokenNeeded) {
                System.out.println("FireBase Token Sent to Server ***************");
                registerDevice();
            }
            return " ";
        }
    }


    public void logoutButtonFunction(View view) {
        showToast("Logged Out");
        showLoginPage();
        popupWindow.dismiss();
        rememberUser();
    }
    public void showLoginPage() {
        setContentView(R.layout.login_page);
        Spinner spinner = findViewById(R.id.spinner);

        if(Credentials == null)
        {
            companySelectionWindow(MainActivity.this);
            spinner.setVisibility(View.INVISIBLE);
            isDeviceTokenNeeded = true ;
        }
        else
        {
            if(Credentials.length()==1)
            {
                try
                {
                    currentCompany = Credentials.getJSONObject(0).getJSONObject("company");
                    currentUser = Credentials.getJSONObject(0).getJSONObject("user");
                    currentPassword = Credentials.getJSONObject(0).getString("pass");
                    spinner.setVisibility(View.INVISIBLE);
                    rememberUser();
                } catch (Exception e){e.printStackTrace();}
            }
            else
            {
                spinner.setOnItemSelectedListener(this);
                ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,companyNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        }
    }
    public void logoutWindow(View view) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.logout_window, null);

        ImageView iv = findViewById(R.id.propic);int y = iv.getBottom();int x = iv.getLeft();int h = iv.getHeight();
        // create the popup window
        Display currentDisplay = getWindowManager().getDefaultDisplay();
        float dw = currentDisplay.getWidth();
        float dh = currentDisplay.getHeight();
        int width = (int)dw/3;
        int height = (int)dh/3;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.TOP ,x,y*2);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                popupWindow.dismiss();
                return true;
            }
        });


        ImageView pic = popupView.findViewById(R.id.ProfilePicture);
        try{
            String url = currentCompany.getString("protocol")+
                    currentCompany.getString("domain")+
                    avatarAPI+currentUser.getJSONObject("user").getString("id")+"?token="+
                    currentUser.getString("token");
            Picasso.get().load(url).transform(new RoundedTransformation(100, 0)).into(pic);
        }catch (Exception e){e.printStackTrace();}
        TextView name = popupView.findViewById(R.id.UserName); TextView designation = popupView.findViewById(R.id.Designation);
        try{
            name.setText(currentUser.getJSONObject("user").getString("name"));
            designation.setText(currentUser.getJSONObject("user").getString("designation"));
        } catch (Exception e){e.printStackTrace();}
    }


    public void homeButtonFunction(View view) {
        LAST_PAGE = R.layout.home_page;

        setContentView(R.layout.home_page);
        new homePageThread().execute("");
    }
    class homePageThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        @Override
        protected String doInBackground(String ...params)
        {
            if(checkConnection())
            {
                try {                // token update
                    currentUser = getUser(currentUser.getJSONObject("user").getString("email") , currentPassword);
                    System.out.println("********************** Token Updated");
                    System.out.println(currentUser.getString("token"));
                } catch (Exception e){e.printStackTrace();}

                try {               // upcoming meeting
                    JSONObject UpcomingMeeting = getData(currentCompany.getString("protocol")+currentCompany.getString("domain")+upcomingMeetingAPI) ;
                    dbHelper.insertData(upcomingMeetingAPI, UpcomingMeeting.toString());
                } catch (Exception e){e.printStackTrace();}

                try{      // top notice
                    JSONObject TopNotice = getData(currentCompany.getString("protocol")+currentCompany.getString("domain")+topNoticeAPI);
                    dbHelper.insertData(topNoticeAPI, TopNotice.toString());
                }catch (Exception e){e.printStackTrace();}

                try{      // all notice
                    JSONObject AllNotice = getData(currentCompany.getString("protocol")+currentCompany.getString("domain")+allNoticeAPI);
                    dbHelper.insertData(allNoticeAPI, AllNotice.toString());
                }catch (Exception e){e.printStackTrace();}

            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();

            loadProfilePic();
            showMeetingData(upcomingMeetingAPI);
            showNoticeData(topNoticeAPI);

        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

    }


    public void meetingButtonFunction(View view) {
        LAST_PAGE = R.layout.meeting_page;
        setContentView(R.layout.meeting_page);
        loadProfilePic();
        new meetingPageThread().execute("");

    }
    class meetingPageThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        @Override
        protected String doInBackground(String ...params)
        {
            if(checkConnection())
            {
                try{         // getting all companies
                    JSONObject Companies = getData(currentCompany.getString("protocol")+currentCompany.getString("domain")+companyListAPI);
                    dbHelper.insertData(companyListAPI, Companies.toString());
                } catch (Exception e){e.printStackTrace();}
            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();

            ListView listView = findViewById(R.id.listCompany);
            ArrayList<JSONObject> list = new ArrayList<>();
            try{
                JSONObject Companies = new JSONObject( dbHelper.getData(companyListAPI) ) ;
                JSONArray array = Companies.getJSONArray("data");
                for(int i =0 ; i<array.length();i++) list.add(array.getJSONObject(i));
            }catch (Exception e){e.printStackTrace();}
            try{
                CompanyListMeetingPageAdapter adapter = new CompanyListMeetingPageAdapter(list, getApplicationContext(),MainActivity.this);
                listView.setAdapter(adapter);
            }catch (Exception e){e.printStackTrace();}

        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

    }
    public void showMeetingData(String api) {
        try {
            ListView listView = findViewById(R.id.list);
            JSONObject Meetings = new JSONObject(dbHelper.getData(api));
            JSONArray array = Meetings.getJSONArray("data");
            ArrayList<JSONObject> list = new ArrayList<>();
            for(int i =0 ; i<array.length();i++) list.add(array.getJSONObject(i));

            if(api.equals(upcomingMeetingAPI))
            {
                UpcomingMeetingAdapter adapter = new UpcomingMeetingAdapter(list, getApplicationContext(),MainActivity.this);
                listView.setAdapter(adapter);
            }

        }catch (Exception e){e.printStackTrace();}
    }
    class meetingDetailPageThread extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        JSONObject meeting = null;
        String meetingId;
        String committee_name;
        meetingDetailPageThread(String id, String name) {
            this.meetingId = id;
            this.committee_name = name;
        }
        protected void onPreExecute() {
            // Pre Code
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }
        protected Void doInBackground(Void... unused) {
            // Background Code
            try{
                if(checkConnection()){
                    meeting = getData(currentCompany.getString("protocol")+currentCompany.getString("domain") +meetingAPI + meetingId).getJSONObject("data");
                }
            }catch (Exception e){e.printStackTrace();}
            return null;
        }
        protected void onPostExecute(Void unused) {
            // Post Code
            super.onPostExecute(unused);
            progress.dismiss();
            if(meeting == null) return;

            setContentView(R.layout.meeting_detail);
            final TextView meetingName,committeeName, address, time , attendence_status;
            meetingName = findViewById(R.id.meetingName);
            committeeName = findViewById(R.id.committeeName);
            address = findViewById(R.id.address);
            time = findViewById(R.id.name);
            attendence_status = findViewById(R.id.attendence_status);
            TextView status = findViewById(R.id.status);
            try{
                meetingName.setText(meeting.getString("title"));
                committeeName.setText(committee_name);
                address.setText(meeting.getString("location"));
                time.setText(meeting.getString("start"));
                status.setText(meeting.getString("status"));

                try{        // getting user attendence status
                    JSONArray userList = meeting.getJSONArray("users");
                    for(int i=0;i< userList.length();i++) {
                        if(userList.getJSONObject(i).getInt("id") == currentUser.getJSONObject("user").getInt("id")) {
                            if(userList.getJSONObject(i).getJSONObject("pivot").getString("attendance").equals("null")){
                                attendence_status.setText("Not Present");
                                attendence_status.setTextColor(Color.RED);
                            }
                            else {
                                attendence_status.setText("Present");
                                attendence_status.setTextColor(Color.GREEN);
                            }

                            break;
                        }
                    }
                }catch (Exception e){e.printStackTrace();}

            } catch (Exception e){e.printStackTrace();}

            findViewById(R.id.backArrow).setOnClickListener(new View.OnClickListener() {          // Back Button
                @Override
                public void onClick(View v) {

                    if(LAST_PAGE == R.layout.home_page) homeButtonFunction(v);
                    else if (LAST_PAGE == R.layout.meeting_page) meetingButtonFunction(v);
                    else if(LAST_PAGE == R.layout.search_page) searchButtonFunction(v);
                }
            });

            loadProfilePic();

            try{           // ongoing agenda list show/hide
                TextView t = findViewById(R.id.startText);
                ImageView i = findViewById(R.id.startIcon);
                if(meeting.getString("status").equals("started")){
                    findViewById(R.id.OngoingAgendaContainer).setVisibility(View.VISIBLE);
                    t.setText("Finish");
                    i.setImageResource(R.drawable.power_off);
                }
                else if(meeting.getString("status").equals("completed")){
                    findViewById(R.id.OngoingAgendaContainer).setVisibility(View.GONE);
                    t.setText("Finished");
                    i.setImageResource(R.drawable.power_grey);
                }
                else if(meeting.getString("status").equals("published")){
                    findViewById(R.id.OngoingAgendaContainer).setVisibility(View.GONE);
                    t.setText("Start");
                    i.setImageResource(R.drawable.power_button);
                }
            }catch (Exception e){e.printStackTrace();}

            try{             // Start Meeting Button
                if( currentUser.getJSONObject("user").getString("designation").equals("Secretary") )
                {
                    findViewById(R.id.startMeeting).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("Confirm Your Action")
                                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            TextView t = findViewById(R.id.startText);
                                            if(t.getText().toString().equals("Start"))              // Meeting is starting
                                            {
                                                new AsyncTask<Void, Void, Void>() {
                                                    ProgressDialog progressDialog;
                                                    Boolean success = false;
                                                    protected void onPreExecute() {
                                                        // Pre Code
                                                        progressDialog = new ProgressDialog(MainActivity.this);
                                                        progressDialog.setTitle("Loading");
                                                        progressDialog.setMessage("Wait while loading...");
                                                        progressDialog.setCancelable(false);
                                                        progressDialog.show();
                                                    }
                                                    protected Void doInBackground(Void... unused) {
                                                        // Background Code
                                                        try{
                                                            String url = currentCompany.getString("protocol")+currentCompany.getString("domain") +meetingStartFinishAPI+meetingId;
                                                            JSONObject param = new JSONObject();
                                                            param.put("status","started");
                                                            if(checkConnection()){
                                                                JSONObject object = PostPutMethod(url,param,PUT_METHOD);
                                                                success = object.getBoolean("status");
                                                            }

                                                        }catch (Exception e){e.printStackTrace();}
                                                        return null;
                                                    }
                                                    protected void onPostExecute(Void unused) {
                                                        // Post Code
                                                        super.onPostExecute(unused);
                                                        progressDialog.dismiss();
                                                        if(success)
                                                        {
                                                            findViewById(R.id.OngoingAgendaContainer).setVisibility(View.VISIBLE);
                                                            TextView t = findViewById(R.id.startText);
                                                            ImageView i = findViewById(R.id.startIcon);
                                                            t.setText("Finish");
                                                            i.setImageResource(R.drawable.power_off);
                                                            showToast("Meeting Started Successfully");
                                                        }
                                                        else showToast("Failed to Start Meeting !!!!");
                                                    }
                                                }.execute();
                                            }
                                            else if(t.getText().toString().equals("Finish"))        // Meeting is finished
                                            {
                                                new AsyncTask<Void, Void, Void>() {
                                                    ProgressDialog progressDialog;
                                                    Boolean success = false;
                                                    protected void onPreExecute() {
                                                        // Pre Code
                                                        progressDialog = new ProgressDialog(MainActivity.this);
                                                        progressDialog.setTitle("Loading");
                                                        progressDialog.setMessage("Wait while loading...");
                                                        progressDialog.setCancelable(false);
                                                        progressDialog.show();
                                                    }
                                                    protected Void doInBackground(Void... unused) {
                                                        // Background Code
                                                        try{
                                                            String url = currentCompany.getString("protocol")+currentCompany.getString("domain") +meetingStartFinishAPI+meetingId;
                                                            JSONObject param = new JSONObject();
                                                            param.put("status","completed");
                                                            if(checkConnection()){
                                                                JSONObject object = PostPutMethod(url,param,PUT_METHOD);
                                                                success = object.getBoolean("status");
                                                            }
                                                        }catch (Exception e){e.printStackTrace();}
                                                        return null;
                                                    }
                                                    protected void onPostExecute(Void unused) {
                                                        // Post Code
                                                        super.onPostExecute(unused);
                                                        progressDialog.dismiss();
                                                        if(success)
                                                        {
                                                            findViewById(R.id.OngoingAgendaContainer).setVisibility(View.VISIBLE);
                                                            TextView t = findViewById(R.id.startText);
                                                            ImageView i = findViewById(R.id.startIcon);
                                                            t.setText("Finish");
                                                            i.setImageResource(R.drawable.power_off);
                                                            showToast("Meeting Started Successfully");
                                                        }
                                                        else showToast("Failed to Finish Meeting !!");
                                                    }
                                                }.execute();
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .create();
                            dialog.setCanceledOnTouchOutside(false);dialog.show();
                        }
                    });
                }
                else{
                    findViewById(R.id.startMeeting).setVisibility(View.INVISIBLE);
                }
            }catch (Exception e){e.printStackTrace();}

            try{           // refresh button
                findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            showMeetingDetailPage(meeting.getString("id"),committee_name);
                        }catch (Exception e){e.printStackTrace();}
                    }
                });
            }catch (Exception e){e.printStackTrace();}

            try{        /// give attendence
                findViewById(R.id.attendence).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("Confirm Your Action")
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try{
                                            if(meeting.getString("status").equals("started"))
                                            {
                                                try{
                                                    if( currentUser.getJSONObject("user").getString("designation").equals("Secretary") )
                                                    {
                                                        new AsyncTask<Void, Void, Void>() {
                                                            ProgressDialog progressDialog;
                                                            Boolean success = false;
                                                            protected void onPreExecute() {
                                                                // Pre Code
                                                                progressDialog = new ProgressDialog(MainActivity.this);
                                                                progressDialog.setTitle("Loading");
                                                                progressDialog.setMessage("Wait while loading...");
                                                                progressDialog.setCancelable(false);
                                                                progressDialog.show();
                                                            }
                                                            protected Void doInBackground(Void... unused) {
                                                                // Background Code
                                                                try{
                                                                    String url = currentCompany.getString("protocol")+currentCompany.getString("domain") +meetingAttendenceRequestAPI + meetingId;
                                                                    if(checkConnection()) {
                                                                        JSONObject object = getData(url);
                                                                        success = object.getBoolean("status");
                                                                    }
                                                                }catch (Exception e){e.printStackTrace();}
                                                                return null;
                                                            }
                                                            protected void onPostExecute(Void unused) {
                                                                // Post Code
                                                                super.onPostExecute(unused);
                                                                progressDialog.dismiss();
                                                                if(success)
                                                                {
                                                                    showToast("Attendence Request sent successfully");
                                                                }
                                                                else showToast("Failed To Send Attendence Request !!");
                                                            }
                                                        }.execute();          // sending attendance request
                                                    }
                                                    else {
                                                        if(attendence_status.getText().toString().equals("Present")){
                                                            showToast("You have already given Attendance");
                                                        }else {
                                                            new AsyncTask<Void, Void, Void>() {
                                                                ProgressDialog progressDialog;
                                                                Boolean success = false;
                                                                protected void onPreExecute() {
                                                                    // Pre Code
                                                                    progressDialog = new ProgressDialog(MainActivity.this);
                                                                    progressDialog.setTitle("Loading");
                                                                    progressDialog.setMessage("Wait while loading...");
                                                                    progressDialog.setCancelable(false);
                                                                    progressDialog.show();
                                                                }
                                                                protected Void doInBackground(Void... unused) {
                                                                    // Background Code
                                                                    try{
                                                                        String url = currentCompany.getString("protocol")+currentCompany.getString("domain") +meetingAttendenceMemberAPI + meetingId;
                                                                        JSONObject param = new JSONObject();
                                                                        param.put("attend","Physically_Present");
                                                                        if(checkConnection()) {
                                                                            JSONObject object = PostPutMethod(url,param,POST_METHOD);
                                                                            success = object.getBoolean("status");
                                                                        }
                                                                    }catch (Exception e){e.printStackTrace();}
                                                                    return null;
                                                                }
                                                                protected void onPostExecute(Void unused) {
                                                                    // Post Code
                                                                    super.onPostExecute(unused);
                                                                    progressDialog.dismiss();
                                                                    if(success)
                                                                    {
                                                                        showToast("Attendence Updated successfully");
                                                                        attendence_status.setText("Present");
                                                                        attendence_status.setTextColor(Color.GREEN);
                                                                    }
                                                                    else showToast("Failed To Update Attendance !!");
                                                                }
                                                            }.execute();
                                                        }
                                                    }
                                                }catch (Exception e){e.printStackTrace();}
                                            }
                                            else if(meeting.getString("status").equals("published"))
                                            {
                                                showToast("Meeting has not started yet");
                                            }
                                            else if(meeting.getString("status").equals("completed"))
                                            {
                                                showToast("Meeting has been finished");
                                            }
                                        }catch (Exception e){e.printStackTrace();}
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .create();
                        dialog.setCanceledOnTouchOutside(false);dialog.show();
                    }
                });
            }catch (Exception e){e.printStackTrace();}

            try{
                new MeetingAdapter.agendaListThread(meeting.getString("id"),MainActivity.this , meeting.getString("status")).execute("");
            }catch (Exception e){e.printStackTrace();}
        }
    }
    public void showMeetingDetailPage(final String meetingId, final String committee_name){
        currentMeetingId = meetingId;
        currentCommitteeTitle = committee_name;
        new meetingDetailPageThread(meetingId,committee_name).execute();
    }


    public void noticeButtonFunction(View view) {
        setContentView(R.layout.notice_page);
        new noticePageThread().execute("");
    }
    class noticePageThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        @Override
        protected String doInBackground(String ...params)
        {
            if(checkConnection())
            {
                try{      // all notice
                    JSONObject AllNotice = getData(currentCompany.getString("protocol")+currentCompany.getString("domain")+allNoticeAPI);
                    dbHelper.insertData(allNoticeAPI, AllNotice.toString());
                }catch (Exception e){e.printStackTrace();}

            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();

            loadProfilePic();
            showNoticeData(allNoticeAPI);

        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

    }
    public void showNoticeData(String api) {
        try {
            ListView listView = findViewById(R.id.listNotice);
            ArrayList<JSONObject> list = new ArrayList<>();
            try
            {
                JSONObject Notice = new JSONObject(dbHelper.getData(api));
                JSONArray array = Notice.getJSONArray("data");
                for(int i =0 ; i<array.length();i++) list.add(array.getJSONObject(i));
            }catch (Exception e){e.printStackTrace();}

            if( api.equals(topNoticeAPI) )
            {
                try{
                    TopNoticeAdapter adapter = new TopNoticeAdapter(list, getApplicationContext(),MainActivity.this);
                    listView.setAdapter(adapter);
                }catch (Exception e){e.printStackTrace();}
            }
            else if(api.equals(allNoticeAPI))
            {
                try{
                    NoticeAdapter adapter = new NoticeAdapter(list, getApplicationContext(),MainActivity.this,  list.get(0).getInt("id"));
                    listView.setAdapter(adapter);
                }catch (Exception e){e.printStackTrace();}
            }

        }catch (Exception e){e.printStackTrace();}
    }

    public void fileButtonFunction(View view) {
        setContentView(R.layout.file_pack_page);
        loadProfilePic();
        new meetingPageThread().execute("");
    }    // using meetingPageThread


    public void libraryButtonFunction(View view) {
        setContentView(R.layout.library_page);
        new libraryPageThread().execute("");
    }
    class libraryPageThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        @Override
        protected String doInBackground(String ...params)
        {
            if(checkConnection())
            {
                try {         // library category
                    JSONObject LibraryCategory = getData(currentCompany.getString("protocol")+currentCompany.getString("domain")+libraryCategoryAPI);
                    dbHelper.insertData(libraryCategoryAPI, LibraryCategory.toString());
                } catch (Exception e){e.printStackTrace();}
            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();

            loadProfilePic();
            ListView catList = findViewById(R.id.listCategory);
            try
            {
                JSONObject category = (new JSONObject(dbHelper.getData(libraryCategoryAPI))).getJSONObject("data");
                ArrayList<JSONObject> list = new ArrayList<>();
                Iterator<String> IDs = category.keys();
                while (IDs.hasNext())
                {
                    String id = IDs.next();
                    JSONObject json = new JSONObject();
                    json.put("id",id);
                    json.put("name",category.getString(id));
                    list.add(json);
                }

                LibraryCategoryAdapter adapter = new LibraryCategoryAdapter(list, getApplicationContext(),MainActivity.this);
                catList.setAdapter(adapter);
            } catch (Exception e){e.printStackTrace();}

        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

    }


    public void searchButtonFunction(View view) {
        setContentView(R.layout.search_page);
        loadProfilePic();
        LAST_PAGE = R.layout.search_page;

        ConstraintLayout filterCommittee = findViewById(R.id.filter);
        filterCommittee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new filterButtonThread().execute("");
            }
        });

        ConstraintLayout startDate = findViewById(R.id.startDate);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog( MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        TextView start = findViewById(R.id.startDateText);
                        start.setText(Integer.toString(year)+"-"+Integer.toString(monthOfYear+1)+"-"+Integer.toString(dayOfMonth));
                    }
                } , 2019, 00, 01);
                datePickerDialog.show();
            }
        });

        ConstraintLayout endDate = findViewById(R.id.endDate);
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog( MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        TextView start = findViewById(R.id.endDateText);
                        start.setText(Integer.toString(year)+"-"+Integer.toString(monthOfYear+1)+"-"+Integer.toString(dayOfMonth));
                    }
                } , 2019, 00, 01);
                datePickerDialog.show();
            }
        });

        ConstraintLayout performSearch = findViewById(R.id.performSearch);
        performSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(v);
                Switch agenda,meeting,notice,content,committee;
                agenda = findViewById(R.id.switchAgenda);
                meeting =findViewById(R.id.switchMeeting);
                notice = findViewById(R.id.switchNotice);
                content = findViewById(R.id.switchContent);
                committee = findViewById(R.id.switchCommittee);

                String searchParameter = "?";
                if(agenda.isChecked()) searchParameter+="search_criteria[]=agendas&";
                if(content.isChecked()) searchParameter+="with_content=1&";
                else searchParameter+="with_content=0&";
                if(meeting.isChecked()) searchParameter+="search_criteria[]=meetings&";
                if(notice.isChecked()) searchParameter+="search_criteria[]=correspondences&";
                if(committee.isChecked()) searchParameter+="search_criteria[]=committees&";

                TextView start , end; start = findViewById(R.id.startDateText); end = findViewById(R.id.endDateText);
                String startDate = start.getText().toString(); String endDate = end.getText().toString();

                EditText searchKey = findViewById(R.id.searchKey);
                String key="";
                try{
                    key = searchKey.getText().toString();
                }catch (Exception e){e.printStackTrace();}

                if(key.split(" ").length==0 || key.equals("")) {
                    showToast("Please Provide Search Key");
                }
                else {
                    searchParameter+="search_string="+key+"&";
                    if( startDate.equals("Start Date") && endDate.equals("End Date") )
                    {
                        System.out.println(searchParameter);
                        new searchThread(searchParameter).execute("");
                    }
                    else if(!startDate.equals("Start Date") && !endDate.equals("End Date"))
                    {
                        searchParameter+="start_date="+startDate+"&end_date="+endDate;
                        System.out.println(searchParameter);
                        new searchThread(searchParameter).execute("");
                    }
                    else showToast("Please Select both date or Select none");
                }

            }
        });
    }
    class searchThread extends AsyncTask<String,String,String> {
        String param;
        JSONObject object;
        searchThread(String p)
        {
            param = p;
        }
        @Override
        protected String doInBackground(String ...params)
        {
            try
            {
                URL url = new URL(currentCompany.getString("protocol")+currentCompany.getString("domain")+ searchAPI + param);
                String auth = "Bearer "+currentUser.getString("token");
                //JSONObject postDataParams = new JSONObject();

                //postDataParams.put("password",pass1);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod(POST_METHOD);
                conn.setRequestProperty("Authorization",auth);
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                //writer.write(JsonToString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    object = new JSONObject(sb.toString());
                }
                else {
                    System.out.println("failed to reset password "+responseCode);
                    object = null;
                }
            } catch (Exception e){e.printStackTrace();}
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);

            ListView listView = findViewById(R.id.listSearch);
            ArrayList<JSONObject> list = new ArrayList<>();

            JSONArray meetings ,agendas,polls,committees,correspondences,correspondence_files,files,libraries;
             try{
                 JSONObject result = object.getJSONObject("data");

                 meetings = result.getJSONArray("meetings");
                 agendas = result.getJSONArray("agendas");
                 polls = result.getJSONArray("polls");
                 committees = result.getJSONArray("committees");
                 correspondences = result.getJSONArray("correspondences");
                 correspondence_files = result.getJSONArray("correspondence_files");
                 files = result.getJSONArray("files");
                 libraries = result.getJSONArray("libraries");

                 try{
                     for(int i=0;i<meetings.length();i++)
                     {
                         JSONObject o = new JSONObject(meetings.getJSONObject(i).toString());
                         o.put("category","Meeting");
                         list.add(o);
                     }
                 } catch (Exception e){e.printStackTrace();}
                 try{
                     for(int i=0;i<agendas.length();i++)
                     {
                         JSONObject o = new JSONObject(agendas.getJSONObject(i).toString());
                         o.put("category","Agenda");
                         list.add(o);
                     }
                 } catch (Exception e){e.printStackTrace();}
                 try{
                     for(int i=0;i<polls.length();i++)
                     {
                         JSONObject o = new JSONObject(polls.getJSONObject(i).toString());
                         o.put("category","Poll");
                         list.add(o);
                     }
                 } catch (Exception e){e.printStackTrace();}
                 try{
                     for(int i=0;i<committees.length();i++)
                     {
                         JSONObject o = new JSONObject(committees.getJSONObject(i).toString());
                         o.put("category","Committee");
                         list.add(o);
                     }
                 } catch (Exception e){e.printStackTrace();}
                 try{
                     for(int i=0;i<correspondences.length();i++)
                     {
                         JSONObject o = new JSONObject(correspondences.getJSONObject(i).toString());
                         o.put("category","Correspondence");
                         list.add(o);
                     }
                 } catch (Exception e){e.printStackTrace();}
                 try{
                     for(int i=0;i<correspondence_files.length();i++)
                     {
                         JSONObject o = new JSONObject(correspondence_files.getJSONObject(i).toString());
                         o.put("category","Correspondence File");
                         list.add(o);
                     }
                 } catch (Exception e){e.printStackTrace();}
                 try{
                     for(int i=0;i<files.length();i++)
                     {
                         JSONObject o = new JSONObject(files.getJSONObject(i).toString());
                         o.put("category","File");
                         list.add(o);
                     }
                 } catch (Exception e){e.printStackTrace();}
                 try{
                     for(int i=0;i<libraries.length();i++)
                     {
                         JSONObject o = new JSONObject(libraries.getJSONObject(i).toString());
                         o.put("category","Library");
                         list.add(o);
                     }
                 } catch (Exception e){e.printStackTrace();}

             }catch (Exception e){e.printStackTrace();}

            SearchListAdapter adapter = new SearchListAdapter(list,getApplicationContext(),MainActivity.this);
            listView.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute()
        {

        }

    }
    class filterButtonThread extends AsyncTask<String,String,String> {
        JSONArray committees;

        @Override
        protected String doInBackground(String ...params)
        {
            try{
                String companyID = currentCompany.getString("id");
                committees = getData(currentCompany.getString("protocol")+currentCompany.getString("domain")+companyCommitteeAPI+companyID).getJSONArray("data");
            }catch (Exception e){e.printStackTrace();}

            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.custom_dialog);
            dialog.setTitle("Committees");
            ListView listView =  dialog.findViewById(R.id.List);
            ArrayList<JSONObject> list = new ArrayList<>();
            try {
                for(int i=0;i<committees.length();i++)  list.add(committees.getJSONObject(i));
            }catch (Exception e){e.printStackTrace();}
            try{
                FilterListAdapter adapter = new FilterListAdapter(list,getApplicationContext(),MainActivity.this);
                listView.setAdapter(adapter);
            } catch (Exception e){e.printStackTrace();}
            dialog.show();

        }
    }


    public void plannerButtonFunction(View view) {
        setContentView(R.layout.year_planner_page);
        loadProfilePic();

        final DatePicker datePicker =findViewById(R.id.datePicker);
        LinearLayout ll = (LinearLayout) datePicker.getChildAt(0);
        LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);
        ll2.getChildAt(1).setVisibility(View.GONE);
        java.util.Calendar c= java.util.Calendar.getInstance();
        c.set(c.get(java.util.Calendar.YEAR),0,1);
        datePicker.setMinDate(c.getTimeInMillis());
        c.set(c.get(java.util.Calendar.YEAR)+1, 11, 1);
        datePicker.setMaxDate(c.getTimeInMillis());

        String start = Integer.toString(c.get(java.util.Calendar.YEAR)-1)+"-01-01 00:00:00";
        String end = Integer.toString(c.get(java.util.Calendar.YEAR)-1)+"-12-31 23:00:00";
        new yearPlannerThread("?start="+start+"&end="+end).execute("");
        TextView header = findViewById(R.id.header);
        header.setText("Meeting List of "+Integer.toString(c.get(java.util.Calendar.YEAR)-1)+" of All Month");

        datePicker.init(c.get(java.util.Calendar.YEAR)-1,0,1,new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Button selectAllMonth = findViewById(R.id.selectAllMonth);
                selectAllMonth.setBackgroundColor(Color.WHITE);
                String start = Integer.toString(year)+"-"+Integer.toString(month+1)+"-01 00:00:00";
                String end = Integer.toString(year)+"-"+Integer.toString(month+1)+"-30 23:00:00";
                new yearPlannerThread("?start="+start+"&end="+end).execute("");

                TextView header = findViewById(R.id.header);
                header.setText("Meeting List of "+Integer.toString(year)+" of Month "+MON[month].toUpperCase());
            }
        });

        Button selectAllMonth = findViewById(R.id.selectAllMonth);
        selectAllMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundResource(R.drawable.blue_button);

                String start = Integer.toString(datePicker.getYear())+"-01-01 00:00:00";
                String end = Integer.toString(datePicker.getYear())+"-12-31 23:00:00";
                new yearPlannerThread("?start="+start+"&end="+end).execute("");
                TextView header = findViewById(R.id.header);
                header.setText("Meeting List of "+Integer.toString(datePicker.getYear())+" of All Month");
            }
        });
    }
    class yearPlannerThread extends AsyncTask<String,String,String> {
        String param;
        JSONObject object;
        yearPlannerThread(String p)
        {
            param = p;
        }
        @Override
        protected String doInBackground(String ...params)
        {
            try
            {
                object = getData(currentCompany.getString("protocol")+currentCompany.getString("domain")+yearPlannerAPI+param);
            } catch (Exception e){e.printStackTrace();}
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            ListView listView = findViewById(R.id.listYearPlanner);
            ArrayList<JSONObject> list = new ArrayList<>();
            try{
                JSONArray meetings = object.getJSONArray("data");
                for(int i=0; i<meetings.length();i++) list.add(meetings.getJSONObject(i));
            } catch (Exception e){e.printStackTrace();}
            try{
                YearPlannerListAdapter adapter = new YearPlannerListAdapter(list,getApplicationContext(),MainActivity.this);
                listView.setAdapter(adapter);
            }catch (Exception e){e.printStackTrace();}
        }

        @Override
        protected void onPreExecute()
        {

        }

    }


    public void settingsButtonFunction(View view) {
        setContentView(R.layout.settings_page);

        try{
            TextView status = findViewById(R.id.IntStorage);
            TextView files = findViewById(R.id.fileSize);
            TextView db = findViewById(R.id.dbSize);
            status.setText( formatSize(getOccupiedMemorySize())+"/"+formatSize(getInternalMemorySize()));
            files.setText(formatSize(getFilesSize())+"/"+formatSize(getInternalMemorySize()));
            db.setText(formatSize(dbHelper.getSize())+"/"+formatSize(getInternalMemorySize()));
        }catch (Exception e){e.printStackTrace();}

        try{
            ProgressBar p1 = findViewById(R.id.progressBar);
            p1.setProgress((int)(100*(double)getOccupiedMemorySize()/(double) getInternalMemorySize()));
        }catch (Exception e){e.printStackTrace();}
        try{
            ProgressBar p2 = findViewById(R.id.progressBar2);
            p2.setProgress((int)(100*(double)getFilesSize()/(double) getInternalMemorySize()));
        }catch (Exception e){e.printStackTrace();}
        try{
            ProgressBar p3 = findViewById(R.id.progressBar3);
            p3.setProgress((int)(100*(double)dbHelper.getSize()/(double) getInternalMemorySize()));
        }catch (Exception e){e.printStackTrace();}

        findViewById(R.id.clearMemory).setOnClickListener(new View.OnClickListener() {    //  Clear Memory
            @Override
            public void onClick(View view) {

            }
        });

        new settingsPageThread().execute("");
    }
    class settingsPageThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        @Override
        protected String doInBackground(String ...params)
        {
            if(checkConnection())
            {
                try{         // getting all companies
                    JSONObject Companies = getData(currentCompany.getString("protocol")+currentCompany.getString("domain")+companyListAPI);
                    dbHelper.insertData(companyListAPI, Companies.toString());
                } catch (Exception e){e.printStackTrace();}
            }
            return " ";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();

            loadProfilePic();

            try {
                ListView listView = findViewById(R.id.listCompany);
                JSONArray array = new JSONArray( dbHelper.getData(VALID_CREDENTIALS) );
                ArrayList<JSONObject> list = new ArrayList<>();
                for(int i =0 ; i<array.length();i++) list.add(array.getJSONObject(i).getJSONObject("company"));

                CompanyAdapter adapter = new CompanyAdapter(list, getApplicationContext(),MainActivity.this);
                listView.setAdapter(adapter);

            }catch (Exception e){e.printStackTrace();}
        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

    }


    public void changePasswordButtonFunction(View view){
        //new changePasswordThread().execute("");
    }
    class changePasswordThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        EditText newPass1;
        EditText newPass2;
        String pass1,pass2;
        Button submit;
        String msg;
        JSONObject j;

        changePasswordThread()
        {
            newPass1 = findViewById(R.id.newPass1);
            newPass2 = findViewById(R.id.newPass2);
            try{
                pass1 = newPass1.getText().toString();
                pass2 = newPass2.getText().toString();
            }catch (Exception e){newPass1.setText(" "); newPass2.setText(" ");}
        }

        @Override
        protected String doInBackground(String ...params)
        {
            if(!pass1.equals(pass2))
            {
                msg = "Please Correct Your Password";
                return "";
            }
            try
            {
                URL url = new URL(currentCompany.getString("protocol")+currentCompany.getString("domain")+changePasswordAPI);
                String auth = "Bearer "+currentUser.getString("token");
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("password",pass1);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod(POST_METHOD);
                conn.setRequestProperty("Authorization",auth);
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(JsonToString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK)
                {

                    BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    j = new JSONObject(sb.toString());
                    msg = j.getString("msg");
                }
                else {
                    System.out.println("failed to reset password "+responseCode);
                    msg = "Password Change Failed !!!!!";
                }
            } catch (Exception e){e.printStackTrace();}

            return "";
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            progress.dismiss();
            try
            {
                if(msg.equals("Password changed successfully"))
                {
                    showToast(j.getString("msg"));
                    settingsButtonFunction(new View(getApplicationContext()));

                    for(int i=0;i<Credentials.length();i++)
                    {
                        if (Credentials.getJSONObject(i).getJSONObject("company").equals(currentCompany))
                        {
                            Credentials.getJSONObject(i).put("pass",pass1);
                            dbHelper.insertData(VALID_CREDENTIALS,Credentials.toString());
                        }
                    }
                }
                else
                {
                    showToast(msg);
                    newPass1.setText(" "); newPass2.setText(" ");
                }
            } catch (Exception e){e.printStackTrace();}
        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        runtimeEnableAutoInit();
        initFireBase();

        intiDatabase();
        loadFromDatabase();
        showLoginPage();

    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
       try
       {
           currentCompany = Credentials.getJSONObject(position).getJSONObject("company");
           currentUser = Credentials.getJSONObject(position).getJSONObject("user");
           currentPassword = Credentials.getJSONObject(position).getString("pass");
       } catch (Exception e){e.printStackTrace();}
        rememberUser();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) { }



    public void rememberUser() {
        EditText us = findViewById(R.id.UserName);
        EditText ps = findViewById(R.id.Password);
        CheckBox cb = findViewById(R.id.checkBox);
        try
        {
            if(dbHelper.getData(REMEMBER_ME).equals("true"))
            {
                us.setText(currentUser.getJSONObject("user").getString("email"));
                ps.setText(currentPassword);
                cb.setChecked(true);
            }

        }
        catch (Exception e){
            us.setText("");
            ps.setText("");
            cb.setChecked(true);
            e.printStackTrace();
        }

        hideKeyboard(us);
    }
    public void intiDatabase() {
        dbHelper = new DbHelper(MainActivity.this);
        //dbHelper.clearTable();
        dbHelper.printTable();
    }
    public void loadFromDatabase() {
        try
        {
            Credentials = new JSONArray(dbHelper.getData(VALID_CREDENTIALS));
            companyNames = new String[Credentials.length()];
            for(int i=0;i<Credentials.length();i++)
            {
                companyNames[i] = Credentials.getJSONObject(i).getJSONObject("company").getString("name");
            }
        } catch (Exception e)
        {
            Credentials = null;
            e.printStackTrace();
        }
    }
    public static JSONObject getCompanyDomain(String UID) {
        try{

            System.out.println("getting *******************");
            URL url = new URL(selectCompanyURL+UID);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod(GET_METHOD);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK)
            {

                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line="";

                while((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                JSONObject x = new JSONObject(sb.toString());
                JSONArray y = x.getJSONArray("data");
                if(y.length()==0) return null;
                else return y.getJSONObject(0);
            }
            else {
                System.out.println("false : "+responseCode);
                return  null;
            }


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
    public  JSONObject getUser(String un, String pass) {

        try{
            System.out.println("getting User*************");
            URL url = new URL(currentCompany.getString("protocol")+currentCompany.getString("domain")+loginAPI);

            JSONObject postDataParams = new JSONObject();
            postDataParams.put("email", un);
            postDataParams.put("password",pass);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod(POST_METHOD);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.connect();

            OutputStream os = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(JsonToString(postDataParams));

            writer.flush();
            writer.close();
            os.close();


            int responseCode=conn.getResponseCode();


            if (responseCode == HttpsURLConnection.HTTP_OK)
            {

                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line="";

                while((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                JSONObject x = new JSONObject(sb.toString());
                System.out.println(x.toString());
                return  x;
            }
            else {
                System.out.println("false : Failed to get Token "+responseCode);
                return  null;
            }


        }
        catch (java.net.SocketTimeoutException e)
        {
            return  null;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }
    public JSONObject registerDevice() {
        try{
            String auth = "Bearer "+currentUser.getString("token");
            URL url = new URL(currentCompany.getString("protocol")+currentCompany.getString("domain")+deviceRegistrationAPI);

            System.out.println(url);

            JSONObject postDataParams = new JSONObject();
            postDataParams.put("device_uid", DEVICE_TOKEN);
            postDataParams.put("platform","android");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod(POST_METHOD);
            conn.setRequestProperty("Authorization",auth);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.connect();

            OutputStream os = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(JsonToString(postDataParams));

            writer.flush();
            writer.close();
            os.close();


            int responseCode=conn.getResponseCode();


            if (responseCode == HttpsURLConnection.HTTP_OK)
            {

                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line="";

                while((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                JSONObject x = new JSONObject(sb.toString());
                System.out.println("Device registered Successfully****************");
                System.out.println(x.toString());
                return  x;

            }
            else {
                System.out.println("false : Failed to get Token "+responseCode);
                return  null;
            }


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Failed to register device *************");
        return null;
    }
    public JSONObject getData(String param) {
        System.out.println("getting data");

        String result;
        String inputLine;
        try {
            System.out.println(param);
            String auth = "Bearer "+currentUser.getString("token");
            URL myUrl = new URL(param);
            HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
            System.out.println("opened connection");
            connection.setRequestMethod(GET_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setRequestProperty("Authorization",auth);
            connection.connect();

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while((inputLine = reader.readLine()) != null)
            {
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();

            result = stringBuilder.toString();
            connection.disconnect();

            JSONObject x = new JSONObject(result);
            System.out.println(x);
            return x;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            result = null;
        }
        return null;
    }
    public  JSONObject PostPutMethod(String param,JSONObject postDataParams,String method) {
        try{
            URL url = new URL(param);
            String auth = "Bearer "+currentUser.getString("token");
            System.out.println(url);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setConnectTimeout(CONNECTION_TIMEOUT);
            conn.setRequestMethod(method);
            conn.setRequestProperty("Authorization",auth);
            conn.setDoInput(true);
            conn.setDoOutput(true);

            conn.connect();

            OutputStream os = conn.getOutputStream();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(JsonToString(postDataParams));

            writer.flush();
            writer.close();
            os.close();


            int responseCode=conn.getResponseCode();


            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                BufferedReader in=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line="";

                while((line = in.readLine()) != null) {

                    sb.append(line);
                    break;
                }

                in.close();
                JSONObject x = new JSONObject(sb.toString());
                System.out.println(x.toString());
                return  x;

            }
            else {
                System.out.println("false : Failed to get Token "+responseCode);
                return  null;
            }


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
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
    public boolean checkConnection() {
        String result;
        String inputLine;
        try {
            URL myUrl = new URL(currentCompany.getString("protocol")+currentCompany.getString("domain")+checkConnectionAPI);
            HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();
            System.out.println("opened connection");
            connection.setRequestMethod(GET_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.connect();

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            while((inputLine = reader.readLine()) != null)
            {
                stringBuilder.append(inputLine);
            }
            reader.close();
            streamReader.close();

            result = stringBuilder.toString();
            connection.disconnect();

            JSONObject x = new JSONObject(result);
            System.out.println(x);
            if(x.getString("status").equals("You have found InfoSapex BoardMeeting")) return true;
            return false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            result = null;
        }
        return false;
    }

    public  void showToast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
    public void hideKeyboard(View v) {
        try {
            InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    public static String JsonToString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
    public void exitApp() {
        MainActivity.this.finish();
        System.exit(0);
    }
    public void loadProfilePic() {
        ImageView imageView = findViewById(R.id.propic);
        try{
            String url = currentCompany.getString("protocol")+
                    currentCompany.getString("domain")+
                    avatarAPI+currentUser.getJSONObject("user").getString("id")+"?token="+
                    currentUser.getString("token");
            Picasso.get().load(url).transform(new RoundedTransformation(100, 0)).into(imageView);
        }
        catch (Exception e){e.printStackTrace();}

    }

    public void runtimeEnableAutoInit() {
        // [START fcm_runtime_enable_auto_init]
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        // [END fcm_runtime_enable_auto_init]
    }
    public void initFireBase() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("Failed ********************");
                            return;
                        }
                        try{
                            String token = task.getResult().getToken();
                            DEVICE_TOKEN = token;
                            System.out.println("Token of firebase*************: "+token);
                        }catch (Exception e){
                            DEVICE_TOKEN = "";
                            e.printStackTrace();
                        }
                    }
                });

        MyFirebaseMessagingService.mainActivity = MainActivity.this;
    }

    public long getInternalMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        return stat.getBlockSizeLong() * stat.getBlockCount();
    }
    public long getOccupiedMemorySize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        return stat.getBlockSizeLong() * (stat.getBlockCount() - stat.getFreeBlocks());
    }
    public long getFilesSize() {
        File path = MainActivity.this.getFilesDir();
        long size =0 ;
        for(File f:path.listFiles())
        {
            size+=f.length();
        }

        return  size;
    }
    public static String formatSize(long size) {
        String result = Long.toString(size)+"B";
        double sz=0;
        if(size>=1024)
        {
            sz = size/1024.0;
            result = String.format("%.02f", sz) +" KB";

            if(sz>=1024)
            {
                sz = sz/1024.0;
                result = String.format("%.02f", sz) +" MB";

                if(sz>=1024)
                {
                    sz = sz/1024.0;
                    result = String.format("%.02f", sz) +" GB";
                }
            }
        }
        return result;
    }
    class fileDownloadThread extends AsyncTask<String,String,String> {
        ProgressDialog progress;
        String url;
        File file;
        String filename;
        ArrayList<JSONObject> agendaList;
        fileDownloadThread(String url,File file,String filename, ArrayList<JSONObject> Agendas)
        {
            this.url=url;
            this.file=file;
            this.filename=filename;
            agendaList = Agendas;
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
                Intent intent = new Intent(MainActivity.this, PdfReaderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("filename", filename);

                if(agendaList==null) bundle.putString("agenda",null);
                else {
                    JSONArray agenda = new JSONArray();
                    for(JSONObject j:agendaList) agenda.put(j);

                    bundle.putString("agenda",agenda.toString());
                }

                intent.putExtras(bundle);
                startActivity(intent);
            }
            else showToast("File Doesn't Exist");

        }

        @Override
        protected void onPreExecute()
        {
            progress = new ProgressDialog(MainActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
        }

    }

}

/*listView.setSelection(LIST_ITEM_CLICKED);         // set the focus of list view

    listView.setOnScrollListener(new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                    && (listView.getLastVisiblePosition() - listView.getHeaderViewsCount() -
                    listView.getFooterViewsCount()) >= (listView.getAdapter().getCount() - 1))
            {
            }
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    });
    */


