package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class Slash extends AppCompatActivity implements Animation.AnimationListener   {

    private static final long SPLASH_DELAY = 3000;
    private Toast customToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash);

        SharedPreferences sharedPref = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("URL", "http://192.168.1.131:5000/");
        editor.apply();

        CircularFillableLoaders cargando = findViewById(R.id.logo_circular);
        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.splashanimation);
        cargando.startAnimation(animacion);

        animacion.setAnimationListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int currentDay = calendar.get(Calendar.DAY_OF_YEAR);

                SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
                int tokenDay = sharedPref.getInt("TokenDay", -1);

                if (tokenDay != -1) {
                    int dayDifference = currentDay - tokenDay;

                    if (dayDifference <= 15) {
                        Intent intent = new Intent(Slash.this, RutasList.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(Slash.this, PantallaInicial.class);
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(Slash.this, PantallaInicial.class);
                    startActivity(intent);
                }
                finish();
            }
        }, SPLASH_DELAY);
        //LLamarApi("http://192.168.1.131:5000/api/rutas");
    }

    public void LLamarApi(String url) {
        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String token = sharedPref.getString("LoginResponse", "");


        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("datosRutas", jsonContent);
                editor.apply();


                Intent intent = new Intent(Slash.this, RutasList.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(UtilREST.Response r) {
                if (r.content != null) {
                    Log.d("ERROR", r.content);
                } else {
                }
                Toast.makeText(Slash.this, getResources().getString(R.string.ErrorServidor), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Slash.this, PantallaInicial.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {}

    @Override
    public void onAnimationRepeat(Animation animation) {}
}
