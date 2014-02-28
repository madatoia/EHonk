package com.example.ehonkv1;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
		
		GPSTracker tracker = new GPSTracker(this);
	    if (tracker.canGetLocation() == false) {
	        tracker.showSettingsAlert();
	    } else {
	        latitude = tracker.getLatitude();
	        longitude = tracker.getLongitude();
	        pos = tracker.getLocation();
	        
	        System.out.println("lat:"+latitude);
	        System.out.println("long:"+longitude);
	        tracker.stopUsingGPS();
	       
	    }	
	    
	    addListenerOnSearch();
		addListenerOnCall();
		
	}

	public void addListenerOnSearch() {

		search = (Button)findViewById(R.id.buttonSearch);
		search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// check carNo and phone
				boolean ok = true;
				String carNo = insertCarNumber.getText().toString();
				
				ok = Constants.checkCarNo(carNo);
				
				if (ok == false) {
					Toast.makeText(getApplicationContext(), "Wrong format",
							Toast.LENGTH_LONG).show();
				} else {
					// call
					System.out.println("call");
				}
			}
		});
	}
	
	public void addListenerOnCall() {

		call = (Button) findViewById(R.id.buttonCall);
		
		call.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!phoneNo.equals("")){
					//sun 
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:"+phoneNo));
					startActivity(callIntent);
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
