package com.example.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.rutasmoterasapi.RutasModel;
import com.example.rutasmoterasapi.UtilREST;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PeliculasList extends AppCompatActivity {

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
}