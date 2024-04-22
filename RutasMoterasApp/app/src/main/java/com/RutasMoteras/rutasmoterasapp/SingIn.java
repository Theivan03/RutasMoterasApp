package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SingIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_in);

        Button siguiente = findViewById(R.id.siguiente);
        Button cancelar = findViewById(R.id.cancelar);
        EditText nombre = findViewById(R.id.nombre);
        EditText apellidos = findViewById(R.id.apellidos);

        siguiente.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String nombreText = nombre.getText().toString().trim();
                String apellidosText = apellidos.getText().toString().trim();
                if (nombreText.isEmpty()) {
                    nombre.setError("Debe ingresar un nombre");
                    return;
                }

                if (apellidosText.isEmpty()) {
                    apellidos.setError("Debe ingresar los apellidos");
                    return;
                }

                Intent intent = new Intent(SingIn.this, SingIn2.class);
                intent.putExtra("nombre", nombreText);
                intent.putExtra("apellidos", apellidosText);
                startActivity(intent);
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SingIn.this, Login.class);
                startActivity(intent);
            }
        });

    }


}
