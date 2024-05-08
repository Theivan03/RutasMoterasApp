package com.RutasMoteras.rutasmoterasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


import com.RutasMoteras.rutasmoterasapi.CheckLogin;
import com.bumptech.glide.Glide;
import com.RutasMoteras.rutasmoterasapi.RutasModel;
import com.RutasMoteras.rutasmoterasapi.UtilJSONParser;
import com.RutasMoteras.rutasmoterasapi.UtilREST;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class RutasList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    RutasAdapter mAdaptadorRutas;
    ListView miListaRutas;
    String token;
    long tokenTime;
    ImageView imgUsu;
    SwipeRefreshLayout swipeRefreshLayout;
    MenuItem borrarFiltroItem;
    private Handler handler = new Handler(Looper.getMainLooper());
    SharedPreferences sharedURL;
    String apiUrl;
    private Toast customToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas_list);

        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        SharedPreferences userPrefs = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        String foto = userPrefs.getString("Foto", null);

        imgUsu = findViewById(R.id.userImageView);
        String imageUrl;

        if(userPrefs.contains("Foto")){
            if(foto != null && !foto.isEmpty())
                cargarImagenBase64(foto);
            else{
                imageUrl = "https://drive.google.com/uc?id=1veQeZEa0_E17VSfY64cVGnMlUKgboNiq";
                Glide.with(this)
                        .load(imageUrl)
                        .into(imgUsu);
            }
        }
        else{
            imageUrl = "https://drive.google.com/uc?id=1veQeZEa0_E17VSfY64cVGnMlUKgboNiq";
            Glide.with(this)
                    .load(imageUrl)
                    .into(imgUsu);
        }


        if (userPrefs.contains("Id")) {
            imgUsu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RutasList.this, User.class);
                    startActivity(intent);
                }
            });
        } else {
            imgUsu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RutasList.this, Login.class);
                    startActivity(intent);
                }
            });
        }


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
        SharedPreferences userPrefs = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        if (userPrefs.contains("Id")) {
            getMenuInflater().inflate(R.menu.mi_menu_usuario, menu);
        } else {
            getMenuInflater().inflate(R.menu.mi_menu, menu);
        }

        borrarFiltroItem = menu.findItem(R.id.BorrarFiltro);
        if (borrarFiltroItem != null) {
            borrarFiltroItem.setVisible(false);
        } else {
            Log.e("Error", "El elemento del menú con ID R.id.BorrarFiltro no se encontró");
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if (id == R.id.IniciarSesion || id == R.id.GuiaUsuario || id == R.id.AddRuta ||
                id == R.id.Contacta || id == R.id.MasInfo || id == R.id.ContactaInvitado) {
            Intent intent = null;
            switch (id) {
                case R.id.IniciarSesion:
                    intent = new Intent(this, Login.class);
                    break;
                case R.id.GuiaUsuario:
                    intent = new Intent(this, Informacion.class);
                    break;
                case R.id.MasInfo:
                    intent = new Intent(this, Informacion.class);
                    break;
                case R.id.AddRuta:
                    intent = new Intent(this, CrearRuta.class);
                    break;
                case R.id.Contacta:
                    intent = new Intent(this, ContactWithUs.class);
                    break;
                case R.id.ContactaInvitado:
                    intent = new Intent(this, ContactWithUsInvitado.class);
                    break;
            }
            startActivity(intent);
            return true;
        }

        if (id == R.id.BorrarFiltro) {
            LLamarApi(apiUrl + "api/rutas");
            SetVisibleTrueBorrarFiltro(false);
        } else {
            handleFilterMenuItem(id);
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleFilterMenuItem(int itemId) {
        String path = "";
        switch (itemId) {
            case R.id.Nuevos:
                path = "api/rutasF";
                break;
            case R.id.Andalucia:
                path = "api/rutasC/Andalucia";
                break;
            case R.id.Aragon:
                path = "api/rutasC/Aragon";
                break;
            case R.id.Asturias:
                path = "api/rutasC/Asturias";
                break;
            case R.id.Cantabria:
                path = "api/rutasC/Cantabria";
                break;
            case R.id.CastillaLaMancha:
                path = "api/rutasC/CastillaLaMancha";
                break;
            case R.id.CastillaLeon:
                path = "api/rutasC/CastillaLeon";
                break;
            case R.id.Cataluña:
                path = "api/rutasC/Cataluña";
                break;
            case R.id.Extremadura:
                path = "api/rutasC/Extremadura";
                break;
            case R.id.Galicia:
                path = "api/rutasC/Galicia";
                break;
            case R.id.IslasBaleares:
                path = "api/rutasC/IslasBaleares";
                break;
            case R.id.IslasCanarias:
                path = "api/rutasC/IslasCanarias";
                break;
            case R.id.LaRioja:
                path = "api/rutasC/LaRioja";
                break;
            case R.id.Madrid:
                path = "api/rutasC/Madrid";
                break;
            case R.id.Murcia:
                path = "api/rutasC/Murcia";
                break;
            case R.id.Navarra:
                path = "api/rutasC/Navarra";
                break;
            case R.id.PaisVasco:
                path = "api/rutasC/Pais Vasco";
                break;
            case R.id.ComunidadValenciana:
                path = "api/rutasC/Comunidad Valenciana";
                break;
            case R.id.Ceuta:
                path = "api/rutasC/Ceuta";
                break;
            case R.id.Melilla:
                path = "api/rutasC/Melilla";
                break;
            case R.id.Scooter:
                path = "api/rutasT/Scooter";
                break;
            case R.id.Custom:
                path = "api/rutasT/Custom";
                break;
            case R.id.Trail:
                path = "api/rutasT/Trail";
                break;
            case R.id.Deportiva:
                path = "api/rutasT/Deportiva";
                break;
            case R.id.Naked:
                path = "api/rutasT/Naked";
                break;
            case R.id.Motocross:
                path = "api/rutasT/Motocross";
                break;
            case R.id.GranTurismo:
                path = "api/rutasT/GranTurismo";
                break;
            default:
                Log.e("Menu Selection", "No path found for item ID: " + itemId);
                return;
        }
        LLamarApi(apiUrl + path);
        SetVisibleTrueBorrarFiltro(true);
    }

    private void SetVisibleTrueBorrarFiltro(boolean visible) {
        handler.postDelayed(() -> borrarFiltroItem.setVisible(visible), 1000);
    }

    public void SetVisibleTrueBorrarFiltro(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                borrarFiltroItem.setVisible(true);
            }
        }, 1000);
    }

    public void LLamarApi(String url){

        showCustomToast();

        CheckLogin.checkLastLoginDay(getApplicationContext());

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                List<RutasModel> rutasList = UtilJSONParser.parseArrayRutasPosts(jsonContent);

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
                Toast.makeText(RutasList.this, getResources().getString(R.string.ErrorServidor), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(RutasList.this, PantallaInicial.class);
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

        Intent intent = new Intent(RutasList.this, DetalleRuta.class);
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

    private void cargarImagenBase64(String base64Image) {

        try {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Glide.with(this).asBitmap().load(decodedString).into(imgUsu);
        } catch (IllegalArgumentException e) {
            Log.e("Base64 Error", "Failed to decode Base64 string", e);
            Glide.with(this).load(R.drawable.userwhothoutphoto).into(imgUsu);
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