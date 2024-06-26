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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ContactWithUs extends AppCompatActivity {
    private static final String SENDER_EMAIL = "rutasmoterasoficial@gmail.com";
    private static final String RECEIVER_EMAIL = "rutasmoterasoficial@gmail.com";
    private static final String PASSWORD_SENDER_EMAIL = "tioilxblzdgivveh";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

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
                String name = nameField.getText().toString().trim();
                String message = messageField.getText().toString().trim();

                if (validateInput(name, message)) {
                    sendEmail(name, message);
                } else {
                    Snackbar.make(view, getResources().getString(R.string.errorValidacion), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validateInput(String name, String message) {
        return !name.isEmpty() && !message.isEmpty();
    }

    void sendEmail(String nombre, String mensaje) {
        showProgressDialog();

        new Thread(() -> {
            try {
                Properties properties = new Properties();
                properties.put("mail.transport.protocol", "smtp");
                properties.put("mail.smtp.host", SMTP_HOST);
                properties.put("mail.smtp.port", SMTP_PORT);
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");

                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, PASSWORD_SENDER_EMAIL);
                    }
                });

                SharedPreferences sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                String emailUser = sharedPref.getString("Email", "");

                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(RECEIVER_EMAIL));
                mimeMessage.setSubject("Duda/Error de " + nombre);
                mimeMessage.setText("Hola, \n\nSoy " + nombre + ", mi correo es el " + emailUser + "\n\n\n" + mensaje + ". \n\nSaludos!😘");

                Transport.send(mimeMessage);
                runOnUiThread(this::onEmailSentSuccess);
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> onEmailSentFailure(e));
            }
        }).start();
    }

    private void onEmailSentSuccess() {
        hideProgressDialog();
        showSuccessDialog();
    }

    private void onEmailSentFailure(Exception e) {
        hideProgressDialog();
        Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.errorCorreo) + ": " + e.getMessage(), Snackbar.LENGTH_LONG).show();
    }

    private void showSuccessDialog() {
        AlertDialog successDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.correoEnviado))
                .setMessage(getResources().getString(R.string.correoEnviadoCorrectamente))
                .setCancelable(false)
                .create();

        successDialog.show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(ContactWithUs.this, RutasList.class);
            startActivity(intent);
        }, 1500);
    }

    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle(getResources().getString(R.string.enviandoCorreo));
            progressDialog.setMessage(getResources().getString(R.string.esperarEnviandoCorreo));
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
