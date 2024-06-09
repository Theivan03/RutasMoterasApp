package com.RutasMoteras.rutasmoterasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.RutasMoteras.rutasmoterasapi.CheckLogin;
import com.RutasMoteras.rutasmoterasapi.RutasModel;
import com.RutasMoteras.rutasmoterasapi.UtilJSONParser;
import com.RutasMoteras.rutasmoterasapi.UtilREST;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SuperUser extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String APP_PREFERENCES_KEY = "AppPreferences";
    private static final String USER_PREFERENCES_KEY = "UserPreferences";
    private static final String LOG_PREFERENCES_KEY = "LogPreferences";
    private static final String LOGIN_RESPONSE_KEY = "LoginResponse";
    private static final String TOKEN_TIMESTAMP_KEY = "TokenTimestamp";

    private RutasAdapter mAdaptadorRutas;
    private ListView miListaRutas;
    private String token;
    private long tokenTime;
    private List<RutasModel> rutasList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private SharedPreferences sharedURL;
    private String apiUrl;
    private Toast customToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user);

        initializePreferences();
        initializeViews();
        setupToolbar();
        setupListView();
        setupSwipeRefresh();

        LLamarApi(apiUrl + "api/rutas");
    }

    private void initializePreferences() {
        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        SharedPreferences sharedPref = getSharedPreferences(APP_PREFERENCES_KEY, Context.MODE_PRIVATE);
        token = sharedPref.getString(LOGIN_RESPONSE_KEY, null);
        tokenTime = sharedPref.getLong(TOKEN_TIMESTAMP_KEY, 0);
    }

    private void initializeViews() {
        miListaRutas = findViewById(R.id.miListaRutas);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> openOptionsMenu());
    }

    private void setupListView() {
        miListaRutas.setOnItemClickListener(this);
        registerForContextMenu(miListaRutas);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            LLamarApi(apiUrl + "api/rutas");
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_super_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.crear) {
            startActivity(new Intent(SuperUser.this, SuperUserCreateNewUser.class));
            return true;
        }

        if (id == R.id.cerrar) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        clearSharedPreferences(APP_PREFERENCES_KEY);
        clearSharedPreferences(USER_PREFERENCES_KEY);
        clearSharedPreferences(LOG_PREFERENCES_KEY);
        startActivity(new Intent(SuperUser.this, PantallaInicial.class));
    }

    private void clearSharedPreferences(String preferencesKey) {
        SharedPreferences sharedPref = getSharedPreferences(preferencesKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_contextual, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        RutasModel ruta = (RutasModel) miListaRutas.getItemAtPosition(info.position);

        if (item.getItemId() == R.id.eliminar) {
            mostrarDialogo(ruta);
            return true;
        } else if (item.getItemId() == R.id.editar) {
            guardarRutaEnArchivo(ruta);
            Intent intent = new Intent(SuperUser.this, EditRuta.class);
            startActivity(intent);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    private void mostrarDialogo(RutasModel ruta) {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.vasABorrarRuta))
                .setMessage(getResources().getString(R.string.EstasSeguroDeBorrarRuta))
                .setPositiveButton("Sí", (dialog, which) -> eliminarRuta(ruta))
                .setNegativeButton("No", null)
                .create()
                .show();
    }

    private void eliminarRuta(RutasModel ruta) {
        if (rutasList != null) {
            int index = rutasList.indexOf(ruta);
            if (index != -1) {
                rutasList.remove(index);
                mAdaptadorRutas.notifyDataSetChanged();
                eliminarRutaDeApi(ruta);
            } else {
                Log.e("Delete Error", "Ruta no encontrada en la lista");
            }
        } else {
            Log.e("Delete Error", "La lista de rutas está vacía o no inicializada");
        }
    }

    private void eliminarRutaDeApi(RutasModel ruta) {
        UtilREST.runQueryWithHeaders(UtilREST.QueryType.DELETE, apiUrl + "api/ruta/" + ruta.getId(), token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                // Manejo del éxito
            }

            @Override
            public void onError(UtilREST.Response r) {
                Log.e("Delete Error", r.content != null ? r.content : "Error data is null");
            }
        });
    }

    public void LLamarApi(String url) {
        showCustomToast();

        CheckLogin.checkLastLoginDay(getApplicationContext());

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                rutasList = UtilJSONParser.parseArrayRutasPosts(r.content);
                hideCustomToast();
                actualizarListaRutas(rutasList);
            }

            @Override
            public void onError(UtilREST.Response r) {
                Log.e("ERROR!!!!!!!!!", r.content != null ? r.content : "El contenido de la respuesta es nulo");
                Toast.makeText(SuperUser.this, getResources().getString(R.string.ErrorServidor), Toast.LENGTH_LONG).show();
                startActivity(new Intent(SuperUser.this, PantallaInicial.class));
                hideCustomToast();
            }
        });
    }

    private void actualizarListaRutas(List<RutasModel> rutasList) {
        mAdaptadorRutas = new RutasAdapter(getApplicationContext(), R.layout.rutas_primera_impresion, rutasList);
        miListaRutas.setAdapter(mAdaptadorRutas);
        mAdaptadorRutas.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RutasModel rutaSeleccionada = (RutasModel) parent.getItemAtPosition(position);
        guardarRutaEnArchivo(rutaSeleccionada);
        startActivity(new Intent(SuperUser.this, DetalleRuta2.class));
    }

    private void guardarRutaEnArchivo(RutasModel ruta) {
        try (FileOutputStream fos = openFileOutput("ruta_seleccionada.txt", Context.MODE_PRIVATE)) {
            fos.write(String.valueOf(ruta.getId()).getBytes());
        } catch (IOException e) {
            Log.e("File Error", "Error writing to file", e);
        }
    }

    public void showCustomToast() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.custom_toast_container));

        customToast = new Toast(getApplicationContext());
        customToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        customToast.setDuration(Toast.LENGTH_LONG);
        customToast.setView(layout);
        customToast.show();
    }

    public void hideCustomToast() {
        if (customToast != null) {
            customToast.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        logout();
        super.onBackPressed();
    }
}
