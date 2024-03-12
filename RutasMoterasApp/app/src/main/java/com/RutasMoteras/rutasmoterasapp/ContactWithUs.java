package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactWithUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_with_us);

        final EditText nameField = findViewById(R.id.name);
        final EditText emailField = findViewById(R.id.email);
        final EditText messageField = findViewById(R.id.message);
        Button sendButton = findViewById(R.id.send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Aquí puedes agregar la lógica para manejar el envío de información
                // Por ejemplo, enviar un correo electrónico o guardar en una base de datos
                String name = nameField.getText().toString();
                String email = emailField.getText().toString();
                String message = messageField.getText().toString();

                // Mostrar un mensaje de confirmación o proceder con la operación de envío
                Toast.makeText(ContactWithUs.this, "Enviado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}