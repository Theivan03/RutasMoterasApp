package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.RutasMoteras.rutasmoterasapi.API;
import com.RutasMoteras.rutasmoterasapi.UtilREST;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SingIn2 extends AppCompatActivity {

    private SharedPreferences sharedURL;
    private String apiUrl;
    private Button crear;
    private Button cancelar;
    private EditText email;
    private EditText contraseña;
    private String nombre;
    private String apellidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in2);

        initializeViews();
        setupListeners();

        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        nombre = getIntent().getStringExtra("nombre");
        apellidos = getIntent().getStringExtra("apellidos");
    }

    private void initializeViews() {
        crear = findViewById(R.id.crear);
        email = findViewById(R.id.email);
        contraseña = findViewById(R.id.contraseña);
        cancelar = findViewById(R.id.cancelar);
    }

    private void setupListeners() {
        crear.setOnClickListener(v -> handleCreateButtonClick());
        cancelar.setOnClickListener(v -> handleCancelButtonClick());
    }

    private void handleCreateButtonClick() {
        String emailText = email.getText().toString().trim();
        String contraseñaText = contraseña.getText().toString().trim();

        if (validateFields(emailText, contraseñaText)) {
            registrarUsuario(contraseñaText, nombre, apellidos, emailText, new int[]{1}, email);
        }
    }

    private void handleCancelButtonClick() {
        navigateToLogin();
    }

    private boolean validateFields(String emailText, String contraseñaText) {
        boolean isValid = true;

        if (emailText.isEmpty()) {
            email.setError(getResources().getString(R.string.emailVacio));
            isValid = false;
        } else if (!validarEmail(emailText)) {
            email.setError(getResources().getString(R.string.emailValido));
            isValid = false;
        }

        if (contraseñaText.isEmpty()) {
            contraseña.setError(getResources().getString(R.string.contraseñaVacia));
            isValid = false;
        } else {
            String resultadoValidacion = validarContraseña(contraseñaText);
            if (resultadoValidacion != null) {
                contraseña.setError(resultadoValidacion);
                isValid = false;
            }
        }

        return isValid;
    }

    private boolean validarEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private String validarContraseña(String contraseña) {
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

    private void registrarUsuario(String password, String name, String surname, String email, int[] rolIds, EditText emailField) {
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
                // Si el registro es exitoso, navega al login
                navigateToLogin();
            }

            @Override
            public void onError(UtilREST.Response response) {
                String errorData = response.content;
                if (errorData != null) {
                    Log.e("Error", errorData);
                    // Aquí se verifica si el error es debido a que el correo ya está en uso
                    if (errorData.contains("Duplicate entry")) {
                        runOnUiThread(() -> emailField.setError("El correo ya está en uso. Por favor, elija otro."));
                    }
                } else {
                    Log.e("Error", "Error data is null");
                }
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SingIn2.this, Login.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, SingIn.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
