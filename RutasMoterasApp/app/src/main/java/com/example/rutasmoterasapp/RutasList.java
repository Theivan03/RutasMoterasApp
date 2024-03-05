package com.example.rutasmoterasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rutasmoterasapi.RutasModel;
import com.example.rutasmoterasapi.UtilREST;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RutasList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas_list);


        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String token = sharedPref.getString("LoginToken", "");
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);

        UtilREST.runQuery(UtilREST.QueryType.GET, "http://44.207.234.210/api/rutas", null, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                Gson gson = new Gson();
                Type listType = new TypeToken<ArrayList<RutasModel>>(){}.getType();
                ArrayList<RutasModel> rutasList = gson.fromJson(r.content, listType);
                Log.d("Resultado de las rutas: ", r.content);
            }

            @Override
            public void onError(UtilREST.Response r) {
                // Manejar el error
            }
        }, headers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.mi_menu, menu);
        return true;
    }

    // Menú AcercaDe, MasInfo y NuevaPeli
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if(id == R.id.Deportiva){

        }

        if(id == R.id.MasInfo){
            Intent intent = new Intent(RutasList.this, Informacion.class);
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }
            return true;
        }

        if(id == R.id.CV){


        }

        return super.onOptionsItemSelected(item);
    }
}