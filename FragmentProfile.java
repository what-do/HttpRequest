package com.reyesc.whatdo;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.reyesc.whatdo.LoginActivity.ACCESS_TOKEN;

public class FragmentProfile extends FragmentExtension {
    private AccessToken mAccessToken;
    private Button mLogoutButton;
    private TextView log;
    private static final String TAG = "FragmentProfile";
    private Button mUpdateButton;
    View view;
    ArrayList<String> interests =  new ArrayList<>();
    ArrayList<String> possibleInterests =  new ArrayList<>();
    ArrayList<Boolean> checkBoxes = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        Bundle args = getArguments();
        mAccessToken = (AccessToken)args.get(ACCESS_TOKEN);

        mLogoutButton = view.findViewById(R.id.logout_button);
        log = view.findViewById(R.id.name);

        mUpdateButton = view.findViewById(R.id.updateButton);



        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);

            }
        });
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getInterests();
            }
        });

        setLog(mAccessToken);

        initInterests();
        initRecyclerView();

        return view;
    }

    public void setLog(AccessToken mAccessToken)  {
        if (mAccessToken != null) {
            initInterests();
            log.setText(mAccessToken.getUserId());
        } else {
            log.setText("you are logged out");
        }
    }

    public void initRecyclerView(){
        Log.d(TAG, "init Recycler View");
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(possibleInterests, checkBoxes, view.getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

   public void initInterests() {
        populatePossibleInterests();

        String[] interests = {"hiking", "amusementparks", "airsoft"};
       try {
           updateInterest(interests, "addinterests");
       } catch (JSONException e) {
           e.printStackTrace();
       }
       //createUser();

    }

    public void updateInterest(String[] interests, String method) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(String i : interests){
            jsonArray.put(i);
        }
        RequestHttp requestHttp = RequestHttp.getRequestHttp();
        requestHttp.putStringRequest(view.getContext(), mAccessToken.getUserId(), method, jsonArray);
    }

    public void getInterests(){
        RequestHttp requestHttp = RequestHttp.getRequestHttp();
        requestHttp.getRequest(view.getContext(), "users", "interests", "12345", new RequestHttp.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONArray response = new JSONArray(result);
                    Log.i(TAG, response.toString());
                    interests.clear();
                    for(int i = 0; i < response.length(); i++){
                        interests.add(i, (String)response.get(i));

                    }
                    System.out.println("final interests: " + interests.toString());
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void populatePossibleInterests(){
        RequestHttp requestHttp = RequestHttp.getRequestHttp();
        requestHttp.getRequest(view.getContext(), "tags", "", "", new RequestHttp.VolleyCallback() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONArray response = new JSONArray(result);
                    Log.i(TAG, response.toString());
                    for(int i = 0; i < response.length(); i++){
                        if(possibleInterests.size() > i) {
                            possibleInterests.set(i, ((JSONObject) response.get(i)).getString("alias"));
                        }
                        else {
                            possibleInterests.add(((JSONObject) response.get(i)).getString("alias"));
                        }
                        if(interests.contains(possibleInterests.get(i))) {
                            if (checkBoxes.size() > i) {
                                checkBoxes.set(i, true);
                            }
                            else {
                                checkBoxes.add(i, true);
                            }
                        }
                        else{
                            if (checkBoxes.size() > i) {
                                checkBoxes.set(i, false);
                            }
                            else {
                                checkBoxes.add(i, false);
                            }
                        }

                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    public void createUser() {
        RequestHttp requestHttp = RequestHttp.getRequestHttp();
        requestHttp.postRequest(view.getContext(), mAccessToken.getUserId(), "Leigh");
    }
}
