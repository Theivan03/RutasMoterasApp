package com.example.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.rutasmoterasapp.Login;
import com.example.rutasmoterasapp.R;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

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
                Intent intent = new Intent(Slash.this, Login.class);
                startActivity(intent);
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
