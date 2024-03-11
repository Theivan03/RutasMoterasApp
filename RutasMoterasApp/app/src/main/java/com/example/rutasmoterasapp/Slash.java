package com.example.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.rutasmoterasapp.Login;
import com.example.rutasmoterasapp.R;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.util.Calendar;

public class Slash extends AppCompatActivity implements Animation.AnimationListener {

    private static final long SPLASH_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slash);

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
    }

    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {}

    @Override
    public void onAnimationRepeat(Animation animation) {}
}
