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
                registrarUsuario(contraseña.getText().toString(), nombre.toString(), apellidos.toString(), email.getText().toString(), convertirImagenABase64(R.drawable.userwhothoutphoto), new int[]{1});

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

    private String convertirImagenABase64(int drawableId) {
        Bitmap bitmapOriginal = BitmapFactory.decodeResource(getResources(), drawableId);

        int maxWidth = 480;
        int maxHeight = 480;
        float scaleWidth = maxWidth / (float) bitmapOriginal.getWidth();
        float scaleHeight = maxHeight / (float) bitmapOriginal.getHeight();
        float scale = Math.min(scaleWidth, scaleHeight);

        int width = Math.round(scale * bitmapOriginal.getWidth());
        int height = Math.round(scale * bitmapOriginal.getHeight());

        Bitmap bitmapRedimensionado = Bitmap.createScaledBitmap(bitmapOriginal, width, height, true);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void registrarUsuario(String password, String name, String surname, String email, String foto, int[] rolIds) {
        JSONObject usuario = new JSONObject();
        try {
            usuario.put("password", password);
            usuario.put("name", name);
            usuario.put("surname", surname);
            usuario.put("email", email);
            usuario.put("image", foto);
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
}