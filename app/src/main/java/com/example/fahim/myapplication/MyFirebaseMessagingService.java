package com.example.fahim.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import static android.content.ContentValues.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static int count = 0;
    public static MainActivity mainActivity;

    public MyFirebaseMessagingService() { }


    @Override
    public void onNewToken(String token) {

    }



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try{
            System.out.println(remoteMessage.getData());
            JSONObject object = new JSONObject(remoteMessage.getData().get("message"));
            sendNotification(object, remoteMessage.getData());
        }catch (Exception e){e.printStackTrace();}
    }


    //This method is only generating push notification
    private void sendNotification(JSONObject message, Map<String, String> row) {

        try
        {
            if(message.getString("Type:").equals("Meeting") || message.getString("Type:").equals("Agenda")) {
                updateMeeting(message);
            }
        }catch (Exception e){e.printStackTrace();}
    }


    public void updateMeeting(final JSONObject message)
    {
        try{
            String notificationMsg = message.getString("Type:")+"         "+message.getString("Action:");
            if(message.getString("Action:").equals("Published")) notificationMsg += "      Start At: "+message.getString("Start At:");
            else if(message.getString("Action:").equals("Attendance")) notificationMsg = "Meeting Started . Requesting Attendance";

            PendingIntent contentIntent = null;
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.login_logo))
                    .setSmallIcon(R.drawable.login_logo)
                    .setContentTitle(message.getString("Title:"))
                    .setContentText(notificationMsg)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(contentIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(count, notificationBuilder.build());
            count++;
        }catch (Exception e){e.printStackTrace();}
        try{
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try{
                        if(message.getString("Action:").equals("Attendance")){
                            attendenceDialogBox(message);
                        }
                        else {
                            if(mainActivity.findViewById(R.id.meetingPageList)!=null) mainActivity.meetingButtonFunction(mainActivity.getCurrentFocus());
                            else if(mainActivity.findViewById(R.id.listAgenda)!=null) mainActivity.showMeetingDetailPage(mainActivity.currentMeetingId,mainActivity.currentCommitteeTitle);
                        }
                    }catch (Exception e){e.printStackTrace();}
                }
            });

        }catch (Exception e){e.printStackTrace();}

    }

    public void attendenceDialogBox(final JSONObject message)
    {
        String title="";
        try{
            title = message.getString("Title:");
        }catch (Exception e){e.printStackTrace();}
        AlertDialog dialog = new AlertDialog.Builder(mainActivity)
                .setTitle("Requesting Attendance for "+title)
                .setPositiveButton("Attend", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncTask<Void, Void, Void>() {
                            Boolean success = false;
                            protected Void doInBackground(Void... unused) {
                                // Background Code
                                try{
                                    String url = mainActivity.currentCompany.getString("protocol")+mainActivity.currentCompany.getString("domain") +
                                            mainActivity.meetingAttendenceMemberAPI + message.getString("Meeting");
                                    JSONObject param = new JSONObject();
                                    param.put("attend","Physically_Present");
                                    if(mainActivity.checkConnection()) {
                                        JSONObject object = mainActivity.PostPutMethod(url,param,mainActivity.POST_METHOD);
                                        success = object.getBoolean("status");
                                    }
                                }catch (Exception e){e.printStackTrace();}
                                return null;
                            }
                            protected void onPostExecute(Void unused) {
                                // Post Code
                                super.onPostExecute(unused);
                                if(success)
                                {
                                    mainActivity.showToast("Attendence Updated successfully");
                                    TextView attendence_status = mainActivity.findViewById(R.id.attendence_status);
                                    if(attendence_status != null){
                                        attendence_status.setText("Present");
                                        attendence_status.setTextColor(Color.GREEN);
                                    }
                                }
                                else mainActivity.showToast("Failed to give attendance");
                            }
                        }.execute();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

}


