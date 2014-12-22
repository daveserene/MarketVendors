package com.shop.it;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shop.Sync.CustomRequest;

public class LoginActivity extends ActionBarActivity {

	RequestQueue queues;
	EditText username;
	EditText password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		queues = Volley.newRequestQueue(this);
		
		TextView registerScreen = (TextView) findViewById(R.id.register);
		// Listening to register new account link
		registerScreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Switching to Register screen
				Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
				startActivity(i);
			}
		});
		
		Button btnStart = (Button) findViewById(R.id.doLoginButton);
		btnStart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String url = "http://10.0.2.2/shopIt/login.php";
				Map<String, String> params = new HashMap<String, String>();
				
				username = (EditText) findViewById(R.id.editUsername);
				password = (EditText) findViewById(R.id.editPassword);
				
				if (username.getText().toString().matches("")) {
				    Toast.makeText(getBaseContext(), "Username field cannot be empty", Toast.LENGTH_SHORT).show();
				    return;
				}else if(password.getText().toString().matches("")){
					Toast.makeText(getBaseContext(), "Password field cannot be empty", Toast.LENGTH_SHORT).show();
				    return;
				}else {
					params.put("username", username.getText().toString().trim());
					params.put("password", password.getText().toString().trim());
				

				CustomRequest jsObjRequest = new CustomRequest(Method.POST, url, params, new Response.Listener<JSONObject>() {

				            @Override
				            public void onResponse(JSONObject response) {
				            	try {
				            		
									if(response.get("status").toString().equalsIgnoreCase("success")){
										Log.d("Success: ", response.toString());
										Intent i = new Intent(getApplicationContext(), AddProductActivity.class);
										
										Bundle extras = new Bundle(); 
										  extras.putString("username",  username.getText().toString().trim()); 
										  extras.putString("market", response.get("market").toString());

										  i.putExtras(extras); 

										startActivity(i);
									}else {
										Toast.makeText(getApplicationContext(), "Wrong Username or Password",
												Toast.LENGTH_LONG).show();
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
				        		Toast.makeText(getBaseContext(), "Log In Error "+response.toString(), Toast.LENGTH_LONG).show();
				            	Log.d("Response: ", response.toString());
				            }
				        });
				queues.add(jsObjRequest);
	
					
				}
				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
}
