package com.RutasMoteras.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.RutasMoteras.rutasmoterasapi.CheckLogin;
import com.RutasMoteras.rutasmoterasapi.RutasModel;
import com.RutasMoteras.rutasmoterasapi.UserModel;
import com.RutasMoteras.rutasmoterasapi.UtilJSONParser;
import com.RutasMoteras.rutasmoterasapi.UtilREST;
import com.bumptech.glide.Glide;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MostrarUser extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String token;
    private RutasAdapter mAdaptadorRutas;
    private ListView miListaRutas;
    private ImageView imagen;
    private TextView nombre;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedURL;
    String apiUrl;
    TextView RutasMoteras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_user);

        RutasMoteras = findViewById(R.id.toolbarTitle2);
        RutasMoteras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MostrarUser.this, RutasList.class);
                startActivity(intent);
            }
        });

        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        miListaRutas = findViewById(R.id.miListaRutas);
        miListaRutas.setOnItemClickListener(this);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LLamarApi(apiUrl + "api/rutasU/" + userId);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        LLamarApi(apiUrl + "api/rutasU/" + userId);
        ObtenerUsuario(apiUrl + "api/usuarioI/" + userId);
    }

    public void LLamarApi(String url){

        CheckLogin.checkLastLoginDay(getApplicationContext());

        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPref.getString("LoginResponse", null);

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                List<RutasModel> rutasList = UtilJSONParser.parseArrayRutasPosts(jsonContent);
                Log.d("Respuesta: ", r.content);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (rutasList != null && !rutasList.isEmpty()) {
                            mAdaptadorRutas = new RutasAdapter(getApplicationContext(), R.layout.rutas_primera_impresion, rutasList);
                            miListaRutas.setAdapter(mAdaptadorRutas);
                        } else {
                            Toast.makeText(MostrarUser.this, getResources().getString(R.string.SinRutas), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

            @Override
            public void onError(UtilREST.Response r) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MostrarUser.this, getResources().getString(R.string.ErrorServidor),Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MostrarUser.this, Login.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }

    private void cargarImagenBase64(String base64Image) {

        try {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Glide.with(this).asBitmap().load(decodedString).into(imagen);
        } catch (IllegalArgumentException e) {
            Log.e("Base64 Error", "Failed to decode Base64 string", e);
            Glide.with(this).load(R.drawable.userwhothoutphoto).into(imagen);
        }
    }

    public void ObtenerUsuario(String url){

        CheckLogin.checkLastLoginDay(getApplicationContext());

        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPref.getString("LoginResponse", null);

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                UserModel user = UtilJSONParser.parseUserPosts(jsonContent);
                Log.d("Usuario: ", r.content);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        nombre = findViewById(R.id.NombreUsu);
                        imagen = findViewById(R.id.ImgUsuario);

                        if (user != null) {
                            nombre.setText(user.getName());
                            cargarImagenBase64(user.getImage());
                        } else {
                            Log.e("MostrarUser", "El objeto user es nulo");
                        }
                    }
                });
            }

            @Override
            public void onError(UtilREST.Response r) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        CheckLogin.checkLastLoginDay(getApplicationContext());

        RutasModel rutaSeleccionada = (RutasModel) parent.getItemAtPosition(position);

        guardarRutaEnArchivo(rutaSeleccionada);

        Intent intent = new Intent(MostrarUser.this, DetalleRuta.class);
        startActivity(intent);
    }

    private void guardarRutaEnArchivo(RutasModel ruta) {

        String rutaInfo = String.valueOf(ruta.getId());
        Log.d("Id de ruta: ", String.valueOf(ruta.getId()));

        try {
            FileOutputStream fos = openFileOutput("ruta_seleccionada.txt", Context.MODE_PRIVATE);
            fos.write(rutaInfo.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
