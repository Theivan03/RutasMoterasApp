package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private ProgressDialog progressDialog;

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

                sendEmail(name, message);
            }
        });
    }

    void sendEmail(String nombre, String mensaje) {

        showProgressDialog();

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
                String emailUser = sharedPref.getString("Email", "");

                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));

                mimeMessage.setSubject("Duda/Error de " + nombre);
                mimeMessage.setText("Hola, \n\nSoy " + nombre + ", mi correo es el " + emailUser + "\n\n\n" + mensaje + ". \n\nSaludos!😘");

                Transport.send(mimeMessage);
                runOnUiThread(() -> {
                    hideProgressDialog();
                    showSuccessDialog();

                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    hideProgressDialog();
                    Toast.makeText(ContactWithUs.this, getResources().getString(R.string.errorCorreo), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void showSuccessDialog() {
        AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.correoEnviado))
                .setMessage(getResources().getString(R.string.correoEnviadoCorrectamente))
                .setCancelable(false)
                .create();

        successDialog.show();


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ContactWithUs.this, RutasList.class);
                startActivity(intent);
            }
        }, 1500);
    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getResources().getString(R.string.enviandoCorreo));
        progressDialog.setMessage(getResources().getString(R.string.esperarEnviandoCorreo));
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}