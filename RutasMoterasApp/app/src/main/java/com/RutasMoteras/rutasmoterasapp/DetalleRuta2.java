package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.RutasMoteras.rutasmoterasapi.CheckLogin;
import com.RutasMoteras.rutasmoterasapi.RutasModel;
import com.RutasMoteras.rutasmoterasapi.UtilJSONParser;
import com.RutasMoteras.rutasmoterasapi.UtilREST;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.graphics.Color;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class DetalleRuta2 extends AppCompatActivity {

    TextView tipoMotoTextView, tituloTextView, fechaTextView, comunidadTextView, descripcionTextView;
    ImageView imgView;
    Button boton;
    String token;
    RutasModel ruta;
    String apiUrl;
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


        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPref.getString("LoginResponse", null);

        sharedPref = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedPref.getString("URL", "");

        String rutaInfo = leerRutaDesdeArchivo();

        LLamarApi(apiUrl + "api/ruta/" + rutaInfo);
        Log.d("Url de la ruta: ", apiUrl + "api/ruta/" + rutaInfo);

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalleRuta2.this, EditRuta.class);
                startActivity(intent);
            }
        });
    }

    private String leerRutaDesdeArchivo() {
        StringBuilder rutaInfo = new StringBuilder();
        try {
            FileInputStream fis = openFileInput("ruta_seleccionada.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String linea;
            while ((linea = br.readLine()) != null) {
                rutaInfo.append(linea);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Leído del archivo:", rutaInfo.toString());
        return rutaInfo.toString();
    }

    public void LLamarApi(String url){

        CheckLogin.checkLastLoginDay(getApplicationContext());

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                ruta = UtilJSONParser.parsePostRuta(jsonContent);

                actualizarVistasConDatosDeRuta();
            }

            @Override
            public void onError(UtilREST.Response r) {
                if (r.content != null) {
                    Log.d("ERROR!!!!!!!!!", r.content);
                } else {
                    Log.d("ERROR!!!!!!!!!", "El contenido de la respuesta es nulo");
                }
                Toast.makeText(DetalleRuta2.this, getResources().getString(R.string.ErrorServidor), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(DetalleRuta2.this, PantallaInicial.class);
                startActivity(intent);
            }
        });
    }

    private void actualizarVistasConDatosDeRuta() {
        if (ruta != null) {
            int color = Color.parseColor("#1e88e5");

            tituloTextView.setText(ruta.getTitle());

            String tipoMotoLabel = getResources().getString(R.string.tipoMoto) + ": ";
            String tipoMoto = ruta.getTipoMoto();
            SpannableString spannableTipoMoto = new SpannableString(tipoMotoLabel + tipoMoto);
            spannableTipoMoto.setSpan(new ForegroundColorSpan(color), 0, tipoMotoLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tipoMotoTextView.setText(spannableTipoMoto);

            String fechaLabel = getResources().getString(R.string.fecha) + ": ";
            String fecha = ruta.getDate();
            SpannableString spannableFecha = new SpannableString(fechaLabel + fecha);
            spannableFecha.setSpan(new ForegroundColorSpan(color), 0, fechaLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            fechaTextView.setText(spannableFecha);

            String comunidadLabel = getResources().getString(R.string.comAuto) + ": ";
            String comunidad = ruta.getComunidad();
            SpannableString spannableComunidad = new SpannableString(comunidadLabel + comunidad);
            spannableComunidad.setSpan(new ForegroundColorSpan(color), 0, comunidadLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            comunidadTextView.setText(spannableComunidad);

            String descripcionLabel = getResources().getString(R.string.descripcion) + ": ";
            String descripcion = ruta.getDescription();
            SpannableString spannableDescripcion = new SpannableString(descripcionLabel + descripcion);
            spannableDescripcion.setSpan(new ForegroundColorSpan(color), 0, descripcionLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            descripcionTextView.setText(spannableDescripcion);

            String base64Image = ruta.getImage();

            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if(decodedByte != null) {
                imgView.setImageBitmap(decodedByte);
            } else {
                Log.e("DetalleRuta2", "La decodificación de la imagen falló.");
            }

            // Decodifica y carga la imagen
            Glide.with(this)
                    .asBitmap()
                    .load(decodedString)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.favicon) // Asegúrate de tener este recurso drawable.
                    .into(imgView);
        } else {
            // Maneja el caso en que `ruta` sea null
        }
    }

}