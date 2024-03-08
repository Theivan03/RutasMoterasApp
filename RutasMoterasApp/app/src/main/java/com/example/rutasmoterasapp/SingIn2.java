package com.example.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.rutasmoterasapi.API;
import com.example.rutasmoterasapi.UtilREST;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SingIn2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in2);

        Button crear = findViewById(R.id.crear);
        EditText email = findViewById(R.id.email);
        EditText contraseña = findViewById(R.id.contraseña);

        String nombre = getIntent().getStringExtra("nombre");
        String apellidos = getIntent().getStringExtra("apellidos");
        Button cancelar = findViewById(R.id.cancelar);

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario(contraseña.getText().toString(), nombre.toString(), apellidos.toString(), email.getText().toString(), new int[]{1});

                Intent intent = new Intent(SingIn2.this, Login.class);
                startActivity(intent);
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingIn2.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private void registrarUsuario(String password, String name, String surname, String email, int[] rolIds) {
        JSONObject usuario = new JSONObject();
        try {
            usuario.put("password", password);
            usuario.put("name", name);
            usuario.put("surname", surname);
            usuario.put("email", email);
            JSONArray rolIdsArray = new JSONArray();
            for (int rolId : rolIds) {
                rolIdsArray.put(rolId);
            }
            usuario.put("rolIds", rolIdsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Aquí se utiliza la clase API para realizar la solicitud de registro
        API.postPost(usuario, "http://192.168.1.131:5000/auth/signup", new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response response) {
                String responseData = response.content;
                Log.d("Response", responseData);
                // Aquí puedes procesar la respuesta del servidor después del éxito del registro
            }

            @Override
            public void onError(UtilREST.Response response) {
                String errorData = response.content;
                if (errorData != null) {
                    Log.e("Error", errorData);
                } else {
                    Log.e("Error", "Error data is null");
                }
            }

        });
    }
}