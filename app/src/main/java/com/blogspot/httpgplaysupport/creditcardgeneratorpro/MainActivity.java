package com.blogspot.httpgplaysupport.creditcardgeneratorpro;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaDrm;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Random;

import static com.blogspot.httpgplaysupport.creditcardgeneratorpro.MainFragment.finalCardNumString;
import static java.lang.Math.exp;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;

    Fragment mainFragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mainFragment = new MainFragment();
        FragmentManager manager = getFragmentManager();
        manager.beginTransaction()
                .replace(R.id.mainLayout, mainFragment, mainFragment.getTag())
                .commit();



    }

}
