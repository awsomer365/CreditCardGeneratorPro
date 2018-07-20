package com.blogspot.httpgplaysupport.creditcardgeneratorpro;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Random;

import static java.lang.Math.exp;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;
    private TextView mTextMessage;

    double cardIIN;
    double cardUNLength;

    static final String CVV = "0123456789";
    static SecureRandom cvvRnd = new SecureRandom();

    Spinner cardSelect;
    ArrayAdapter<CharSequence> adapter;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        Button genButton = (Button) findViewById(R.id.button2);


        //sets up adapter to read array list used to populate spinner widget.
        cardSelect = (Spinner) findViewById(R.id.cardSelect);
        adapter = ArrayAdapter.createFromResource(this, R.array.card_arrays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardSelect.setAdapter(adapter);

        //can use getItemIdAtPosition or getItemAtPosition to identify which item in array is selected
        //
        cardSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getBaseContext(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    public void buttonOnClick(View v) {
        // do something when the button is clicked
        Button button = (Button) v;

        TextView displayView = (TextView) findViewById(R.id.cardNumberView);
        TextView cvvViewText = (TextView) findViewById(R.id.cvvView);
        TextView yearView = (TextView) findViewById(R.id.yearView);
        // TextView monthView = (TextView) findViewById(R.id.monthView);

        Random yearRnd = new Random();
        int number = yearRnd.nextInt(16) + 2018;
        String myString = String.valueOf(number);

        Random monthRnd = new Random();
        int monthNumber = yearRnd.nextInt(12) + 1;
        String monthString = String.valueOf(monthNumber);

        cardLength();
        double cardPreCheckNum = cardIIN * Math.pow(cardUNLength, 10);
        //cardPreCheckNum = cardPreCheckNum + cvvString(cardUNLength);
        //cardIIN = cardIIN * Math.pow(cardUNLength, 10);
        cardCheckSum();


        //displayView.setText(String.valueOf(cardIIN) + String.valueOf(cvvString(cardUNLength)));
        //displayView.setText(String.valueOf(preCheckNumber));


        //displayView.setText(cvvString(4).toString() + "-" + cvvString(4).toString()+ "-" + cvvString(4).toString()+ "-" + cvvString(4).toString());
        cvvViewText.setText(cvvString(3).toString());
        yearView.setText(monthString + "/" + myString);
        //monthView.setText(monthString);
    }


    String cvvString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(CVV.charAt(cvvRnd.nextInt(CVV.length())));
        return sb.toString();

    }

    //Uses the itemID from the arrayList and then randomly sets the prefix possibilities for each card company. Also sets the User idetification number length. (16-cardIIN)
    public void cardLength() {
        if (cardSelect.getSelectedItemId() == 0) {
            cardIIN = 4;
            cardUNLength = 14;
        } else if (cardSelect.getSelectedItemId() == 1) {
            cardIIN = new Random().nextInt(56 - 51) + 51;
            cardUNLength = 13;
        } else if (cardSelect.getSelectedItemId() == 2) {
            int twoValue = new Random().nextInt(3 - 1) + 1;
            if (twoValue == 1) {
                cardIIN = 34;
            } else {
                cardIIN = 37;
            }
            cardUNLength = 13;
        } else if (cardSelect.getSelectedItemId() == 3) {
            int threeValue = new Random().nextInt(4 - 1) + 1;
            if (threeValue == 1) {
                cardIIN = 6011;
                cardUNLength = 11;
            } else if (threeValue == 2) {
                cardIIN = 64;
                cardUNLength = 13;
            } else if (threeValue == 3) {
                cardIIN = 65;
                cardUNLength = 13;
            }
        } else if (cardSelect.getSelectedItemId() == 4) {
            int threeValue = new Random().nextInt(4 - 1) + 1;
            if (threeValue == 1) {
                cardIIN = new Random().nextInt(306 - 300) + 300;
                cardUNLength = 12;
            } else if (threeValue == 2) {
                cardIIN = 3095;
                cardUNLength = 11;
            } else if (threeValue == 3) {
                cardIIN = new Random().nextInt(40 - 38) + 38;
                ;
                cardUNLength = 13;
            }
        } else if (cardSelect.getSelectedItemId() == 5) {
            cardIIN = 62;
            cardUNLength = 13;
        } else if (cardSelect.getSelectedItemId() == 6) {
            int twoValue = new Random().nextInt(3 - 1) + 1;
            if (twoValue == 1) {
                cardIIN = 60;
                cardUNLength = 13;
            } else {
                cardIIN = 6521;
                cardUNLength = 11;
            }
        }
        else if (cardSelect.getSelectedItemId() == 7) {
            cardIIN = new Random().nextInt(3590 - 3528) + 3528;
            cardUNLength = 11;
        }
    }

    int cardCheckSum(){
    return 0;
    }
}
