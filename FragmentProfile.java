package com.reyesc.whatdo;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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
    View view;
    ArrayList<String> interests;
    ArrayList<Boolean> checkBoxes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        Bundle args = getArguments();
        mAccessToken = (AccessToken)args.get(ACCESS_TOKEN);

        mLogoutButton = view.findViewById(R.id.logout_button);
        log = view.findViewById(R.id.name);

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logOut();
                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        try {
            setLog(mAccessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            initInterests();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initRecyclerView();

        return view;
    }

    public void setLog(AccessToken mAccessToken) throws JSONException {
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
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(interests, checkBoxes, view.getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
    }

    public void initInterests() throws JSONException {
        //requestHttp.getRequest(this.getContext());
        //requestHttp.postRequest(this.getContext());
        //requestHttp.putRequest(this.getContext());
        interests = new ArrayList();
        checkBoxes = new ArrayList<>();
        interests.add("Hiking");
        checkBoxes.add(true);
        interests.add("Jamming");
        checkBoxes.add(false);
        interests.add("Golfing");
        checkBoxes.add(true);
        interests.add("Speaking");
        checkBoxes.add(true);
        interests.add("Basketball");
        checkBoxes.add(true);
        interests.add("Tennis");
        checkBoxes.add(false);
        interests.add("Baseball");
        checkBoxes.add(false);


        //requestHttp.postRequest(view.getContext());
        //requestHttp.getRequest(view.getContext());
        String[] interests = {"Biking", "Hopping", "Sky_Diving"};
        //requestHttp.putObjectRequest(view.getContext(), "69" , addInterest(interests));
        //requestHttp.putStringRequest(view.getContext());
        addInterest(interests);

    }

    public void addInterest(String[] interests) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(String i : interests){
            jsonArray.put(i);
        }
        RequestHttp requestHttp = RequestHttp.getRequestHttp();
        requestHttp.putStringRequest(view.getContext(), mAccessToken.getUserId(), jsonArray);
    }
}
