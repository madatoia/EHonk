package com.example.ehonkv1;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Meniu extends Activity {

	TextView tv;
	Button activate;
	Button searchCar;
	Context apCtext;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meniu);
		
		apCtext = this.getApplicationContext();
		addListenerActivate();
		addListenerOnSearch();
	}
	public void addListenerOnSearch() {
		 
		searchCar = (Button)findViewById(R.id.buttonSearch);
		 
		searchCar.setOnClickListener(new OnClickListener() {
		 
			  @Override
			  public void onClick(View v) {
				  Intent intent = new Intent(apCtext, SearchCar.class);
				  startActivity(intent); 					 
			}
		});
	}
	
	public void addListenerActivate() {
		 
		activate = (Button)findViewById(R.id.buttonActivate);
		 
		activate.setOnClickListener(new OnClickListener() {
		 
			  @Override
			  public void onClick(View v) {
				  Intent intent = new Intent(apCtext, Activate.class);
				  startActivity(intent); 					 
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
