package com.blogspot.httpgplaysupport.creditcardgeneratorpro;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Random;


public class MainFragment extends Fragment implements View.OnClickListener {

    int cardIIN;
    int cardUNLength;

    static String finalCardNumString;

    static final String CVV = "0123456789";
    static SecureRandom cvvRnd = new SecureRandom();

    Spinner cardSelect;
    ArrayAdapter<CharSequence> adapter;



    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myFragmentView = inflater.inflate(R.layout.fragment_main, container, false);

        Button switchFragButton = myFragmentView.findViewById(R.id.checkFragmentButton);
        switchFragButton.setOnClickListener(this);

        ImageButton genButton = myFragmentView.findViewById(R.id.button);
        genButton.setOnClickListener(this);

        //sets up adapter to read array list used to populate spinner widget.
        cardSelect = (Spinner) myFragmentView.findViewById(R.id.cardSelect);
        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.card_arrays, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardSelect.setAdapter(adapter);


        //can use getItemIdAtPosition or getItemAtPosition to identify which item in array is selected
        cardSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), parent.getItemAtPosition(position) + " selected", Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return myFragmentView;
    }


    @Override
    //functions for when button with specific id is clicked
    public void onClick(View v){
        //switches to CheckFragment layout file
        switch (v.getId()) {
            case R.id.checkFragmentButton:
                Log.d("Credit Card Number: ", "Value: ");
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.mainLayout, new CheckFragment());
                ft.addToBackStack(null);
                ft.commit();
                break;

        }
        //generate button
        switch (v.getId()){
            case R.id.button:

                TextView displayView = getView().findViewById(R.id.cardNumberView);
                TextView cvvViewText = getView().findViewById(R.id.cvvView);
                TextView yearView = getView().findViewById(R.id.yearView);

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

                cvvViewText.setText(cvvString(3).toString());
                yearView.setText(monthString + "/" + myString);


                break;

        }
    }

    //creates random string for credit card number
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

    //uses Luhn algorithm to generate last checksum digit in card number
    public static String calculateCheckDigit(String card) {
        if (card == null)
            return null;
        String digit;
        /* convert generated card string to array int */
        int[] digits = new int[card.length()];
        for (int i = 0; i < card.length(); i++) {
            digits[i] = Character.getNumericValue(card.charAt(i));
        }
        for (int i = digits.length - 1; i >= 0; i -= 2)    {
            digits[i] += digits[i];
            if (digits[i] >= 10) {
                digits[i] = digits[i] - 9;
            }
        }
        int sum = 0;
        for (int i = 0; i < digits.length; i++) {
            sum += digits[i];
        }
        sum = sum * 9;
        digit = sum + "";
        return digit.substring(digit.length() - 1);
    }


}
