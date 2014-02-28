package com.example.ehonkv1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import android.content.Intent;

public class Activate extends Activity {

	Spinner spin;
	Button activate;
	Button add;
	EditText newCarNo;
	Button deactivate;
	ArrayList<String> cars;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activate_activity);
		try {

			BufferedReader data = new BufferedReader(new InputStreamReader(openFileInput(Constants.PROFILE_FILE_NAME)));
			
			String line = data.readLine(); // telephone no

			line = data.readLine();
			while ((line = data.readLine()) != null) {
				cars.add(line);
			}
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
				String searchCar = (String) spin.getSelectedItem();

				Toast.makeText(getApplicationContext(), "Searching: "
						+ searchCar, Toast.LENGTH_SHORT);
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
					ArrayList<String> list = new ArrayList();
					list.add(newCarStr);
					list.addAll(cars);

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
				//TRIMITE MESAJ DEACTIVARE
			}
		});
	}

}

class CustomOnItemSelectedListener implements OnItemSelectedListener {
 
  public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
  }
 
  @Override
  public void onNothingSelected(AdapterView<?> arg0) {
	// TODO Auto-generated method stub
  }
 
}
