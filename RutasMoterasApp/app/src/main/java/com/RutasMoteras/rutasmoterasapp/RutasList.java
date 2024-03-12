package com.RutasMoteras.rutasmoterasapp;

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
import com.RutasMoteras.rutasmoterasapi.RutasModel;
import com.RutasMoteras.rutasmoterasapi.UtilJSONParser;
import com.RutasMoteras.rutasmoterasapi.UtilREST;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RutasList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    RutasAdapter mAdaptadorRutas;
    ListView miListaRutas;
    String token;
    long tokenTime;
    ImageView imgUsu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas_list);

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

        LLamarApi("http://192.168.1.131:5000/api/rutas");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        SharedPreferences userPrefs = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        if (userPrefs.contains("Id")) {
            getMenuInflater().inflate(R.menu.mi_menu_usuario, menu);
            return true;
        } else {
            getMenuInflater().inflate(R.menu.mi_menu, menu);
            return true;
        }
    }

    // Menú AcercaDe, MasInfo y NuevaPeli
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();


        if(id == R.id.GuiaUsuario){
            Intent intent = new Intent(RutasList.this, Informacion.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.AddRuta){
            Intent intent = new Intent(RutasList.this, Informacion.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.MasInfo){
            Intent intent = new Intent(RutasList.this, Informacion.class);
            startActivity(intent);
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

        // Guardar la ruta en un archivo de texto
        guardarRutaEnArchivo(rutaSeleccionada);

        // Ir a otra pantalla
        Intent intent = new Intent(RutasList.this, DetalleRuta.class);
        startActivity(intent);
    }

    private void guardarRutaEnArchivo(RutasModel ruta) {
        // Crear una cadena con la información de la ruta
        String rutaInfo = getResources().getString(R.string.tipoMoto) + ": " + ruta.getTipoMoto() + "\n"
                + ruta.getTitle() + "\n"
                + "Fecha: " + ruta.getDate() + "\n"
                + getResources().getString(R.string.comAuto) + ": " + ruta.getComunidad() + "\n"
                + "Descripcion: " + ruta.getDescription() + "\n"
                + ruta.getImage() + "\n"
                + ruta.getUserId();

        // Guardar la cadena en un archivo de texto
        try {
            FileOutputStream fos = openFileOutput("ruta_seleccionada.txt", Context.MODE_PRIVATE);
            fos.write(rutaInfo.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}