package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SingIn extends AppCompatActivity {

    private EditText nombre;
    private EditText apellidos;
    private Button siguiente;
    private Button cancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        nombre = findViewById(R.id.nombre);
        apellidos = findViewById(R.id.apellidos);
        siguiente = findViewById(R.id.siguiente);
        cancelar = findViewById(R.id.cancelar);
    }

    private void setupListeners() {
        siguiente.setOnClickListener(v -> handleNextButtonClick());
        cancelar.setOnClickListener(v -> handleCancelButtonClick());
    }

    private void handleNextButtonClick() {
        String nombreText = nombre.getText().toString().trim();
        String apellidosText = apellidos.getText().toString().trim();

        if (validateFields(nombreText, apellidosText)) {
            Intent intent = new Intent(SingIn.this, SingIn2.class);
            intent.putExtra("nombre", nombreText);
            intent.putExtra("apellidos", apellidosText);
            startActivity(intent);
        }
    }

    private boolean validateFields(String nombreText, String apellidosText) {
        if (nombreText.isEmpty()) {
            nombre.setError(getResources().getString(R.string.debePonerNombre));
            return false;
        }

        if (nombreText.length() > 25) {
            nombre.setError(getResources().getString(R.string.menosDe25Caracteres));
            return false;
        }

        if (apellidosText.isEmpty()) {
            apellidos.setError(getResources().getString(R.string.debePonerApellidos));
            return false;
        }

        if (apellidosText.length() > 50) {
            apellidos.setError(getResources().getString(R.string.menosDe50Caracteres));
            return false;
        }

        return true;
    }

    private void handleCancelButtonClick() {
        Intent intent = new Intent(SingIn.this, Login.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
