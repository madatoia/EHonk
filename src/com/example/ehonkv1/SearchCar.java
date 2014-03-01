package com.example.ehonkv1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SearchCar extends Activity {

	TextView tv;
	Button search;
	Button call;
	EditText insertCarNumber;
	public Location pos;
	String phoneNo = "40747644830";
	double latitude, longitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search);
		insertCarNumber = (EditText)findViewById(R.id.editText1);
		
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

	    addListenerOnSearch();
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	    
	    LocationManager locationManager = 
	            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    
	    if (!isGPSEnabled) {
	        // Create a dialog here that requests the user to enable GPS, and use an intent
	        // with the android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS action
	        // to take the user to the Settings screen to enable GPS when they click "OK"
	        // Setting Dialog Message
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchCar.this);
			alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

	        // On pressing Settings button
	        alertDialog.setPositiveButton("Settings",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        Intent intent = new Intent(
	                        		android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                        SearchCar.this.startActivity(intent);
	                    }
	                });

	        // on pressing cancel button
	        alertDialog.setNegativeButton("Cancel",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.cancel();
	                    }
	                });

	        // Showing Alert Message
	        alertDialog.show();
	    }
        Log.v("isGPSEnabled", "=" + isGPSEnabled);
	    
	    // check network connection
		ConnectivityManager connMgr = (ConnectivityManager) 
	            getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    boolean isNetworkEnabled = (networkInfo != null) && (networkInfo.isConnected());
	    
	    if (!isNetworkEnabled) {
	    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchCar.this);
			alertDialog.setMessage("Network is not enabled. Do you want to go to settings menu?");

	        // On pressing Settings button
	        alertDialog.setPositiveButton("Settings",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        Intent intent = new Intent(
	                        		android.provider.Settings.ACTION_WIRELESS_SETTINGS);
	                        SearchCar.this.startActivity(intent);
	                    }
	                });

	        // on pressing cancel button
	        alertDialog.setNegativeButton("Cancel",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        dialog.cancel();
	                    }
	                });

	        // Showing Alert Message
	        alertDialog.show();
	    }
	    Log.v("isGPSEnabled", "=" + isNetworkEnabled);
	}

	private void readStream(InputStream in) {
		  BufferedReader reader = null;
		  try {
		    reader = new BufferedReader(new InputStreamReader(in));
		    String line = "";
		    String msg = "";
		    while ((line = reader.readLine()) != null) {
		    	msg += line;
		    }
		    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
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
		} 

	public void addListenerOnSearch() {

		search = (Button)findViewById(R.id.buttonSearch);
		search.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
			boolean ok = true;
			
			String carNo = insertCarNumber.getText().toString();
			// check carNo and phone
			ok = Constants.checkCarNo(carNo);
			if(!ok) {
				Toast.makeText(getApplicationContext(), "Wrong format",
						Toast.LENGTH_LONG).show();
			}
			else {
				//http://141.85.223.25:3000
				String addr = "http://141.85.223.25:3000/search.json";
				URL url;
				try {
				    
					url = new URL(addr);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setUseCaches(false);
					conn.setAllowUserInteraction(false);
					conn.setRequestMethod("GET");
					conn.addRequestProperty("car_number", carNo);
					conn.connect();
					
					readStream(conn.getInputStream());
					/*
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(SearchCar.this);
					alertDialog.setMessage(conn.getResponseCode());
			        // On pressing Settings button
			        alertDialog.setPositiveButton("Ok",
			                new DialogInterface.OnClickListener() {
			                    public void onClick(DialogInterface dialog, int which) {
			                    	dialog.cancel();
			                    }
			                });
					alertDialog.show();
					*/
					Toast.makeText(getApplicationContext(), "Wrong format",
							Toast.LENGTH_LONG).show();
					System.out.println(conn.getResponseCode());
					System.out.println(conn.getResponseMessage());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.err.println(e.getMessage());
						e.printStackTrace();
					}
			
				}
			}
		
		
		});
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
