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
import android.widget.TextView;
import android.widget.Toast;

import com.RutasMoteras.rutasmoterasapi.RutasModel;
import com.RutasMoteras.rutasmoterasapi.UtilJSONParser;
import com.RutasMoteras.rutasmoterasapi.UtilREST;
import com.bumptech.glide.Glide;

import java.util.List;

public class User extends AppCompatActivity implements AdapterView.OnItemClickListener {

    TextView nombre;
    TextView apellidos;
    TextView correo;
    String token;
    Long tokenTime;
    RutasAdapter mAdaptadorRutas;
    ListView miListaRutas;
    ImageView imgUsu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        SharedPreferences sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        Long id = sharedPref.getLong("Id", -1);

        nombre = findViewById(R.id.nombreUsuario);
        nombre.setText(sharedPref.getString("Name", ""));

        apellidos = findViewById(R.id.Apellidos);
        apellidos.setText(sharedPref.getString("Surname", ""));

        correo = findViewById(R.id.Correo);
        correo.setText(sharedPref.getString("Email", ""));

        imgUsu = findViewById(R.id.userImageView);
        String imageUrl;

        Log.d("Imagen: ", sharedPref.getString("Foto", ""));
        if(sharedPref.contains("Foto")){
            if(sharedPref.getString("Foto", "") == "" || sharedPref.getString("Foto", "") == null || sharedPref.getString("Foto", "").isEmpty()){
                imageUrl = "https://drive.google.com/uc?id=1veQeZEa0_E17VSfY64cVGnMlUKgboNiq";
            }
            else{
                imageUrl = sharedPref.getString("Foto", "");
            }
        }
        else{
            imageUrl = "https://drive.google.com/uc?id=1veQeZEa0_E17VSfY64cVGnMlUKgboNiq";
        }

        Glide.with(this)
                .load(imageUrl)
                .into(imgUsu);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Omitir el título predeterminado para usar el TextView personalizado
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Configura el botón de menú para abrir el menú cuando se haga clic
        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> openOptionsMenu());

        miListaRutas = findViewById(R.id.miListaRutas);
        miListaRutas.setOnItemClickListener(this);

        LLamarApi("http://192.168.1.131:5000/api/rutasU/" + id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.menu_usu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id = item.getItemId();


        if(id == R.id.Editar){
            Intent intent = new Intent(User.this, Informacion.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.Logout){
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

            Intent intent = new Intent(User.this, PantallaInicial.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.Contacta){
            Intent intent = new Intent(User.this, Informacion.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void LLamarApi(String url){

        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPref.getString("LoginResponse", null);
        tokenTime = sharedPref.getLong("TokenTimestamp", 0);

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                List<RutasModel> rutasList = UtilJSONParser.parseArrayRutasPosts(jsonContent);
                Log.d("Respuesta: ", r.content);

                if (rutasList != null && !rutasList.isEmpty()) {
                    mAdaptadorRutas = new RutasAdapter(getApplicationContext(), R.layout.rutas_primera_impresion, rutasList);
                    miListaRutas.setAdapter(mAdaptadorRutas);
                } else {
                    Toast.makeText(User.this, getResources().getString(R.string.SinRutas), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(UtilREST.Response r) {
                Toast.makeText(User.this, getResources().getString(R.string.ErrorServidor),Toast.LENGTH_LONG).show();

                Intent intent = new Intent(User.this, Login.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}