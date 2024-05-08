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

public class SuperUser extends AppCompatActivity implements AdapterView.OnItemClickListener{

    RutasAdapter mAdaptadorRutas;
    ListView miListaRutas;
    String token;
    long tokenTime;
    List<RutasModel> rutasList;
    SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler = new Handler(Looper.getMainLooper());
    SharedPreferences sharedURL;
    String apiUrl;
    private Toast customToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_user);

        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPref.getString("LoginResponse", null);
        tokenTime = sharedPref.getLong("TokenTimestamp", 0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> openOptionsMenu());

        miListaRutas = findViewById(R.id.miListaRutas);
        miListaRutas.setOnItemClickListener(this);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LLamarApi(apiUrl + "api/rutas");
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        registerForContextMenu(miListaRutas);

        LLamarApi(apiUrl + "api/rutas");
        //cargarDatosDeLaApi();
    }

    private void cargarDatosDeLaApi() {
        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String jsonRutas = sharedPref.getString("datosRutas", null);

        if (jsonRutas != null && !jsonRutas.isEmpty()) {
            try {
                List<RutasModel> rutasList = UtilJSONParser.parseArrayRutasPosts(jsonRutas);
                mAdaptadorRutas = new RutasAdapter(getApplicationContext(), R.layout.rutas_primera_impresion, rutasList);
                miListaRutas.setAdapter(mAdaptadorRutas);
                mAdaptadorRutas.notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(this, getResources().getString(R.string.ErrorServidor), Toast.LENGTH_SHORT).show();
                Log.e("RutasList", "Error al parsear los datos de las rutas", e);
            }
        } else {
            // Llama a la API si no hay datos en SharedPreferences o los datos son inválidos

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_super_user, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if (id == R.id.crear) {
            LLamarApi(apiUrl + "api/rutas");
        }

        if (id == R.id.cerrar) {
            SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("TokenDay", -1);
            editor.apply();

            sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.clear();
            editor.apply();

            sharedPref = getSharedPreferences("LogPreferences", Context.MODE_PRIVATE);
            editor = sharedPref.edit();
            editor.putBoolean("Log", false);
            editor.apply();

            Intent intent = new Intent(SuperUser.this, PantallaInicial.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        getMenuInflater().inflate(R.menu.menu_contextual, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        RutasModel r = (RutasModel) miListaRutas.getItemAtPosition(info.position);

        int id = item.getItemId();
        switch(id){
            case R.id.eliminar:
                mostrarDialogo(r);

                break;
            case R.id.editar:
                Intent intent = new Intent(SuperUser.this, EditRuta.class);
                intent.putExtra("FILM_POSITION", info.position);
                if (intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent, r.getId());
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void mostrarDialogo(RutasModel ruta) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.vasABorrarRuta));
        builder.setMessage(getResources().getString(R.string.EstasSeguroDeBorrarRuta));

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (rutasList != null && !rutasList.isEmpty()) {
                    int index = rutasList.indexOf(ruta);
                    if (index != -1) {
                        rutasList.remove(index);
                        mAdaptadorRutas.notifyDataSetChanged();

                        UtilREST.runQueryWithHeaders(UtilREST.QueryType.DELETE, apiUrl + "api/ruta/" + ruta.getId(), token, new UtilREST.OnResponseListener() {
                            @Override
                            public void onSuccess(UtilREST.Response r) {
                                // Manejo del éxito
                            }

                            @Override
                            public void onError(UtilREST.Response r) {
                                if (r.content != null) {
                                    Log.e("Delete Error", r.content);
                                } else {
                                    Log.e("Delete Error", "Error data is null");
                                }
                            }
                        });
                    } else {
                        Log.e("Delete Error", "Ruta no encontrada en la lista");
                    }
                } else {
                    Log.e("Error", "La lista de rutas está vacía o no inicializada");
                }
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hacer nada
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void LLamarApi(String url){

        showCustomToast();

        CheckLogin.checkLastLoginDay(getApplicationContext());

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                rutasList = UtilJSONParser.parseArrayRutasPosts(jsonContent);

                hideCustomToast();

                mAdaptadorRutas = new RutasAdapter(getApplicationContext(), R.layout.rutas_primera_impresion, rutasList);
                miListaRutas.setAdapter(mAdaptadorRutas);
                mAdaptadorRutas.notifyDataSetChanged();

            }

            @Override
            public void onError(UtilREST.Response r) {
                if (r.content != null) {
                    Log.d("ERROR!!!!!!!!!", r.content);
                } else {
                    Log.d("ERROR!!!!!!!!!", "El contenido de la respuesta es nulo");
                }
                Toast.makeText(SuperUser.this, getResources().getString(R.string.ErrorServidor), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(SuperUser.this, PantallaInicial.class);
                startActivity(intent);

                hideCustomToast();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        CheckLogin.checkLastLoginDay(getApplicationContext());

        RutasModel rutaSeleccionada = (RutasModel) parent.getItemAtPosition(position);

        guardarRutaEnArchivo(rutaSeleccionada);

        Intent intent = new Intent(SuperUser.this, DetalleRuta2.class);
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

    public void showCustomToast() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.custom_toast_container));

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
        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("TokenDay", -1);
        editor.apply();

        sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.clear();
        editor.apply();

        sharedPref = getSharedPreferences("LogPreferences", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putBoolean("Log", false);
        editor.apply();

        Intent intent = new Intent(this, PantallaInicial.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}