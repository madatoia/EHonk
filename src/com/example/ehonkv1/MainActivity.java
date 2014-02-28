package com.example.ehonkv1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
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

public class MainActivity extends Activity {

	TextView tv;
	Button set;
	EditText insertPhone;
	EditText insertCarNumber;
	String phoneNumber = "";
	String ph = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			FileInputStream data = openFileInput(Constants.PROFILE_FILE_NAME);
			data.close();
			
			//data already setted
			Intent intent = new Intent(getApplicationContext(), Meniu.class);
			  startActivity(intent);
		} catch (FileNotFoundException e) {
			// nu am setari initiale
			setContentView(R.layout.activity_main);
			AccountManager am = AccountManager.get(this);
			Account[] accounts = am.getAccounts();

			for (Account ac : accounts) {
				String actype = ac.type;

				if (actype.equals("com.whatsapp")) {
					phoneNumber = ac.name;
				}
			}
			insertPhone = (EditText) findViewById(R.id.editText1);
			insertPhone.setText(phoneNumber);

			
			insertCarNumber = (EditText) findViewById(R.id.editText2);
			addListenerOnSearch();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addListenerOnSearch() {

		set = (Button) findViewById(R.id.button1);
		tv = (TextView) findViewById(R.id.textView1);
		
		set.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// check carNo and phone
				char[] numbers = insertPhone.getText().toString().toCharArray();
				boolean ok = true;
				if (numbers[0] != '4' || numbers[1] != '0' || numbers[2] != '7'
						|| numbers.length != 11)
					ok = false;
				if(ok){
					for (int i = 3; i < 11; i++) {
						if (numbers[i] < '0' || numbers[i] > '9')
							ok = false;
					}
				}
				
				String carNo = insertCarNumber.getText().toString();
				ok = Constants.checkCarNo(carNo);
				
				if(ok == false){
					Toast.makeText(getApplicationContext(), "Wrong format", Toast.LENGTH_LONG).show();
				} else{
					//scriu in fisier
					try {
						FileOutputStream out = openFileOutput(Constants.PROFILE_FILE_NAME, Context.MODE_PRIVATE);
						out.write((phoneNumber+"\n").getBytes());
						out.write(carNo.getBytes());
						out.close();
						
						Intent intent = new Intent(getApplicationContext(), Meniu.class);
						  startActivity(intent); 
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
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
