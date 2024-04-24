package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.RutasMoteras.rutasmoterasapi.UserModel;
import com.RutasMoteras.rutasmoterasapi.API;
import com.RutasMoteras.rutasmoterasapi.UtilJSONParser;
import com.RutasMoteras.rutasmoterasapi.UtilREST;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Login extends AppCompatActivity {

    Button registrar;
    Button login;
    EditText email;
    EditText password;
    SharedPreferences sharedURL;
    String apiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        registrar = findViewById(R.id.registrar);
        login = findViewById(R.id.iniciar);
        email = findViewById(R.id.nombre);
        password = findViewById(R.id.contraseña);

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
        API.postPost(loginData, apiUrl + "auth/login", new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response response) {
                String responseData = response.content;
                Log.d("Login Response", responseData);

                Calendar calendar = Calendar.getInstance();
                int currentDay = calendar.get(Calendar.DAY_OF_YEAR);

                SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString("LoginResponse", responseData);
                editor.putInt("TokenDay", currentDay);

                editor.apply();

                ObtenerUsuario(apiUrl + "api/usuario/"+email, responseData, password);
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

    public void ObtenerUsuario(String url, String token, String contraseña){

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                UserModel user = UtilJSONParser.parseUserPosts(jsonContent);

                String cp = "";
                if (user.getPostalCode() != null && !user.getPostalCode().isEmpty()) {
                    cp = user.getPostalCode();
                }

                String city = "";
                if (user.getCity() != null && !user.getCity().isEmpty()) {
                    city = user.getCity();
                }

                SharedPreferences sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("Log", true);
                editor.putLong("Id", user.getId());
                editor.putString("Password", user.getPassword());
                editor.putString("Name", user.getName());
                editor.putString("Surname", user.getSurname());
                editor.putString("City", city);
                editor.putString("postalCode", cp);
                editor.putString("Email", user.getEmail());
                editor.putString("Foto", user.getImage());
                editor.apply();
                Log.d("Info de usuario", String.valueOf(user));

                sharedPref = getSharedPreferences("LogPreferences", Context.MODE_PRIVATE);
                editor = sharedPref.edit();
                editor.putBoolean("Log", true);
                editor.apply();

                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(user.getRoles() != 2){
                            Log.d("ID usuario", String.valueOf(user.getId()));
                            Intent intent = new Intent(Login.this, RutasList.class);
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(Login.this, SuperUser.class);
                            startActivity(intent);
                        }
                    }
                }, 500);
            }

            @Override
            public void onError(UtilREST.Response r) {
                if (r.content != null) {
                    Log.e("Login Error", r.content);
                } else {
                    Log.e("Login Error", "Error data is null");
                }
            }
        });
    }
}
