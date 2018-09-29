package com.blogspot.httpgplaysupport.creditcardgeneratorpro;


import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import static com.blogspot.httpgplaysupport.creditcardgeneratorpro.MainFragment.calculateCheckDigit;

public class CheckFragment extends Fragment implements View.OnClickListener, RewardedVideoAdListener {


    public CheckFragment() {
        // Required empty public constructor
    }

    private CheckBox modCheckBx, IINCheckBx, UNCheckBx;

    private RewardedVideoAd mRewardedVideoAd;

    private EditText cardInput;
    private TextView iinText;
    private TextView unText;
    private TextView luhnText;
    private TextView checkText;


    private String cardIssuer;
    private int cardUnStringVal;

    private int checkInt;
    private int rewardState = 0;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myFragmentView = inflater.inflate(R.layout.fragment_check, container, false);

        modCheckBx = myFragmentView.findViewById(R.id.modCheckBx);
        IINCheckBx = myFragmentView.findViewById(R.id.IINCheckBx);

        cardInput = myFragmentView.findViewById(R.id.cardInput);
        cardInput.setText(MainFragment.finalCardNumString);
        iinText = myFragmentView.findViewById(R.id.iinText);
        unText = myFragmentView.findViewById(R.id.unText);
        luhnText = myFragmentView.findViewById(R.id.luhnText);
        checkText = myFragmentView.findViewById(R.id.checkRemainTxt);

        modCheckBx.setEnabled(false);
        IINCheckBx.setEnabled(false);

        modCheckBx.setChecked(false);
        IINCheckBx.setChecked(false);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        Button checkButton = myFragmentView.findViewById(R.id.checkButton);
        checkButton.setOnClickListener(this);

        Button switchFragButton = myFragmentView.findViewById(R.id.switchGenButton);
        switchFragButton.setOnClickListener(this);

        Button unlockButton = myFragmentView.findViewById(R.id.unlockBtn);
        unlockButton.setOnClickListener(this);

        getUnlockKeyValue();

        checkText.setText("Checks: " + checkInt);

        if (checkInt <= 0){
            checkInt = 0;
        }

        return myFragmentView;
    }
//retrieves saved value called "unlocks_key"
    private void getUnlockKeyValue() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("unlocks_key", Context.MODE_PRIVATE);

        int defaultValue = getResources().getInteger(R.integer.unlockIntValue);
        checkInt = sharedPreferences.getInt(getString(R.string.unlocks_key), defaultValue);

        if (checkInt <= 0) {
            checkInt = 0;
        }
    }

    //saves "unlock_key"
    private void setUnlockKeyValue() {
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("unlocks_key", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(getString(R.string.unlocks_key), checkInt);

        editor.commit();
    }

    @Override
    //Button onclick functions
    public void onClick(View v){

        switch (v.getId()) {
            case R.id.unlockBtn:
                //check if ad has loaded and ready to display
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                    rewardState = 0;
                }
                else if (rewardState == 3){
                    Toast.makeText(getActivity(),
                            "No Ads At This Time, Try Again Later",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(),
                            "Failed To Load Ad, Try Again Later. Error Code " + rewardState,
                            Toast.LENGTH_SHORT).show();
                }

                break;

        }

        //switches layout to MainFragment.java
        switch (v.getId()) {
            case R.id.switchGenButton:
                Log.d("Credit Card Number: ", "Value: ");
                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.mainLayout, new MainFragment());
                ft.commit();
                break;

        }
        //Check button that checks user input against luhn algorithm and changes text to red or green to say if valid
        switch (v.getId()){
            case R.id.checkButton:

                String inputNum = cardInput.getText().toString();

                //Checks if user input is exactly 16 digits. No more or less
                if (cardInput.getText().toString().trim().length() < 16 || cardInput.getText().toString().trim().length() > 16) {
                    Toast.makeText(getActivity(), "Please Input a 16 Digit Card Number", Toast.LENGTH_SHORT).show();
                    invalidError();
                } else {
                    if (checkInt > 0) {
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

                        checkInt -= 1;
                        checkText.setText("Checks: " + checkInt);
                    } else{
                        checkInt = 0;
                        invalidError();
                        Toast.makeText(getActivity(),
                                "No Checks Remaining",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                iinText.setText(cardIssuer);

        }
    }

    //error messages if card is not 16 digits or if user has no mor unlocks
    public void invalidError(){
        modCheckBx.setChecked(false);
        luhnText.setText(R.string.invalid_code);
        luhnText.setTextColor((this.getResources().getColor(R.color.text_red)));
        IINCheckBx.setChecked(false);
        iinText.setTextColor(this.getResources().getColor(R.color.text_red));
        cardIssuer = "Invalid Number";
        unText.setTextColor((this.getResources().getColor(R.color.text_red)));
        unText.setText(R.string.invalid_code);
    }

    //Google ad function to load video ads
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-9282720184335688/6522830889",
                new AdRequest.Builder().build());
    }

    //LuhnCheck algorithm to see if last digit correlates with rest of card number
    public static boolean luhnCheck(String card) {
        if (card == null)
            return false;
        char checkDigit = card.charAt(card.length() - 1);
        String digit = calculateCheckDigit(card.substring(0, card.length() - 1));
        return checkDigit == digit.charAt(0);
    }

    //CheckIIn identifies if user input number has a recognized IIN.
    public void checkIIN(String card) {
        int i;
        int x;
        boolean foundIIN = false;
        //loads list of card INN numbers and sets to issuerArray
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

    //shows user the User number portion of card number
    public void checkUn(String card) {
        unText.setTextColor(this.getResources().getColor(R.color.text_green));
        String personalAcctNum = card.substring(cardUnStringVal, card.length() - 1);
        unText.setText(personalAcctNum);
    }


    //Google ad functions automatically loaded
    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

        loadRewardedVideoAd();

    }

    //Rewards user with unlocks after they watch an ad
    @Override
    public void onRewarded(RewardItem rewardItem) {
        checkInt = checkInt + rewardItem.getAmount();
        setUnlockKeyValue();
        checkText.setText("Checks: " + checkInt);


        Toast.makeText(getActivity(),
                "Checks Successfully Added",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        rewardState = i;
    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}
