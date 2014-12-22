package com.shop.it;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.Volley;
import com.shop.Sync.CustomRequest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity  extends Activity {
	RequestQueue queue;
	EditText username;
	EditText password;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set View to register.xml
        setContentView(R.layout.register);


        queue = Volley.newRequestQueue(this);
        
        TextView loginScreen = (TextView) findViewById(R.id.linkLogin);

        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {

			public void onClick(View arg0) {
                                // Closing registration screen
				// Switching to Login Screen/closing register screen
				Intent x = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(x);
			}
		});
        
		Button btnStart = (Button) findViewById(R.id.btnRegister);
		btnStart.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String url = "http://10.0.2.2/shopIt/register.php";
				Map<String, String> params = new HashMap<String, String>();
				
				username = (EditText) findViewById(R.id.regFullname);
				password = (EditText) findViewById(R.id.regPass);
				Spinner market = (Spinner) findViewById(R.id.regMarketList);
				
				if (username.getText().toString().matches("")) {
				    Toast.makeText(getBaseContext(), "Vendor Name field cannot be empty", Toast.LENGTH_LONG).show();
				    return;
				}else if(password.getText().toString().matches("")){
					Toast.makeText(getBaseContext(), "Password field cannot be empty", Toast.LENGTH_LONG).show();
				    return;
				}else {
					params.put("na", username.getText().toString().trim());
					params.put("pa", password.getText().toString().trim());
					params.put("ma", market.getSelectedItem().toString().trim());
				

					Log.v("user", username.getText().toString().trim());
					Log.v("pass", password.getText().toString().trim());
					Log.v("market", market.getSelectedItem().toString().trim());
					
				CustomRequest jsObjRequest = new CustomRequest(Method.POST, url, params, new Response.Listener<JSONObject>() {

				            @Override
				            public void onResponse(JSONObject response) {
				            	try {
									if(response.get("status").toString().equalsIgnoreCase("success")){
										Log.d("Success: ", response.toString());
										
										Toast.makeText(getApplicationContext(), "Registration Success",
												Toast.LENGTH_LONG).show();
										
										Intent i = new Intent(getApplicationContext(), LoginActivity.class);
										startActivity(i);
									}else {
										Toast.makeText(getApplicationContext(), "Select another username, "+username.getText().toString().trim()+" is already taken.",
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
				queue.add(jsObjRequest);
	
					
				}

			}
		});
    
	}

}
