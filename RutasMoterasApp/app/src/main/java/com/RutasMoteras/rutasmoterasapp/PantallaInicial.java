package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PantallaInicial extends AppCompatActivity {

    Button sinUsuario;
    Button conUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_inicial);

        sinUsuario = findViewById(R.id.SinUsuario);
        conUsuario = findViewById(R.id.ConUsuario);

        sinUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();

                SharedPreferences sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.apply();

                sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
                editor = sharedPref.edit();

                editor.putString("LoginResponse", "");
                editor.putLong("TokenTimestamp", currentTime);
                editor.apply();

                Intent intent = new Intent(PantallaInicial.this, RutasList.class);
                startActivity(intent);
            }
        });

        conUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PantallaInicial.this, Login.class);
                startActivity(intent);
            }
        });
    }
}