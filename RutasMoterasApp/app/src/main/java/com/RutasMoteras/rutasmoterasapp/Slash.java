package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.RutasMoteras.rutasmoterasapi.UtilREST;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.util.Calendar;

public class Slash extends AppCompatActivity {

    private static final long SPLASH_DELAY = 3000;
    //private static final String BASE_URL = "http://192.168.1.131:5000/";
    private static final String BASE_URL = "http://44.207.234.210/";
    private static final String APP_URL_KEY = "AppURL";
    private static final String APP_PREFERENCES_KEY = "AppPreferences";
    private static final String USER_PREFERENCES_KEY = "UserPreferences";
    private static final String LOGIN_RESPONSE_KEY = "LoginResponse";
    private static final String TOKEN_DAY_KEY = "TokenDay";
    private static final String ROLE_KEY = "Role";
    private static final String DATOS_RUTAS_KEY = "datosRutas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash);

        initializeSharedPreferences();
        startSplashAnimation();
        scheduleNextActivity();
    }

    private void initializeSharedPreferences() {
        SharedPreferences sharedPref = getSharedPreferences(APP_URL_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("URL", BASE_URL);
        editor.apply();
    }

    private void startSplashAnimation() {
        CircularFillableLoaders cargando = findViewById(R.id.logo_circular);
        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.splashanimation);
        cargando.startAnimation(animacion);
    }

    private void scheduleNextActivity() {
        new Handler().postDelayed(() -> {
            Calendar calendar = Calendar.getInstance();
            int currentDay = calendar.get(Calendar.DAY_OF_YEAR);

            SharedPreferences sharedPref = getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
            int tokenDay = sharedPref.getInt(TOKEN_DAY_KEY, -1);

            sharedPref = getSharedPreferences(USER_PREFERENCES_KEY, Context.MODE_PRIVATE);
            Long role = sharedPref.getLong(ROLE_KEY, 1);

            Intent intent;
            if (tokenDay != -1) {
                int dayDifference = currentDay - tokenDay;
                if (dayDifference <= 15) {
                    if (role == 2) {
                        intent = new Intent(Slash.this, SuperUser.class);
                    } else {
                        intent = new Intent(Slash.this, RutasList.class);
                    }
                } else {
                    intent = new Intent(Slash.this, PantallaInicial.class);
                }
            } else {
                intent = new Intent(Slash.this, PantallaInicial.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }

    public void LLamarApi(String url) {
        SharedPreferences sharedPref = getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        String token = sharedPref.getString(LOGIN_RESPONSE_KEY, "");

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(DATOS_RUTAS_KEY, jsonContent);
                editor.apply();

                Intent intent = new Intent(Slash.this, RutasList.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(UtilREST.Response r) {
                if (r.content != null) {
                    Log.d("ERROR", r.content);
                }
                Toast.makeText(Slash.this, getResources().getString(R.string.ErrorServidor), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Slash.this, PantallaInicial.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
