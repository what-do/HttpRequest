package com.reyesc.whatdo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import static com.android.volley.VolleyLog.TAG;

public class MainActivity extends AppCompatActivity implements FragmentExtension.FragmentToActivityListener {
    private ArrayList<Fragment> fragmentStack;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_profile:
                    replaceFragment(R.id.fragment_profile);
                    return true;
                case R.id.navigation_discover:
                    replaceFragment(R.id.fragment_discover);
                    return true;
                case R.id.navigation_saved:
                    replaceFragment(R.id.fragment_saved);
                    return true;
                case R.id.navigation_friends:
                    replaceFragment(R.id.fragment_friends);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentStack = new ArrayList<>();
        setContentView(R.layout.activity_main);

        RequestHttp requestHttp = new RequestHttp();


        requestHttp.getRequest(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.ic_whatdotitle);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        createFragments();

        BottomNavigationView navigation = findViewById(R.id.navigationView);
        navigation.setSelectedItemId(R.id.navigation_discover);
        NavBarHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private void createFragments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new FragmentProfile();
        transaction.add(R.id.container, fragment, String.valueOf(R.id.fragment_profile));
        transaction.commit();

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.detach(fragment);
        transaction.commit();

        transaction = getSupportFragmentManager().beginTransaction();
        fragment = new FragmentSaved();
        transaction.add(R.id.container, fragment, String.valueOf(R.id.fragment_saved));
        transaction.commit();

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.detach(fragment);
        transaction.commit();

        transaction = getSupportFragmentManager().beginTransaction();
        fragment = new FragmentFriends();
        transaction.add(R.id.container, fragment, String.valueOf(R.id.fragment_friends));
        transaction.commit();

        transaction = getSupportFragmentManager().beginTransaction();
        transaction.detach(fragment);
        transaction.commit();

        transaction = getSupportFragmentManager().beginTransaction();
        fragment = new FragmentDiscover();
        transaction.add(R.id.container, fragment, String.valueOf(R.id.fragment_discover));
        fragmentStack.add(fragment);
        transaction.commit();
    }

    private void replaceFragment(int fragmentTag) {
        Fragment currentFragment = fragmentStack.get(fragmentStack.size() - 1);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(String.valueOf(fragmentTag));

        if (fragment.equals(currentFragment)) return;

        for (Fragment frag : fragmentStack) {
            if (fragmentTag == Integer.parseInt(frag.getTag())) {
                fragmentStack.remove(fragment);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.detach(currentFragment);
                transaction.attach(fragment);
                fragmentStack.add(fragment);
                transaction.commit();
                return;
            }
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.detach(currentFragment);
        transaction.attach(fragment);
        fragmentStack.add(fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentStack.size() > 1) {
            Fragment currentFragment = fragmentStack.get(fragmentStack.size() - 1);
            Fragment previousFragment = fragmentStack.get(fragmentStack.size() - 2);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.detach(currentFragment);
            transaction.attach(previousFragment);
            fragmentStack.remove(currentFragment);
            transaction.commit();

            BottomNavigationView view = this.findViewById(R.id.navigationView);
            switch (Integer.parseInt(previousFragment.getTag())) {
                case R.id.fragment_profile:
                    view.setSelectedItemId(R.id.navigation_profile);
                    break;
                case R.id.fragment_discover:
                    view.setSelectedItemId(R.id.navigation_discover);
                    break;
                case R.id.fragment_saved:
                    view.setSelectedItemId(R.id.navigation_saved);
                    break;
                case R.id.fragment_friends:
                    view.setSelectedItemId(R.id.navigation_friends);
                    break;
            }
        } else {
            this.moveTaskToBack(true);
            //this.finish();
        }
    }

    public void fromFeedToCollection(ActivityCard card) {
        FragmentDiscover fragmentDiscover = (FragmentDiscover) getSupportFragmentManager().findFragmentByTag(String.valueOf(R.id.fragment_discover));
        fragmentDiscover.removeFromFeed(card);
        FragmentSaved fragmentSaved = (FragmentSaved) getSupportFragmentManager().findFragmentByTag(String.valueOf(R.id.fragment_saved));
        fragmentSaved.addToCollection(card);
    }

    public void fromCollectionToFeed(ActivityCard card) {
        FragmentSaved fragmentSaved = (FragmentSaved) getSupportFragmentManager().findFragmentByTag(String.valueOf(R.id.fragment_saved));
        fragmentSaved.removeFromCollection(card);
        FragmentDiscover fragmentDiscover = (FragmentDiscover) getSupportFragmentManager().findFragmentByTag(String.valueOf(R.id.fragment_discover));
        fragmentDiscover.addToFeed(card);
    }

    public void toasting(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
    }

}

