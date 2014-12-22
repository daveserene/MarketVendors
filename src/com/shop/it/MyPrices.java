package com.shop.it;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.shop.Adapter.MyPricesAdapter;
import com.shop.Model.DBAdapter;
import com.shop.Sync.CustomRequest;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shop.it.RowData;

public class MyPrices extends ActionBarActivity {
	private LayoutInflater mInflater;
	DBAdapter d;
	RowData rowData;
	
	private Vector<RowData> myPricesData;
	ArrayList<String> vendornames = new ArrayList();
	ArrayList<String> markets = new ArrayList();
	ArrayList<String> commodities = new ArrayList();
	ArrayList<String> prices = new ArrayList();
	ArrayList<String> units = new ArrayList();
	
	public ArrayList<String> commodityImageNames = new ArrayList();
	public ArrayList<Bitmap> commodityImages = new ArrayList();

	HashMap<String, Integer> listImages;
	RequestQueue queue;
	MyPricesAdapter adapter;
	
	
	String userName, userMarket = "";
	//SyncHelper syncMyPrices;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_prices);
		
		Bundle extras = getIntent().getExtras();

        if (extras != null) {
            userName = extras.getString("username");
            userMarket = extras.getString("market");
            
            Log.d("received user: ", userName);
            Log.d("received market: ", userMarket);
            
        }
		
		listImages = new HashMap<String, Integer>();
		//Get drawables for list view
		listImages.put("peas",R.drawable.peas);
		listImages.put("beans",R.drawable.beans);
		listImages.put("groundnuts",R.drawable.groundnuts);
		listImages.put("matooke",R.drawable.matooke);
		listImages.put("pineapples",R.drawable.pineapples);
		listImages.put("posho",R.drawable.posho);
		listImages.put("rice",R.drawable.rice);
		
		d = new DBAdapter(this);
		d.open();

		queue = Volley.newRequestQueue(this);

		//Load commodities from database
		getCommodities();

		myPricesData = new Vector<RowData>();
		mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		//Loop through vendors and populate list view
		for (int i = 0; i < vendornames.size(); i++) {
			try {
				Log.d("SHOPIT====>>>>", "VendorName: "+vendornames.get(i));
				
				rowData = new RowData(i, vendornames.get(i), markets.get(i), commodities.get(i), prices.get(i), units.get(i));
				myPricesData.add(rowData);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
		}
		
		adapter = new MyPricesAdapter(getBaseContext(), R.layout.activity_my_prices_list, R.id.commodityTitle, myPricesData, mInflater, listImages);
		
		ListView myPricesListView = (ListView)findViewById(R.id.myPricesListView);
		myPricesListView.setAdapter(adapter);

		Button uploadPrices = (Button) findViewById(R.id.uploadCommodities);
		
		uploadPrices.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String url = "http://10.0.2.2/shopIt/postPrices.php";
				Map<String, String> params = new HashMap<String, String>();
				
				for(int x = 0; x < vendornames.size(); x++) {
					params.put("vdr-"+x, vendornames.get(x));
					params.put("mkt-"+x, markets.get(x));
					params.put("cty-"+x, commodities.get(x));
					params.put("pcs-"+x, prices.get(x));
					params.put("uty-"+x, units.get(x));
					
				}
				

				CustomRequest jsObjRequest = new CustomRequest(Method.POST, url, params, new Response.Listener<JSONObject>() {

				            @Override
				            public void onResponse(JSONObject response) {
				            	try {
									if(response.get("name").toString().equalsIgnoreCase("New records created successfully")){
										Log.d("Success: ", response.toString());
										deleteAllCommodities();
										
										Intent addProductActivity = new Intent(getApplicationContext(), AddProductActivity.class); 

										Bundle extras = new Bundle(); 
										  extras.putString("username",  userName); 
										  extras.putString("market", userMarket);

										  addProductActivity.putExtras(extras);
										startActivity(addProductActivity);
									}else {
										if(response.toString().contains("Query was empty")) {
						            		Toast.makeText(getBaseContext(), "No Commodities to Sync...", Toast.LENGTH_SHORT).show();
						            	}
										Log.d("Error: ", response.toString());
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				            	
				                

				            }
				        }, new Response.ErrorListener() {

				            @Override
				            public void onErrorResponse(VolleyError response) {
				            	
				                Log.d("Error Response: ", response.toString());
				            }
				        });
				queue.add(jsObjRequest);
				        
			}
		});

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
        if (id == R.id.viewAllPrices) {

			Intent viewAllActivity = new Intent(getBaseContext(),
					ViewAll.class);
			
			Bundle extr = new Bundle(); 
			  extr.putString("username",  userName); 
			  extr.putString("market", userMarket);

			  viewAllActivity.putExtras(extr);

			startActivity(viewAllActivity);
			
			return true;
		}else if (id == R.id.addProduct) {

			Intent addProductActivity = new Intent(getBaseContext(),
					AddProductActivity.class);
			
			Bundle extras = new Bundle(); 
			  extras.putString("username",  userName); 
			  extras.putString("market", userMarket);

			  addProductActivity.putExtras(extras);
			
			startActivity(addProductActivity);
			return true;
		}
		return super.onOptionsItemSelected(item);

	}
	
	public void onDestroy()
	{
		super.onDestroy();
		d.close();
	}

	public void getCommodities() {
		int y = 0;
		//---get all items ---
		
		Cursor c = d.getAllItems("Commodity");
		String user, mkt = "";
		
		if (c.moveToFirst())
		{
			do {
				user = c.getString(1);
				mkt = c.getString(2);

				if(userName.contentEquals(user) && userMarket.contentEquals(mkt)) {
					vendornames.add(y, user);
					markets.add(y, mkt);
					commodities.add(y, c.getString(3));
					prices.add(y, c.getString(4));
					units.add(y, c.getString(5));
					
					y++;
				}

				
			} while (c.moveToNext());
		}
		//d.close();
	}
	
	public void deleteAllCommodities() {
		//---get all items ---
		Boolean status;
		//d.open();
		Cursor c = d.getAllItems("Commodity");
		if (c.moveToFirst())
		{
			do {

				if(userName.contentEquals(c.getString(1)) && userMarket.contentEquals(c.getString(2))) {
					status = d.deleteItem("Commodity", Long.parseLong(c.getString(0)));
					Log.d("status: ", "deleted "+status);
				}
				
			} while (c.moveToNext());
		}
		//d.close();
		
	}
	public void onListItemClick(ListView parent, View v, int position, long id) {

	}
	
}
