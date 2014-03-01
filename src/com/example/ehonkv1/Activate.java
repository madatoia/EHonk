package com.example.ehonkv1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class Activate extends Activity {

	Spinner spin;
	Button activate;
	Button add;
	EditText newCarNo;
	Button deactivate;
	ArrayList<String> cars = new ArrayList<String>();
	double latitude, longitude;
	Context mCtext;
	boolean activated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activate_activity);
		mCtext = getApplicationContext();
		try {

			BufferedReader data = new BufferedReader(new InputStreamReader(
					openFileInput(Constants.PROFILE_FILE_NAME)));

			String line = data.readLine(); // telephone no

			while ((line = data.readLine()) != null) {
				if (!line.subSequence(0, 2).equals("id"))
					cars.add(line);
				else {
					activated = true;
				}
			}
			data.close();
			cars.clear();
			Toast.makeText(getApplicationContext(),
					"You have already activated an other car",
					Toast.LENGTH_LONG).show();
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

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addListenerOnSpinnerItemSelection() {
		spin = (Spinner) findViewById(R.id.spinner1);
		spin.setOnItemSelectedListener(new CustomOnItemSelectedListener());
	}

	// get the selected dropdown list value
	public void addListenerOnButton() {

		spin = (Spinner) findViewById(R.id.spinner1);
		activate = (Button) findViewById(R.id.buttonActivate);

		activate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!activated) {
					String selectedCar = (String) spin.getSelectedItem();

					Toast.makeText(getApplicationContext(),
							"Activating: " + selectedCar, Toast.LENGTH_SHORT)
							.show();

					GPSTracker tracker = new GPSTracker(mCtext);
					if (tracker.canGetLocation() == false) {
						tracker.showSettingsAlert();
					} else {
						latitude = tracker.getLatitude();
						longitude = tracker.getLongitude();

						System.out.println("lat:" + latitude);
						System.out.println("long:" + longitude);
						tracker.stopUsingGPS();

					}

					// TRIMITE MESAJ SERVER

					// dupa raspuns;
					String id = "id";
					try {
						BufferedWriter data = new BufferedWriter(
								new OutputStreamWriter(openFileOutput(
										Constants.PROFILE_FILE_NAME,
										Context.MODE_APPEND)));
						data.append("\n" + id);
						data.close();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"You have already activated an other car",
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

					// scriu in fisier data
					try {

						BufferedWriter data = new BufferedWriter(
								new OutputStreamWriter(openFileOutput(
										Constants.PROFILE_FILE_NAME,
										Context.MODE_APPEND)));
						data.append("\n" + newCarStr);
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
				// TRIMITE MESAJ DEACTIVARE
				File inFile = new File(Constants.PROFILE_FILE_NAME);
				File outFile = new File("temp.txt");
				try {
					BufferedReader data = new BufferedReader(new FileReader(
							inFile));

					BufferedWriter temp = new BufferedWriter(new FileWriter(
							outFile));

					String line = data.readLine(); // telephone no
					while ((line = data.readLine()) != null) {
						if (!line.subSequence(0, 2).equals("id")) {
							temp.write(line);
						}
					}

					temp.close();
					data.close();

					outFile.renameTo(inFile);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
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
