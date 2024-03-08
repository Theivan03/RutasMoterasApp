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
import android.widget.ListView;
import android.widget.Toast;


import com.example.rutasmoterasapi.RutasModel;
import com.example.rutasmoterasapi.UtilJSONParser;
import com.example.rutasmoterasapi.UtilREST;

import java.util.List;

public class RutasList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    RutasAdapter mAdaptadorRutas;
    ListView miListaRutas;
    String token;
    long tokenTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas_list);

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

        /*if (System.currentTimeMillis() - tokenTime > 3600000) {

        } else {
            Intent intent = new Intent(RutasList.this, Login.class);
            startActivity(intent);
        }*/
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

        if(id == R.id.Andalucia){


        }if(id == R.id.Aragon){


        }if(id == R.id.Asturias){


        }if(id == R.id.Cantabria){


        }if(id == R.id.CastillaLaMancha){


        }if(id == R.id.CastillaLeon){


        }if(id == R.id.Cataluña){


        }if(id == R.id.Extremadura){


        }if(id == R.id.Galicia){


        }if(id == R.id.IslasBaleares){


        }if(id == R.id.IslasCanarias){


        }if(id == R.id.LaRioja){


        }if(id == R.id.Madrid){


        }if(id == R.id.Murcia){


        }if(id == R.id.Navarra){


        }if(id == R.id.PasiVasco){


        }if(id == R.id.ComunidadValenciana){


        }if(id == R.id.Ceuta){


        }if(id == R.id.Melilla){


        }

        if(id == R.id.Scooter){


        }if(id == R.id.Custom){


        }if(id == R.id.Trail){


        }if(id == R.id.Deportiva){


        }if(id == R.id.Naked){


        }if(id == R.id.Motocross){


        }if(id == R.id.GranTurismo){


        }


        return super.onOptionsItemSelected(item);
    }

    public void LLamarApi(String url){

        if (System.currentTimeMillis() - tokenTime > 3600000) {
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
        } else {
            Intent intent = new Intent(RutasList.this, Login.class);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}