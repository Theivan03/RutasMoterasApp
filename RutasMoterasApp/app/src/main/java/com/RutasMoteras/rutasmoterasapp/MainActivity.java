package com.RutasMoteras.rutasmoterasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Códigos de los permisos... (definidos por el usuario)
    private static final int CODIGO_PERMISOS_CAMARA = 1;
    private static final int CODIGO_PERMISOS_MENSAJE = 2;

    ImageView img_ok_camara, img_no_camara, img_ok_mensaje, img_no_mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Imágenes
        img_ok_camara = (ImageView) findViewById(R.id.img_ok_camara);
        img_no_camara = (ImageView) findViewById(R.id.img_no_camara);
        img_ok_mensaje = (ImageView) findViewById(R.id.img_ok_mensaje);
        img_no_mensaje = (ImageView) findViewById(R.id.img_no_mensaje);

        // Botones para pedir los permisos...
        Button btnPermisoCamara = findViewById(R.id.btnPermisoCamara);

        btnPermisoCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarPermisosCamara();
            }
        });

        Button btnPermisoMensaje = findViewById(R.id.btnPermisoMensaje);
        btnPermisoMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarPermisosMensaje();
            }
        });

        Button btnAbrirCamara = findViewById(R.id.btnAbrirCamara);
        btnAbrirCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int estado = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA);
                if (estado != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "No tienes permisos para lanzar la cámara", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    startActivityForResult(intent, 0);
                }
            }
        });

        Button btnEnviarMensaje = findViewById(R.id.btnEnviarSMS);
        btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int estado = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.RECEIVE_SMS);
                if (estado != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "No tienes permisos para enviar mensajes", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Tienes permisos para enviar mensajes", Toast.LENGTH_SHORT).show();
                    SmsManager s = SmsManager.getDefault();
                    String numDestino = "XXXXXXXXX";
                    s.sendTextMessage(numDestino, null, "¿Quedamos a comer?", null, null);
                }
            }
        });
    }

    private void verificarPermisosCamara() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CAMERA);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) {  // Tenemos el permiso concedido...
            Toast.makeText(MainActivity.this, "El permiso para la cámara ya está concedido", Toast.LENGTH_SHORT).show();
            img_ok_camara.setVisibility(View.VISIBLE);
            img_no_camara.setVisibility(View.GONE);
        } else { // No tenemos el permiso, lo pedimos...
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, CODIGO_PERMISOS_CAMARA);
        }
    }

    private void verificarPermisosMensaje() {
        int estadoDePermiso = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.RECEIVE_SMS);
        if (estadoDePermiso == PackageManager.PERMISSION_GRANTED) { // Tenemos el permiso concedido...
            Toast.makeText(MainActivity.this, "El permiso para usar los mensajes ya está concedido", Toast.LENGTH_SHORT).show();
            img_ok_mensaje.setVisibility(View.VISIBLE);
            img_no_mensaje.setVisibility(View.GONE);
        } else { // No tenemos el permiso, lo pedimos...
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, CODIGO_PERMISOS_MENSAJE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CODIGO_PERMISOS_CAMARA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Si se cancelo la petici
                    // ón, los array's van vacíos.
                    Toast.makeText(MainActivity.this, "Has concedido el permiso para usar la cámara", Toast.LENGTH_SHORT).show();
                    img_ok_camara.setVisibility(View.VISIBLE);
                    img_no_camara.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, "Has denegado el permiso para usar la cámara", Toast.LENGTH_SHORT).show();
                    img_ok_camara.setVisibility(View.GONE);
                    img_no_camara.setVisibility(View.VISIBLE);
                }
                break;
            case CODIGO_PERMISOS_MENSAJE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Has concedido el permiso para usar los mensajes", Toast.LENGTH_SHORT).show();
                    img_ok_mensaje.setVisibility(View.VISIBLE);
                    img_no_mensaje.setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, "Has denegado el permiso para usar los mensajes", Toast.LENGTH_SHORT).show();
                    img_ok_mensaje.setVisibility(View.GONE);
                    img_no_mensaje.setVisibility(View.VISIBLE);
                }
                break;

            // Aquí más casos dependiendo de los permisos
            // case OTRO_CODIGO_DE_PERMISOS...

        }
    }
}