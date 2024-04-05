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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

public class DetalleRuta extends AppCompatActivity {

    TextView tipoMotoTextView, tituloTextView, fechaTextView, comunidadTextView, descripcionTextView;
    ImageView imgView;
    Button visitar;
    String token;
    RutasModel ruta;
    String apiUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ruta);

        tipoMotoTextView = findViewById(R.id.TipoMoto);
        tituloTextView = findViewById(R.id.Titulo);
        fechaTextView = findViewById(R.id.Fecha);
        comunidadTextView = findViewById(R.id.Comunidad);
        descripcionTextView = findViewById(R.id.Decripcion);
        imgView = findViewById(R.id.imgRuta);
        visitar = findViewById(R.id.visitar);

        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPref.getString("LoginResponse", null);

        sharedPref = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedPref.getString("URL", "");

        String rutaInfo = leerRutaDesdeArchivo();

        LLamarApi(apiUrl + "api/ruta/" + rutaInfo);
        Log.d("Url de la ruta: ", apiUrl + "api/ruta/" + rutaInfo);

        visitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = String.valueOf(ruta.getUserId());

                Intent intent = new Intent(DetalleRuta.this, MostrarUser.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
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
                Toast.makeText(DetalleRuta.this, getResources().getString(R.string.ErrorServidor), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(DetalleRuta.this, PantallaInicial.class);
                startActivity(intent);
            }
        });
    }

    private void actualizarVistasConDatosDeRuta() {
        if (ruta != null) {
            tipoMotoTextView.setText(getResources().getString(R.string.tipoMoto) + ": " + ruta.getTipoMoto());
            tituloTextView.setText(getResources().getString(R.string.titulo) + ": " + ruta.getTitle());
            fechaTextView.setText(getResources().getString(R.string.fecha) + ": " + ruta.getDate());
            comunidadTextView.setText(getResources().getString(R.string.comAuto) + ": " + ruta.getComunidad());
            descripcionTextView.setText(getResources().getString(R.string.descripcion) + ": " + ruta.getDescription());

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

}