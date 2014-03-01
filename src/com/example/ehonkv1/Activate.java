package com.example.ehonkv1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class Activate extends FragmentActivity implements LocationListener {

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1; // 1 minute
    
    private static final String REGISTER_ERROR1 = "Invalid registration, try again!";
    private static final String REGISTER_ERROR2 = "An error occured during registration, try again!";
    private static final String UNREGISTER_ERROR1 = "An error occured during unregistration, try again!";
    private static final String UNREGISTER_ERROR2 = "Unregistered!";
    
	LocationManager locationManager = null;
	ConnectivityManager connMgr = null;

	Spinner spin;
	Button activate;
	Button add;
	EditText newCarNo;
	Button deactivate;
	ArrayList<String> cars = new ArrayList<String>();
	double latitude, longitude;
	Context mCtext = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activate_activity);
		
		mCtext = getApplicationContext();
		try {
			BufferedReader data = new BufferedReader(new InputStreamReader(
					openFileInput(Constants.PROFILE_FILE_NAME)));

			String line;
			
			while ((line = data.readLine()) != null) {
					if(!line.equals("\n"))
						cars.add(line);
			}
			data.close();
		} catch (IOException e) {
			
		} finally {
			spin = (Spinner) findViewById(R.id.spinner1);

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, cars);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spin.setAdapter(dataAdapter);

			addListenerOnButton();
			addListenerOnSpinnerItemSelection();
			addListenerOnSearch();
			addListenerOnView();
		}
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
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(Activate.this);
			alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

	        // On pressing Settings button
	        alertDialog.setPositiveButton("Settings",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        Intent intent = new Intent(
	                        		android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	                        Activate.this.startActivity(intent);
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
	    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(Activate.this);
			alertDialog.setMessage("Network is not enabled. Do you want to go to settings menu?");

	        // On pressing Settings button
	        alertDialog.setPositiveButton("Settings",
	                new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                        Intent intent = new Intent(
	                        		android.provider.Settings.ACTION_WIRELESS_SETTINGS);
	                        Activate.this.startActivity(intent);
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
                LocationManager.GPS_PROVIDER,
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

	public void addListenerOnSpinnerItemSelection() {
		spin = (Spinner) findViewById(R.id.spinner1);
		spin.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}
	
    private class RegisterTask extends AsyncTask<String, Void, String> {
        
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
			return "Register Sent!";
		}
    	
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	if( conn != null && message != null) {
        		if( message.equals(REGISTER_ERROR1) || message.equals(REGISTER_ERROR2)) {
					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        		}
        		else {
    				String id = message;
    				//Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
   					SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
    				editor.putString("id", id);
    				editor.commit();
        		}
        	}
       }
    }
    
    private class UnRegisterTask extends AsyncTask<String, Void, String> {
        
    	HttpURLConnection conn = null;
    	InputStream is = null;
    	String message = null;
    	@Override
        protected String doInBackground(String... urls) {
              
            // params[0] is the url.
        	// params[1] is the blocking_client_id
            URL url;
			try {
				String url_query = urls[0];
				url_query += "?blocking_client_id=" + urls[1];
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
			return "UnRegister Sent!";
		}
    	
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	if( conn != null && message != null) {
        		if( message.equals(UNREGISTER_ERROR1) || message.equals(UNREGISTER_ERROR2)) {
					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        		}
        		else {
    				Toast.makeText(getApplicationContext(), "Car deactivated", Toast.LENGTH_SHORT).show();
					SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
					editor.putString("id", "-1");
					editor.commit();
        		}
        	}
       }
    }

	// get the selected dropdown list value
	public void addListenerOnButton() {

		spin = (Spinner) findViewById(R.id.spinner1);
		activate = (Button) findViewById(R.id.buttonActivate);

		activate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
				String restoredText = prefs.getString("id", "-1");
				
				if (restoredText.equals("-1")) {
					String selectedCar = (String) spin.getSelectedItem();

					Toast.makeText(getApplicationContext(),
							"Activating: " + selectedCar, Toast.LENGTH_SHORT)
							.show();

					Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					String longitude = "" + location.getLongitude();
					String latitude = "" + location.getLatitude();
					String addr = "http://141.85.223.25:3000/register.json";
					new RegisterTask().execute(addr, selectedCar, latitude, longitude);

				} else {
					Toast.makeText(getApplicationContext(),
							"You have are already active in other car",
							Toast.LENGTH_LONG).show();
				}
			}
		});
	}

	public void addListenerOnSearch() {

		add = (Button) findViewById(R.id.addNewCar);
		newCarNo = (EditText) findViewById(R.id.newCarEditText);

		add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String newCarStr = newCarNo.getText().toString();

				if (Constants.checkCarNo(newCarStr)) {
					ArrayList<String> list = new ArrayList<String>();
					list.add(newCarStr);
					list.addAll(cars);
					cars.add(newCarStr);
					
					newCarNo.setText("");

					// scriu in fisier data
					try {

						BufferedWriter data = new BufferedWriter(
								new OutputStreamWriter(openFileOutput(
										Constants.PROFILE_FILE_NAME,
										Context.MODE_APPEND)));
						data.append(newCarStr+"\n");
						data.close();

					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
							getBaseContext(),
							android.R.layout.simple_spinner_item, list);
					dataAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spin.setAdapter(dataAdapter);
				} else {
					ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
							getBaseContext(),
							android.R.layout.simple_spinner_item, cars);
					dataAdapter
							.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spin.setAdapter(dataAdapter);
				}
			}
		});
	}

	public void addListenerOnView() {

		deactivate = (Button) findViewById(R.id.buttonDeactivate);

		deactivate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				SharedPreferences prefs = getPreferences(MODE_PRIVATE); 
				String restoredText = prefs.getString("id", "-1");
				
				if (restoredText.equals("-1")) {
					Toast.makeText(mCtext, "No car activated", Toast.LENGTH_SHORT).show();
				} else {
					String addr = "http://141.85.223.25:3000/unregister.json";
					new UnRegisterTask().execute(addr, restoredText);
				}
			}
		});
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

class CustomOnItemSelectedListener implements OnItemSelectedListener {

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
	}

}
