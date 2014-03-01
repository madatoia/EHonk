package com.example.ehonkv1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SearchCar extends FragmentActivity implements LocationListener {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute

	Button search;
	EditText insertCarNumber;
	LocationManager locationManager = null;
	ConnectivityManager connMgr = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search);
		insertCarNumber = (EditText)findViewById(R.id.editText1);
		
		addListenerOnSearch();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
	}
	
	@Override
	protected void onStart() {
	    super.onStart();
	    
	    locationManager = 
	            (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    boolean isGPSEnabled = (locationManager != null) && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	    
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
		connMgr = (ConnectivityManager) 
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
	    
	    // bind location client to locationManager
	    locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
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

	public void addListenerOnSearch() {

		search = (Button)findViewById(R.id.buttonSearch);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean ok = true;
			
				String carNo = insertCarNumber.getText().toString();
				// 	check carNo and phone
				ok = Constants.checkCarNo(carNo);

				if(!ok) {
					Toast.makeText(getApplicationContext(), "Wrong format",
							Toast.LENGTH_LONG).show();
				}
				else {
					Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					String longitude = "" + location.getLongitude();
					String latitude = "" + location.getLatitude();
					String addr = "http://141.85.223.25:3000/search.json";
					new RequestTask().execute(addr, carNo, latitude, longitude);
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
	
    private class RequestTask extends AsyncTask<String, Void, String> {
        
    	HttpURLConnection conn = null;
    	InputStream is = null;
    	String message = null;
    	@Override
        protected String doInBackground(String... urls) {
              
            // params[0] is the url.
        	// params[1] is the car_number
    		// params[2] is the latitude
    		// params[3] is the longitude
            URL url;
			try {
				String url_query = urls[0];
				url_query += "?car_number=" + urls[1] + "&lat=" + urls[2] + "&long=" + urls[3];
				url = new URL(url_query);
				conn = (HttpURLConnection) url.openConnection();
				conn.setUseCaches(false);
				conn.setAllowUserInteraction(false);
				conn.setRequestMethod("GET");
				conn.connect();
				is = conn.getInputStream();
				message = readStream(is);
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
        	}
       }
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
