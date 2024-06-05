package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PantallaInicial extends AppCompatActivity {

    private static final String USER_PREFERENCES = "UserPreferences";
    private static final String APP_PREFERENCES = "AppPreferences";
    private static final String LOGIN_RESPONSE_KEY = "LoginResponse";
    private static final String TOKEN_TIMESTAMP_KEY = "TokenTimestamp";

    private Button sinUsuario;
    private Button conUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicial);

        initViews();
        setupListeners();
    }

    private void initViews() {
        sinUsuario = findViewById(R.id.SinUsuario);
        conUsuario = findViewById(R.id.ConUsuario);
    }

    private void setupListeners() {
        sinUsuario.setOnClickListener(v -> handleSinUsuario());
        conUsuario.setOnClickListener(v -> handleConUsuario());
    }

    private void handleSinUsuario() {
        long currentTime = System.currentTimeMillis();

        SharedPreferences sharedPref = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.apply();

        sharedPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString(LOGIN_RESPONSE_KEY, "");
        editor.putLong(TOKEN_TIMESTAMP_KEY, currentTime);
        editor.apply();

        Intent intent = new Intent(PantallaInicial.this, RutasList.class);
        startActivity(intent);
    }

    private void handleConUsuario() {
        Intent intent = new Intent(PantallaInicial.this, Login.class);
        startActivity(intent);
    }
}
