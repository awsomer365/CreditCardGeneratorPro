package com.blogspot.httpgplaysupport.creditcardgeneratorpro;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.blogspot.httpgplaysupport.creditcardgeneratorpro.MainActivity.calculateCheckDigit;

public class CheckActivity extends AppCompatActivity {


    private CheckBox modCheckBx, IINCheckBx, UNCheckBx;


    private EditText cardInput;
    private TextView iinText;
    private TextView unText;
    private TextView luhnText;

    private String cardIssuer;
    private int cardUnStringVal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check);


        modCheckBx = findViewById(R.id.modCheckBx);
        IINCheckBx = findViewById(R.id.IINCheckBx);

        cardInput = findViewById(R.id.cardInput);
        cardInput.setText(MainActivity.finalCardNumString);
        iinText = findViewById(R.id.iinText);
        unText = findViewById(R.id.unText);
        luhnText = findViewById(R.id.luhnText);

        modCheckBx.setEnabled(false);
        IINCheckBx.setEnabled(false);

        modCheckBx.setChecked(false);
        IINCheckBx.setChecked(false);

    }


    public void buttonOnClick(View v) {
        // do something when the button is clicked
        String inputNum = cardInput.getText().toString();
        //Checks if user input is exactly 16 digits. No more or less
        if (cardInput.getText().toString().trim().length() < 16 || cardInput.getText().toString().trim().length() > 16) {
            Toast.makeText(getApplicationContext(), "Please Input a 16 Digit Card Number", Toast.LENGTH_SHORT).show();
            modCheckBx.setChecked(false);
            luhnText.setText(R.string.invalid_code);
            luhnText.setTextColor((this.getResources().getColor(R.color.text_red)));
            IINCheckBx.setChecked(false);
            iinText.setTextColor(this.getResources().getColor(R.color.text_red));
            cardIssuer = "Invalid Number";
            unText.setTextColor((this.getResources().getColor(R.color.text_red)));
            unText.setText(R.string.invalid_code);
        } else {
            if (luhnCheck(inputNum)) {
                modCheckBx.setChecked(true);
                luhnText.setTextColor((this.getResources().getColor(R.color.text_green)));
                luhnText.setText(inputNum.substring(15));
            } else {
                modCheckBx.setChecked(false);
                luhnText.setTextColor((this.getResources().getColor(R.color.text_red)));
                luhnText.setText(R.string.invalid_code);
            }

            checkIIN(inputNum);
            checkUn(inputNum);
        }
        iinText.setText(cardIssuer);
    }

    //LuhnCheck algorithm to see if last digit correlates with rest of card number
    public static boolean luhnCheck(String card) {
        if (card == null)
            return false;
        char checkDigit = card.charAt(card.length() - 1);
        String digit = calculateCheckDigit(card.substring(0, card.length() - 1));
        return checkDigit == digit.charAt(0);
    }

    //CheckIIn identifies if user input number has a recognized IIN
    public void checkIIN(String card) {
        int i;
        int x;
        boolean foundIIN = false;
        Resources res = getResources();
        String[] issuerArray = res.getStringArray(R.array.card_arrays);
        x = 4;
        if (card.indexOf(String.valueOf(x)) == 0) {
            cardIssuer = issuerArray[0];
            foundIIN = true;
            cardUnStringVal = String.valueOf(x).length();
        }
        for (x = 51; x <= 55; x++) {
            if (card.indexOf(String.valueOf(x)) == 0) {
                cardIssuer = issuerArray[1];
                foundIIN = true;
                cardUnStringVal = String.valueOf(x).length();
            }
        }
        x = 34;
        for (i = 0; i <= 1; i++) {
            if (card.indexOf(String.valueOf(x)) == 0) {
                cardIssuer = issuerArray[2];
                foundIIN = true;
                cardUnStringVal = String.valueOf(x).length();
            }
            x = 37;
        }
        x = 6011;
        for (i = 0; i <= 2; i++) {
            if (i == 2) {
                x = 65;
            }
            if (card.indexOf(String.valueOf(x)) == 0) {
                cardIssuer = issuerArray[3];
                foundIIN = true;
                cardUnStringVal = String.valueOf(x).length();
            }
            x = 64;
        }
        x = 3095;
        for (i = 0; i <= 2; i++) {
            if (card.indexOf(String.valueOf(x)) == 0) {
                cardIssuer = issuerArray[4];
                foundIIN = true;
                cardUnStringVal = String.valueOf(x).length();
            } else if (i == 1) {
                for (x = 300; x <= 305; x++) {
                    if (card.indexOf(String.valueOf(x)) == 0) {
                        cardIssuer = issuerArray[4];
                        foundIIN = true;
                        cardUnStringVal = String.valueOf(x).length();
                    }
                }
            } else if (i == 2) {
                for (x = 38; x <= 39; x++) {
                    if (card.indexOf(String.valueOf(x)) == 0) {
                        cardIssuer = issuerArray[4];
                        foundIIN = true;
                        cardUnStringVal = String.valueOf(x).length();
                    }
                }
            }
        }
        x = 62;
        if (card.indexOf(String.valueOf(x)) == 0) {
            cardIssuer = issuerArray[5];
            foundIIN = true;
            cardUnStringVal = String.valueOf(x).length();
        }
        x = 60;
        for (i = 0; i <= 1; i++) {
            if (card.indexOf(String.valueOf(x)) == 0 && card.indexOf(String.valueOf(x + 5951)) != 0) {
                cardIssuer = issuerArray[6];
                foundIIN = true;
                cardUnStringVal = String.valueOf(x).length();
            }
            x = 6521;
        }
        for (x = 3528; x <= 3589; x++) {
            if (card.indexOf(String.valueOf(x)) == 0) {
                cardIssuer = issuerArray[7];
                foundIIN = true;
                cardUnStringVal = String.valueOf(x).length();
            }
        }
        if (!foundIIN) {
            cardUnStringVal = 0;
            cardIssuer = "Not Recognized";
            iinText.setTextColor(this.getResources().getColor(R.color.text_red));
            IINCheckBx.setChecked(false);
        } else {
            iinText.setTextColor(this.getResources().getColor(R.color.text_green));
            IINCheckBx.setChecked(true);
        }
    }

    public void checkUn(String card) {
        unText.setTextColor(this.getResources().getColor(R.color.text_green));
        String personalAcctNum = card.substring(cardUnStringVal, card.length() - 1);
        unText.setText(personalAcctNum);
    }

}
