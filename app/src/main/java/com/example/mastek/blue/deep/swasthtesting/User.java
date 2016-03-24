package com.example.mastek.blue.deep.swasthtesting;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by akash on 21/3/16.
 */
public class User {
    public static final String SP_NAME = "userDetails";
    SharedPreferences sharedPreferences;
    private int credits;
    private int card_number;
    private String name;

    public User(Context context) {
        sharedPreferences = context.getSharedPreferences(SP_NAME, 0);
    }

    public void addUserDetails(int credits, int card_number, String name) {
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putInt("credits", credits);
        spEditor.putInt("card_number", card_number);
        spEditor.putString("name", name);
        spEditor.apply();
    }

    public int getCredits() {
        credits = sharedPreferences.getInt("credits", 0);
        return credits;
    }

    public int getCardNumber() {
        card_number = sharedPreferences.getInt("card_number", 0);
        return card_number;
    }

    public String getName() {
        name = sharedPreferences.getString("name", "");
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void updateCredits(int amount){
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.remove("credits");
        spEditor.apply();
        //credits = credits + 5;
        spEditor.putInt("credits", amount);
        spEditor.apply();
    }

}
