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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas_list);

        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        SharedPreferences userPrefs = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        imgUsu = findViewById(R.id.userImageView);
        String imageUrl;



        if(userPrefs.contains("Foto")){
            if(userPrefs.getString("Foto", "") == "" || userPrefs.getString("Foto", "") == null || userPrefs.getString("Foto", "").isEmpty()){
                imageUrl = "https://drive.google.com/uc?id=1veQeZEa0_E17VSfY64cVGnMlUKgboNiq";
            }
            else{
                imageUrl = userPrefs.getString("Foto", "");
            }
        }
        else{
            imageUrl = "https://drive.google.com/uc?id=1veQeZEa0_E17VSfY64cVGnMlUKgboNiq";
        }


        Glide.with(this)
                .load(imageUrl)
                .into(imgUsu);


        if (userPrefs.contains("Id")) {
            imgUsu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RutasList.this, User.class);
                    startActivity(intent);
                }
            });
        } else {
            // No hay nada en UserPreferences, por lo que no permitimos clics en la imagen
            imgUsu.setClickable(false);
        }


        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPref.getString("LoginResponse", null);
        tokenTime = sharedPref.getLong("TokenTimestamp", 0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Omitir el título predeterminado para usar el TextView personalizado
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Configura el botón de menú para abrir el menú cuando se haga clic
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


    // Menú AcercaDe, MasInfo y NuevaPeli
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();

        if(id == R.id.BorrarFiltro){
            LLamarApi(apiUrl + "api/rutas");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    borrarFiltroItem.setVisible(false);
                }
            }, 1000);
        }

        if(id == R.id.IniciarSesion){
            Intent intent = new Intent(RutasList.this, Login.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.GuiaUsuario){
            Intent intent = new Intent(RutasList.this, Informacion.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.AddRuta){
            Intent intent = new Intent(RutasList.this, CrearRuta.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.Contacta){
            Intent intent = new Intent(RutasList.this, ContactWithUs.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.MasInfo){
            Intent intent = new Intent(RutasList.this, Informacion.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.Nuevos){

            LLamarApi(apiUrl + "api/rutasF");
            SetVisibleTrueBorrarFiltro();

        }

        if(id == R.id.Andalucia){

            LLamarApi(apiUrl + "api/rutasC/Andalucia");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Aragon){

            LLamarApi(apiUrl + "api/rutasC/Aragon");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Asturias){

            LLamarApi(apiUrl + "api/rutasC/Asturias");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Cantabria){

            LLamarApi(apiUrl + "api/rutasC/Cantabria");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.CastillaLaMancha){

            LLamarApi(apiUrl + "api/rutasC/CastillaLaMancha");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.CastillaLeon){

            LLamarApi(apiUrl + "api/rutasC/CastillaLeon");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Cataluña){

            LLamarApi(apiUrl + "api/rutasC/Cataluña");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Extremadura){

            LLamarApi(apiUrl + "api/rutasC/Extremadura");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Galicia){

            LLamarApi(apiUrl + "api/rutasC/Galicia");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.IslasBaleares){

            LLamarApi(apiUrl + "api/rutasC/IslasBaleares");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.IslasCanarias){

            LLamarApi(apiUrl + "api/rutasC/IslasCanarias");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.LaRioja){

            LLamarApi(apiUrl + "api/rutasC/LaRioja");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Madrid){

            LLamarApi(apiUrl + "api/rutasC/Madrid");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Murcia){

            LLamarApi(apiUrl + "api/rutasC/Murcia");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Navarra){

            LLamarApi(apiUrl + "api/rutasC/Navarra");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.PasiVasco){

            LLamarApi(apiUrl + "api/rutasC/Pais Vasco");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.ComunidadValenciana){

            LLamarApi(apiUrl + "api/rutasC/Comunidad Valenciana");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Ceuta){

            LLamarApi(apiUrl + "api/rutasC/Ceuta");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Melilla){

            LLamarApi(apiUrl + "api/rutasC/Melilla");
            SetVisibleTrueBorrarFiltro();

        }

        if(id == R.id.Scooter){

            LLamarApi(apiUrl + "api/rutasT/Scooter");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Custom){

            LLamarApi(apiUrl + "api/rutasT/Custom");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Trail){

            LLamarApi(apiUrl + "api/rutasT/Trail");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Deportiva){

            LLamarApi(apiUrl + "api/rutasT/Deportiva");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Naked){

            LLamarApi(apiUrl + "api/rutasT/Naked");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.Motocross){

            LLamarApi(apiUrl + "api/rutasT/Motocross");
            SetVisibleTrueBorrarFiltro();

        }if(id == R.id.GranTurismo){

            LLamarApi(apiUrl + "api/rutasT/GranTurismo");
            SetVisibleTrueBorrarFiltro();

        }


        return super.onOptionsItemSelected(item);
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
        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                List<RutasModel> rutasList = UtilJSONParser.parseArrayRutasPosts(jsonContent);

                mAdaptadorRutas = new RutasAdapter(getApplicationContext(), R.layout.rutas_primera_impresion, rutasList);
                miListaRutas.setAdapter(mAdaptadorRutas);
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
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RutasModel rutaSeleccionada = (RutasModel) parent.getItemAtPosition(position);

        guardarRutaEnArchivo(rutaSeleccionada);

        Intent intent = new Intent(RutasList.this, DetalleRuta.class);
        startActivity(intent);
    }

    private void guardarRutaEnArchivo(RutasModel ruta) {
        String rutaInfo = getResources().getString(R.string.tipoMoto) + ": " + ruta.getTipoMoto() + "\n"
                + ruta.getTitle() + "\n"
                + "Fecha: " + ruta.getDate() + "\n"
                + getResources().getString(R.string.comAuto) + ": " + ruta.getComunidad() + "\n"
                + "Descripcion: " + ruta.getDescription() + "\n"
                + ruta.getImage() + "\n"
                + ruta.getUserId();

        try {
            FileOutputStream fos = openFileOutput("ruta_seleccionada.txt", Context.MODE_PRIVATE);
            fos.write(rutaInfo.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}