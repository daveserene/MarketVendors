package com.shop.it;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.shop.Adapter.MyPricesAdapter;
import com.shop.Model.DBAdapter;
import com.shop.Sync.CustomRequest;

import com.shop.it.RowData;

public class ViewAll extends ActionBarActivity {
	RequestQueue queue;

	HashMap<String, Integer> listImages;

	private LayoutInflater mInflater;
	DBAdapter d;
	RowData rowData;
	MyPricesAdapter adapter;
	private Vector<RowData> myPricesData;

	ArrayList<String> vendornames = new ArrayList();
	ArrayList<String> markets = new ArrayList();
	ArrayList<String> commodities = new ArrayList();
	ArrayList<String> prices = new ArrayList();
	ArrayList<String> units = new ArrayList();

	String userName, userMarket = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_all);

		Bundle es = getIntent().getExtras();

		if (es != null) {
			userName = es.getString("username");
			userMarket = es.getString("market");

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

		//syncMyPrices = new SyncHelper(vendornames, markets, commodities, prices, units); 

		myPricesData = new Vector<RowData>();
		mInflater = (LayoutInflater) getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		populateListview();

		Button syncViewAllPrices = (Button) findViewById(R.id.downloadCommodities);
		syncViewAllPrices.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				String url = "http://10.0.2.2/shopIt/getAllCommodities.php";
				Map<String, String> params = new HashMap<String, String>();

				CustomRequest jsObjRequest = new CustomRequest(Method.GET, url, params, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {

						Log.d("Success: ", response.toString());

						//Clear local database before downloading latest updates
						deleteAllCommodities();

						String cty ="cty";
						String unit ="unit";
						String price ="price";
						String vname ="vname";
						String mkt ="mkt";
						long id;

						Iterator<String> iter = response.keys();
						while (iter.hasNext()) {
							String key = iter.next();
							try {
								JSONObject value = (JSONObject) response.get(key);
								//Log.d("values: ", value.get(cty).toString());

								id = d.insertItem("server",value.get(vname).toString(), value.get(mkt).toString(), value.get(cty).toString(), value.get(price).toString(), value.get(unit).toString());



							} catch (JSONException e) {
								// Something went wrong!

							}
						}

						populateListview();

					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError response) {
						Log.d("Response: ", response.toString());
					}
				});
				queue.add(jsObjRequest);
			}
		});


	}

	public void populateListview(){
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

		//new MyPricesAdapter(getBaseContext(), R.layout.activity_my_prices_list, R.id.commodityTitle, myPricesData, mInflater, listImages);
		adapter = new MyPricesAdapter(this, R.layout.activity_my_prices_list, R.id.commodityTitle, myPricesData, mInflater, listImages);

		ListView myPricesListView = (ListView)findViewById(R.id.viewAllListView);
		myPricesListView.setAdapter(adapter);
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

			Intent myprices = new Intent(getBaseContext(),
					MyPrices.class);

			Bundle ext = new Bundle(); 
			ext.putString("username",  userName); 
			ext.putString("market", userMarket);

			myprices.putExtras(ext);

			startActivity(myprices);

			return true;
		}else if (id == R.id.viewAllPrices) {

			return true;
		}else if (id == R.id.addProduct) {

			Intent addProductActivity = new Intent(getBaseContext(),
					AddProductActivity.class);

			Bundle t = new Bundle(); 
			t.putString("username",  userName); 
			t.putString("market", userMarket);

			addProductActivity.putExtras(t);

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
		//d.open();
		Cursor c = d.getAllItems("server");

		if (c.moveToFirst())
		{
			do {

				vendornames.add(y, c.getString(1));
				markets.add(y, c.getString(2));
				commodities.add(y, c.getString(3));
				prices.add(y, c.getString(4));
				units.add(y, c.getString(5));

				y++;



			} while (c.moveToNext());
		}
		//d.close();
	}

	public void deleteAllCommodities() {
		//---get all items ---
		Boolean status;
		//d.open();
		Cursor c = d.getAllItems("server");
		if (c.moveToFirst())
		{
			do {
				status = d.deleteItem("server", Long.parseLong(c.getString(0)));
				//Log.d("status: ", "deleted "+status+" "+c.getString(1));				

			} while (c.moveToNext());
		}
		//d.close();

	}

}
