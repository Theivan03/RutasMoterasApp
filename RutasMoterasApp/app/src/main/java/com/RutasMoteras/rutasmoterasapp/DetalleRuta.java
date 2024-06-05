package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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

public class DetalleRuta extends AppCompatActivity {

    private static final String TAG = "DetalleRuta";
    private static final String FILE_NAME = "ruta_seleccionada.txt";
    private static final String LOGIN_RESPONSE_KEY = "LoginResponse";
    private static final String URL_KEY = "URL";
    private static final String APP_PREFERENCES = "AppPreferences";
    private static final String APP_URL = "AppURL";
    private static final int DEFAULT_COLOR = Color.parseColor("#808080");

    private TextView tipoMotoTextView, tituloTextView, fechaTextView, comunidadTextView, descripcionTextView;
    private ImageView imgView;
    private Button visitar;
    private String token;
    private RutasModel ruta;
    private String apiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_ruta);

        initViews();
        loadPreferences();
        String rutaInfo = leerRutaDesdeArchivo();
        llamarApi(apiUrl + "api/ruta/" + rutaInfo);
        Log.d(TAG, "Url de la ruta: " + apiUrl + "api/ruta/" + rutaInfo);

        visitar.setOnClickListener(v -> {
            String userId = String.valueOf(ruta.getUserId());
            Intent intent = new Intent(DetalleRuta.this, MostrarUser.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    private void initViews() {
        tipoMotoTextView = findViewById(R.id.TipoMoto);
        tituloTextView = findViewById(R.id.Titulo);
        fechaTextView = findViewById(R.id.Fecha);
        comunidadTextView = findViewById(R.id.Comunidad);
        descripcionTextView = findViewById(R.id.Decripcion);
        imgView = findViewById(R.id.imgRuta);
        visitar = findViewById(R.id.visitar);
    }

    private void loadPreferences() {
        SharedPreferences sharedPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        token = sharedPref.getString(LOGIN_RESPONSE_KEY, null);

        sharedPref = getSharedPreferences(APP_URL, Context.MODE_PRIVATE);
        apiUrl = sharedPref.getString(URL_KEY, "");
    }

    private void llamarApi(String url) {
        CheckLogin.checkLastLoginDay(getApplicationContext());

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                ruta = UtilJSONParser.parsePostRuta(r.content);
                actualizarVistasConDatosDeRuta();
            }

            @Override
            public void onError(UtilREST.Response r) {
                String errorMsg = (r.content != null) ? r.content : "El contenido de la respuesta es nulo";
                Log.d(TAG, "ERROR: " + errorMsg);
                Toast.makeText(DetalleRuta.this, getResources().getString(R.string.ErrorServidor), Toast.LENGTH_LONG).show();
                startActivity(new Intent(DetalleRuta.this, PantallaInicial.class));
            }
        });
    }

    private void actualizarVistasConDatosDeRuta() {
        if (ruta != null) {
            setSpannableText(tituloTextView, ruta.getTitle(), "");
            setSpannableText(tipoMotoTextView, ruta.getTipoMoto(), getResources().getString(R.string.tipoMoto) + ": ");
            setSpannableText(fechaTextView, ruta.getDate(), getResources().getString(R.string.fecha) + ": ");
            setSpannableText(comunidadTextView, ruta.getComunidad(), getResources().getString(R.string.comAuto) + ": ");
            setSpannableText(descripcionTextView, ruta.getDescription(), getResources().getString(R.string.descripcion) + ": ");

            String base64Image = ruta.getImage();
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if (decodedByte != null) {
                imgView.setImageBitmap(decodedByte);
            } else {
                Log.e(TAG, "La decodificación de la imagen falló.");
            }

            Glide.with(this)
                    .asBitmap()
                    .load(decodedString)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.favicon)
                    .into(imgView);
        }
    }

    private void setSpannableText(TextView textView, String text, String label) {
        SpannableString spannableString = new SpannableString(label + text);
        spannableString.setSpan(new ForegroundColorSpan(DEFAULT_COLOR), 0, label.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
    }

    String leerRutaDesdeArchivo() {
        StringBuilder rutaInfo = new StringBuilder();
        try (FileInputStream fis = openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                rutaInfo.append(linea);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error leyendo el archivo", e);
        }
        Log.d(TAG, "Leído del archivo: " + rutaInfo.toString());
        return rutaInfo.toString();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, RutasList.class));
        finish();
        super.onBackPressed();
    }
}
