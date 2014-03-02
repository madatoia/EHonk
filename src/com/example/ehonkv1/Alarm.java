package com.example.ehonkv1;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.Toast;

public class Alarm extends BroadcastReceiver
{    
	static String id = "";
	
	 @Override
     public void onReceive(Context context, Intent intent) 
     {   
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        
		String addr = "http://141.85.223.25:3000/get_messages.json";
		new PollingTask().execute(addr, Alarm.id);
		
		
		
		//Intent notification_intent = new Intent(context, NotificationAlert.class);
		//context.startActivity(notification_intent);
		
		final NotificationManager mgr=
	            (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	        Notification note=new Notification(R.drawable.ic_launcher,
	                                                        "Android Example Status message!",
	                                                        System.currentTimeMillis());
	         
	        note.setLatestEventInfo(context, "Android Example Notification Title",
	                                "This is the android example notification message", null);
	         
	        //After uncomment this line you will see number of notification arrived
	        note.number=2;
	        mgr.notify(111, note);
		
        wl.release();
     }

     public void SetAlarm(Context context, String id)
     {
    	 Alarm.id = id;
         AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
         Intent i = new Intent(context, Alarm.class);
         PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
         am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000 * 10, pi); // Millisec * Second * Minute
     }

     public void CancelAlarm(Context context)
     {
         Intent intent = new Intent(context, Alarm.class);
         PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
         AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
         alarmManager.cancel(sender);
     }
     
 	private String readStream(InputStream in) {
		BufferedReader reader = null;
		String msg = "";
		try {
			reader = new BufferedReader(new InputStreamReader(in));
		    String line = "";
		    while ((line = reader.readLine()) != null) {
		    	msg += line;
		    }
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
		        }
		    }
		  }
		  
		  return msg;
	}
 	
	
    private class PollingTask extends AsyncTask<String, Void, String> {
        
    	HttpURLConnection conn = null;
    	InputStream is = null;
    	String message = null;
    	@Override
        protected String doInBackground(String... params) {
              
        // params[0] is the url.
    		URL url;
    		try {
    			String url_query = params[0] + "?blocking_client_id=" + params[1];
    			url = new URL(url_query);
    			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    			conn.setUseCaches(false);
    			conn.setAllowUserInteraction(false);
    			conn.setRequestMethod("GET");
    			conn.connect();
    			InputStream is = conn.getInputStream();
    			String message = readStream(is);
    		} catch (MalformedURLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}        
			return "Request Sent!";
		}
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	if( conn != null && message != null) {
        		
        		

        		/*
				AlertDialog.Builder builder = new AlertDialog.Builder(SearchCar.this);
				builder.setMessage(message)
				       .setCancelable(false)
				       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   dialog.cancel();
				           }
				       });
				AlertDialog alert = builder.create();
				alert.show();
				if(is!=null){
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				*/
        	}
       }
        
        
    }
 }
