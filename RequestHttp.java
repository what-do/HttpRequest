package com.reyesc.whatdo;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Michael on 4/16/18.
 */

public class RequestHttp {

    private RequestQueue mRequestQueue;

    private StringRequest mStringRequest;

    private JsonArrayRequest mJsonArrayRequest;


    private String userUrl = "http://civil-ivy-200504.appspot.com/users";

    private RequestHttp(){}

    private static RequestHttp requestHttp;

    public static RequestHttp getRequestHttp(){
        if (requestHttp == null){
            requestHttp = new RequestHttp();
        }
        return requestHttp;
    }


    public void getRequest(Context context){

        mRequestQueue = Volley.newRequestQueue(context);


        mStringRequest = new StringRequest(Request.Method.GET, userUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i(TAG, "Response: "  + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG, "Error: " + error.toString());

            }
        });

        mJsonArrayRequest = new JsonArrayRequest(Request.Method.GET, userUrl, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.i(TAG, "Response: " + response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error: " + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
        mRequestQueue.add(mJsonArrayRequest);

    }
    public void putStringRequest(Context context, String id, String task, final JSONArray jsonArray) {

        mRequestQueue = Volley.newRequestQueue(context);
        id = "123";
        String requestUrl = userUrl + "/" + task + "/" + id;
        Log.i(TAG, "sending to" + requestUrl);
        mStringRequest = new StringRequest(Request.Method.PUT, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i(TAG, "Response: " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG, "Put Error: " + error.toString());

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                //or try with this:
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("interests", jsonArray.toString());
                Log.i(TAG, "Did it send this?: " + params.toString());
                return params;
            }
        };

        mRequestQueue.add(mStringRequest);

    }
    /*public void putObjectRequest(Context context, String id, final JSONObject obj) {

        mRequestQueue = Volley.newRequestQueue(context);
        String userPutUrl = userUrl + id;
        Log.i(TAG, "sending to " + userPutUrl);


        JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, userPutUrl, obj, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, " Put Response: " + response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JSONObject jsonObject = obj;
                        Log.i(TAG, "Put Array error: " + error + "Unable to send " + jsonObject.toString());

                    }
                });



            mRequestQueue.add(mJsonObjectRequest);


    }*/

    public void postRequest(Context context, final String id, final String userName) {

        mRequestQueue = Volley.newRequestQueue(context);


        mStringRequest = new StringRequest(Request.Method.POST, userUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i(TAG, "Response: " + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i(TAG, "Error: " + error.toString());

            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                //or try with this:
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", userName);
                params.put("id", id);
                Log.i(TAG, params.toString());
                return params;
            }
        };

        mRequestQueue.add(mStringRequest);

    }


}
