package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.RutasMoteras.rutasmoterasapi.API;
import com.RutasMoteras.rutasmoterasapi.UtilREST;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class SingIn2 extends AppCompatActivity {

    SharedPreferences sharedURL;
    String apiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in2);

        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        Button crear = findViewById(R.id.crear);
        EditText email = findViewById(R.id.email);
        EditText contraseña = findViewById(R.id.contraseña);

        String nombre = getIntent().getStringExtra("nombre");
        String apellidos = getIntent().getStringExtra("apellidos");
        Button cancelar = findViewById(R.id.cancelar);

        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = email.getText().toString().trim();
                String contraseñaText = contraseña.getText().toString().trim();

                if (emailText.isEmpty() || contraseñaText.isEmpty()) {
                    if (emailText.isEmpty()) email.setError(getResources().getString(R.string.emailVacio));
                    if (contraseñaText.isEmpty()) contraseña.setError(getResources().getString(R.string.contraseñaVacia));
                    return;
                }

                if (!validarEmail(emailText)) {
                    email.setError(getResources().getString(R.string.emailValido));
                    return;
                }

                String resultadoValidacion = validarContraseña(contraseñaText);
                if (resultadoValidacion != null) {
                    contraseña.setError(resultadoValidacion);
                    return;
                }

                registrarUsuario(contraseñaText, nombre.toString(), apellidos.toString(), emailText, new int[]{1});

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

    boolean validarEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    String validarContraseña(String contraseña) {
        if (contraseña.length() < 8) {
            return getResources().getString(R.string.contra8Caracteres);
        }
        if (!contraseña.matches(".*[0-9].*")) {
            return getResources().getString(R.string.contraNumeros);
        }
        if (!contraseña.matches(".*[a-z].*")) {
            return getResources().getString(R.string.contraMinuscula);
        }
        if (!contraseña.matches(".*[A-Z].*")) {
            return getResources().getString(R.string.contraMayuscula);
        }
        if (contraseña.matches(".*\\s+.*")) {
            return getResources().getString(R.string.contraEspacios);
        }
        return null;
    }


    void registrarUsuario(String password, String name, String surname, String email, int[] rolIds) {
        JSONObject usuario = new JSONObject();
        try {
            usuario.put("password", password);
            usuario.put("name", name);
            usuario.put("surname", surname);
            usuario.put("email", email);
            usuario.put("image", "");
            usuario.put("city", "");
            usuario.put("postalCode", "");
            JSONArray rolIdsArray = new JSONArray();
            for (int rolId : rolIds) {
                rolIdsArray.put(rolId);
            }
            usuario.put("rolIds", rolIdsArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        API.postPost(usuario, apiUrl + "auth/signup", new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response response) {
                String responseData = response.content;
                Log.d("Response", responseData);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SingIn.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}