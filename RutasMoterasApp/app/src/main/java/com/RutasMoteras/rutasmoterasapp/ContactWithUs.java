package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContactWithUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_with_us);

        final EditText nameField = findViewById(R.id.nameUser);
        final EditText messageField = findViewById(R.id.messageUser);
        Button sendButton = findViewById(R.id.sendUser);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameField.getText().toString();
                String message = messageField.getText().toString();
                String subject = "Duda/Error de " + name;

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"rutasmoterasoficial@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, message);

                try {
                    startActivity(Intent.createChooser(intent, "Enviar correo"));
                } catch (android.content.ActivityNotFoundException ex) {
                    // Manejar la excepción si no hay clientes de correo electrónico instalados.
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 3000);
            }
        });
    }
}