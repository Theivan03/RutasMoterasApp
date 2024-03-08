package com.example.rutasmoterasapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.rutasmoterasapi.RutasModel;
import com.example.rutasmoterasapi.UtilJSONParser;
import com.example.rutasmoterasapi.UtilREST;

import java.util.ArrayList;
import java.util.List;

public class RutasList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    RutasAdapter mAdaptadorRutas;
    ListView miListaRutas;
    String token;
    long tokenTime;
    List<RutasModel> rutasList;
    ImageView imgUsu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas_list);

        imgUsu = findViewById(R.id.userImageView);
        String imageUrl = "https://drive.google.com/uc?id=1veQeZEa0_E17VSfY64cVGnMlUKgboNiq"; // URL directa de descarga de la imagen

        Glide.with(this)
                .load(imageUrl)
                .into(imgUsu);

        List<RutasModel> rutasList = new ArrayList<>();

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

        ObtenerRutasApi();



    }

    public void ObtenerRutasApi(){

        LLamarApi("http://192.168.1.131:5000/api/rutas");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.mi_menu, menu);
        return true;
    }

    // Menú AcercaDe, MasInfo y NuevaPeli
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();


        if(id == R.id.MasInfo){
            Intent intent = new Intent(RutasList.this, Informacion.class);
            if (intent.resolveActivity(getPackageManager()) != null){
                startActivity(intent);
            }
            return true;
        }

        if(id == R.id.Nuevos){

            LLamarApi("http://192.168.1.131:5000/api/rutasF");

        }

        if(id == R.id.Andalucia){

            LLamarApi("http://192.168.1.131:5000/api/rutasC/Andalucia");

        }if(id == R.id.Aragon){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.Asturias){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.Cantabria){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.CastillaLaMancha){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.CastillaLeon){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.Cataluña){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.Extremadura){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.Galicia){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.IslasBaleares){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.IslasCanarias){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.LaRioja){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.Madrid){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.Murcia){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.Navarra){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.PasiVasco){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.ComunidadValenciana){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.Ceuta){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }if(id == R.id.Melilla){

            LLamarApi("http://192.168.1.131:5000/api/rutasC");

        }

        if(id == R.id.Scooter){

            LLamarApi("http://192.168.1.131:5000/api/rutasT");

        }if(id == R.id.Custom){

            LLamarApi("http://192.168.1.131:5000/api/rutasT");

        }if(id == R.id.Trail){

            LLamarApi("http://192.168.1.131:5000/api/rutasT");

        }if(id == R.id.Deportiva){

            LLamarApi("http://192.168.1.131:5000/api/rutasT");

        }if(id == R.id.Naked){

            LLamarApi("http://192.168.1.131:5000/api/rutasT");

        }if(id == R.id.Motocross){

            LLamarApi("http://192.168.1.131:5000/api/rutasT");

        }if(id == R.id.GranTurismo){

            LLamarApi("http://192.168.1.131:5000/api/rutasT");

        }


        return super.onOptionsItemSelected(item);
    }

    public void LLamarApi(String url){

        List<RutasModel> rutasList = null;
        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                List<RutasModel> rutasList = UtilJSONParser.parseArrayPosts(jsonContent);

                mAdaptadorRutas = new RutasAdapter(getApplicationContext(), R.layout.rutas_primera_impresion, rutasList);
                miListaRutas.setAdapter(mAdaptadorRutas);

            }

            @Override
            public void onError(UtilREST.Response r) {
                Toast.makeText(RutasList.this,"Ha habido un problema con el servidor,\n sentimos las molestias",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(RutasList.this, Login.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}