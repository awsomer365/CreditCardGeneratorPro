package com.blogspot.httpgplaysupport.creditcardgeneratorpro;

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

import static java.lang.Math.exp;

public class MainActivity extends AppCompatActivity {

    private AdView mAdView;

    int cardIIN;
    int cardUNLength;

    static String finalCardNumString;

    static final String CVV = "0123456789";
    static SecureRandom cvvRnd = new SecureRandom();

    Spinner cardSelect;
    ArrayAdapter<CharSequence> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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

    public void changeActivity(View view) {
        Intent intent = new Intent(this, CheckActivity.class);
       // intent.putExtra(finalCardNumString);
        startActivity(intent);

    }


    public void buttonOnClick(View v) {
        // do something when the button is clicked

        TextView displayView = findViewById(R.id.cardNumberView);
        TextView cvvViewText = findViewById(R.id.cvvView);
        TextView yearView = findViewById(R.id.yearView);
        // TextView monthView = (TextView) findViewById(R.id.monthView);

        Random yearRnd = new Random();
        int number = yearRnd.nextInt(16) + Calendar.getInstance().get(Calendar.YEAR);;
        String myString = String.valueOf(number);

        Random monthRnd = new Random();
        int monthNumber = yearRnd.nextInt(12) + 1;
        String monthString = String.valueOf(monthNumber);

        cardLength();

        String cardPreCheckNum = String.valueOf(cardIIN) + String.valueOf(cvvString(cardUNLength));

        calculateCheckDigit(cardPreCheckNum);

        String cardNumberString = cardPreCheckNum + calculateCheckDigit(cardPreCheckNum);
        finalCardNumString = cardNumberString;


        displayView.setText(finalCardNumString);

        Log.d("Credit Card Number: ", "Value: " + cardNumberString);


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

    public static String calculateCheckDigit(String card) {
        if (card == null)
            return null;
        String digit;
        /* convert to array of int for simplicity */
        int[] digits = new int[card.length()];
        for (int i = 0; i < card.length(); i++) {
            digits[i] = Character.getNumericValue(card.charAt(i));
        }

        /* double every other starting from right - jumping from 2 in 2 */
        for (int i = digits.length - 1; i >= 0; i -= 2)	{
            digits[i] += digits[i];

            /* taking the sum of digits grater than 10 - simple trick by substract 9 */
            if (digits[i] >= 10) {
                digits[i] = digits[i] - 9;
            }
        }
        int sum = 0;
        for (int i = 0; i < digits.length; i++) {
            sum += digits[i];
        }
        /* multiply by 9 step */
        sum = sum * 9;

        /* convert to string to be easier to take the last digit */
        digit = sum + "";
        return digit.substring(digit.length() - 1);
    }

}
