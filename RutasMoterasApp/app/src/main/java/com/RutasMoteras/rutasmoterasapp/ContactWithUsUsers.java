package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactWithUsUsers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_with_us_users);

        final EditText nameField = findViewById(R.id.nameUser);
        final EditText messageField = findViewById(R.id.messageUser);
        Button sendButton = findViewById(R.id.sendUser);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameField.getText().toString();
                String message = messageField.getText().toString();

                Toast.makeText(ContactWithUsUsers.this, "Enviado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}