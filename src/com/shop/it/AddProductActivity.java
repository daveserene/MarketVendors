package com.shop.it;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.shop.Model.DBAdapter;


public class AddProductActivity extends ActionBarActivity {
	EditText price;
	EditText vendor;
	Spinner commodity;
	Spinner market;
	Spinner unit;

	String userMarket, userName = "";
	
	DBAdapter d;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_product);

		Bundle extras = getIntent().getExtras();

        if (extras != null) {
            userName = extras.getString("username");
            userMarket = extras.getString("market");
            
            Log.d("received user: ", userName);
            Log.d("received market: ", userMarket);
            
        }

		
		d = new DBAdapter(this);
		d.open();
		
		unit = (Spinner) findViewById(R.id.addUnitList);
		commodity = (Spinner) findViewById(R.id.addCommodityList);
		price = (EditText) findViewById(R.id.addPriceValue);
		

		Button btnSaveCommodity = (Button) findViewById(R.id.saveCommodityButton);
		btnSaveCommodity.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
		
				
				if(price.getText().toString().matches("")){
					Toast.makeText(getBaseContext(), "Price field cannot be empty", Toast.LENGTH_SHORT).show();
				    return;
				}else {
					try {
						  
						  long id = d.insertItem("Commodity",userName, userMarket, commodity.getSelectedItem().toString(), price.getText().toString(), unit.getSelectedItem().toString());
						  
						  //Clear the form
						  price.setText("", TextView.BufferType.EDITABLE);
						  
						  Toast.makeText(getApplicationContext(), "Saved "+commodity.getSelectedItem().toString(),
									Toast.LENGTH_SHORT).show();
						  Log.d("SHOPIT====>>>>", "Adding db values "+id);
						}catch(SQLiteConstraintException e) {
					      Toast.makeText(getApplicationContext(), "You have already saved this Item",
									Toast.LENGTH_LONG).show();
					      Log.d("SHOPIT====>>>>", "Error Adding db values "+e.getMessage());
					      
						}				  
				}				

			}
		});

	}

	public void onDestroy()
	{
		super.onDestroy();
		d.close();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_product, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.myPrices) {

			Intent myPricesActivity = new Intent(getBaseContext(),
					
					MyPrices.class);
			Bundle extras = new Bundle(); 
			  extras.putString("username",  userName); 
			  extras.putString("market", userMarket);

			  myPricesActivity.putExtras(extras);
			startActivity(myPricesActivity);

			//return true;
		}else if (id == R.id.viewAllPrices) {

			Intent viewAllActivity = new Intent(getBaseContext(),
					ViewAll.class);

			Bundle s = new Bundle(); 
			  s.putString("username",  userName); 
			  s.putString("market", userMarket);

			  viewAllActivity.putExtras(s);

			startActivity(viewAllActivity);

			return true;
		}else if (id == R.id.addProduct) {

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
