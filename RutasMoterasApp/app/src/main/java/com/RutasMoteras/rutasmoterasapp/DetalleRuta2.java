package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.RutasMoteras.rutasmoterasapi.CheckLogin;
import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class DetalleRuta2 extends AppCompatActivity {

    TextView tipoMotoTextView, tituloTextView, fechaTextView, comunidadTextView, descripcionTextView;
    ImageView imgView;
    Button boton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ruta2);

        tipoMotoTextView = findViewById(R.id.TipoMoto);
        tituloTextView = findViewById(R.id.Titulo);
        fechaTextView = findViewById(R.id.Fecha);
        comunidadTextView = findViewById(R.id.Comunidad);
        descripcionTextView = findViewById(R.id.Decripcion);
        imgView = findViewById(R.id.imgRuta);
        boton = findViewById(R.id.button2);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalleRuta2.this, EditRuta.class);
                startActivity(intent);
            }
        });


        String rutaInfo = leerRutaDesdeArchivo();

        String[] datosRuta = rutaInfo.split("\n");

        tipoMotoTextView.setText(getResources().getString(R.string.tipoMoto) + ": " + datosRuta[0]);
        tituloTextView.setText(getResources().getString(R.string.titulo) + ": " + datosRuta[1]);
        fechaTextView.setText(datosRuta[2]);
        comunidadTextView.setText(getResources().getString(R.string.comAuto) + ": " + datosRuta[3]);
        descripcionTextView.setText(getResources().getString(R.string.descripcion) + ": " +datosRuta[4]);

        Glide.with(this)
                .load(datosRuta[5])
                .into(imgView);
    }

    private String leerRutaDesdeArchivo() {

        CheckLogin.checkLastLoginDay(getApplicationContext());

        StringBuilder rutaInfo = new StringBuilder();
        try {
            FileInputStream fis = openFileInput("ruta_seleccionada.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String linea;
            while ((linea = br.readLine()) != null) {
                rutaInfo.append(linea).append("\n");
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rutaInfo.toString();
    }

}