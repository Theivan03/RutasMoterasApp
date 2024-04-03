package com.RutasMoteras.rutasmoterasapi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.RutasMoteras.rutasmoterasapp.Login;

import java.util.Calendar;

public class CheckLogin {

    public static void checkLastLoginDay(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        int lastLoginDay = sharedPref.getInt("TokenDay", -1);

        if (lastLoginDay != -1) {
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
            int dayDifference = currentDay - lastLoginDay;

            if (dayDifference > 14) {
                showLoginDialog(context);
            }
        }
    }

    private static void showLoginDialog(Context context) {
        Intent intent = new Intent(context, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
