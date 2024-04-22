package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.Calendar;

public class SuperUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user);
    }

    private void checkLastLoginDay() {
        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        int lastLoginDay = sharedPref.getInt("TokenDay", -1);

        if (lastLoginDay != -1) {
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
            int dayDifference = currentDay - lastLoginDay;

            if (dayDifference > 14) {
                showLoginDialog();
            }
        }
    }

    private void showLoginDialog() {
        Intent intent = new Intent(SuperUser.this, Login.class);
        startActivity(intent);
        finish();
    }
}