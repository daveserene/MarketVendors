package com.shop.Sync;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;

import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

	public class CustomRequest extends Request<JSONObject>{

	      private Listener<JSONObject> listener;
	      private Map<String, String> params;

	      public CustomRequest(String url, Map<String, String> params,
	                Listener<JSONObject> reponseListener, ErrorListener errorListener) {
	            super(Method.GET, url, errorListener);
	            this.listener = reponseListener;
	            this.params = params;
	      }

	      public CustomRequest(int method, String url, Map<String, String> params,
	                Listener<JSONObject> reponseListener, ErrorListener errorListener) {
	            super(method, url, errorListener);
	            this.listener = reponseListener;
	            this.params = params;
	        }

	    @Override
	    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
	      return params;
	    };

	    @Override
	    protected void deliverResponse(JSONObject response) {
	        listener.onResponse(response);
	    }

	    @Override
	    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
	         try {
	                String jsonString = new String(response.data,
	                        HttpHeaderParser.parseCharset(response.headers));
	                return Response.success(new JSONObject(jsonString),
	                        HttpHeaderParser.parseCacheHeaders(response));
	            } catch (UnsupportedEncodingException e) {
	                return Response.error(new ParseError(e));
	            } catch (JSONException je) {
	                return Response.error(new ParseError(je));
	            }
	    }

	}
	
