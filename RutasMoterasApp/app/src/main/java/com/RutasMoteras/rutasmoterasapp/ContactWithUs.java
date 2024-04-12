package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

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
                /**
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
                }, 3000);**/
                sendEmail(name, message);
            }
        });
    }

    private void sendEmail(String nombre, String mensaje) {

        new Thread(() -> {
            try {

                final String passwordSenderEmail = "tioilxblzdgivveh";

                final String senderEmail = "rutasmoterasoficial@gmail.com";
                final String receiverEmail = "rutasmoterasoficial@gmail.com";

                final String host = "smtp.gmail.com";
                Properties properties = new Properties();
                properties.put("mail.transport.protocol", "smtp");
                properties.put("mail.smtp.host", host);
                properties.put("mail.smtp.port", "587");
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");

                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, passwordSenderEmail);
                    }
                });

                SharedPreferences sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                String nameUser = sharedPref.getString("Name", "");
                String emailUser = sharedPref.getString("Email", "");

                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));

                mimeMessage.setSubject("Duda/Error de " + nombre);
                mimeMessage.setText("Hola, \n\nSoy " + nameUser + ", mi correo es el " + emailUser + "\n\n\n" + mensaje + ". \n\nSaludos!😘");

                Transport.send(mimeMessage);
                runOnUiThread(() -> Toast.makeText(ContactWithUs.this, "Correo enviado correctamente.", Toast.LENGTH_SHORT).show());
                Intent intent = new Intent(ContactWithUs.this, RutasList.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ContactWithUs.this, "Error al enviar el correo", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}