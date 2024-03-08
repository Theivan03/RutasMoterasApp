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
import android.widget.Button;
import android.widget.EditText;

import com.example.rutasmoterasapi.API;
import com.example.rutasmoterasapi.UtilREST;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    Button registrar;
    Button login;
    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registrar = findViewById(R.id.registrar);
        login = findViewById(R.id.iniciar);
        email = findViewById(R.id.nombre);
        password = findViewById(R.id.apellidos);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(password.getText().toString(), email.getText().toString());
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SingIn.class);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivity(intent);
                }
            }
        });
    }

    private void loginUser(String password, String email) {
        JSONObject loginData = new JSONObject();
        try {
            loginData.put("password", password);
            loginData.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Realizar la solicitud de inicio de sesión utilizando la clase API
        API.postPost(loginData, "http://192.168.1.131:5000/auth/login", new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response response) {
                String responseData = response.content;
                Log.d("Login Response", responseData);

                // Obtén la hora actual en milisegundos
                long currentTime = System.currentTimeMillis();

                SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                // Guarda el token y la hora actual
                editor.putString("LoginResponse", responseData);
                editor.putLong("TokenTimestamp", currentTime);

                editor.apply();

                Intent intent = new Intent(Login.this, RutasList.class);
                startActivity(intent);
            }


            @Override
            public void onError(UtilREST.Response response) {
                String errorData = response.content;
                if (errorData != null) {
                    Log.e("Login Error", errorData);
                } else {
                    Log.e("Login Error", "Error data is null");
                }
                // Manejar el error si el inicio de sesión no fue exitoso
            }
        });
    }
}