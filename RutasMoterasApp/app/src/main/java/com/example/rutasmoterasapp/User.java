package com.example.rutasmoterasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

public class User extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Omitir el título predeterminado para usar el TextView personalizado
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Configura el botón de menú para abrir el menú cuando se haga clic
        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> openOptionsMenu());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_usu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();


        if(id == R.id.Editar){
            Intent intent = new Intent(User.this, Informacion.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.Logout){
            SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("TokenDay", -1);
            editor.apply();

            sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.clear();
            editor.apply();

            sharedPref = getSharedPreferences("LogPreferences", Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.putBoolean("Log", false);
            editor.apply();

            Intent intent = new Intent(User.this, PantallaInicial.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.Contacta){
            Intent intent = new Intent(User.this, Informacion.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}