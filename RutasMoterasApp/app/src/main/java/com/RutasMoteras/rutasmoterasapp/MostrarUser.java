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
import java.util.List;

public class MostrarUser extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = "MostrarUser";
    private static final String APP_URL = "AppURL";
    private static final String APP_PREFERENCES = "AppPreferences";
    private static final String USER_PREFERENCES = "UserPreferences";
    private static final String LOGIN_RESPONSE_KEY = "LoginResponse";
    private static final String URL_KEY = "URL";

    private String token;
    private RutasAdapter mAdaptadorRutas;
    private ListView miListaRutas;
    private ImageView imagen;
    private TextView nombre;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String apiUrl;
    private TextView RutasMoteras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_user);

        initViews();
        setupListeners();

        SharedPreferences sharedURL = getSharedPreferences(APP_URL, Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString(URL_KEY, "");

        String userId = getIntent().getStringExtra("userId");

        fetchUserRoutes(apiUrl + "api/rutasU/" + userId);
        fetchUserDetails(apiUrl + "api/usuarioI/" + userId);
    }

    private void initViews() {
        RutasMoteras = findViewById(R.id.toolbarTitle2);
        miListaRutas = findViewById(R.id.miListaRutas);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        nombre = findViewById(R.id.NombreUsu);
        imagen = findViewById(R.id.ImgUsuario);
    }

    private void setupListeners() {
        RutasMoteras.setOnClickListener(v -> {
            Intent intent = new Intent(MostrarUser.this, RutasList.class);
            startActivity(intent);
        });

        miListaRutas.setOnItemClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            String userId = getIntent().getStringExtra("userId");
            fetchUserRoutes(apiUrl + "api/rutasU/" + userId);
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void fetchUserRoutes(String url) {
        CheckLogin.checkLastLoginDay(getApplicationContext());

        SharedPreferences sharedPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        token = sharedPref.getString(LOGIN_RESPONSE_KEY, null);

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response response) {
                String jsonContent = response.content;
                List<RutasModel> rutasList = UtilJSONParser.parseArrayRutasPosts(jsonContent);
                Log.d(TAG, "Respuesta: " + response.content);

                runOnUiThread(() -> {
                    if (rutasList != null && !rutasList.isEmpty()) {
                        mAdaptadorRutas = new RutasAdapter(getApplicationContext(), R.layout.rutas_primera_impresion, rutasList);
                        miListaRutas.setAdapter(mAdaptadorRutas);
                    } else {
                        Toast.makeText(MostrarUser.this, getResources().getString(R.string.SinRutas), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(UtilREST.Response response) {
                runOnUiThread(() -> {
                    Toast.makeText(MostrarUser.this, getResources().getString(R.string.ErrorServidor), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MostrarUser.this, Login.class));
                });
            }
        });
    }

    private void fetchUserDetails(String url) {
        CheckLogin.checkLastLoginDay(getApplicationContext());

        SharedPreferences sharedPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        token = sharedPref.getString(LOGIN_RESPONSE_KEY, null);

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response response) {
                String jsonContent = response.content;
                UserModel user = UtilJSONParser.parseUserPosts(jsonContent);
                Log.d(TAG, "Usuario: " + response.content);

                runOnUiThread(() -> {
                    if (user != null) {
                        nombre.setText(user.getName());
                        cargarImagenBase64(user.getImage());
                    } else {
                        Log.e(TAG, "El objeto user es nulo");
                    }
                });
            }

            @Override
            public void onError(UtilREST.Response response) {
                Log.e(TAG, "Error al obtener detalles del usuario");
            }
        });
    }

    private void cargarImagenBase64(String base64Image) {
        try {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Glide.with(this).asBitmap().load(decodedString).into(imagen);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Failed to decode Base64 string", e);
            Glide.with(this).load(R.drawable.userwhothoutphoto).into(imagen);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckLogin.checkLastLoginDay(getApplicationContext());

        RutasModel rutaSeleccionada = (RutasModel) parent.getItemAtPosition(position);
        guardarRutaEnArchivo(rutaSeleccionada);

        startActivity(new Intent(this, DetalleRutaViendoUsuario.class));
    }

    private void guardarRutaEnArchivo(RutasModel ruta) {
        String rutaInfo = String.valueOf(ruta.getId());
        Log.d(TAG, "Id de ruta: " + ruta.getId());

        try (FileOutputStream fos = openFileOutput("ruta_seleccionada2.txt", Context.MODE_PRIVATE)) {
            fos.write(rutaInfo.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "Error al guardar la ruta en archivo", e);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DetalleRuta.class));
        finish();
        super.onBackPressed();
    }
}
