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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.RutasMoteras.rutasmoterasapi.CheckLogin;
import com.RutasMoteras.rutasmoterasapi.RutasModel;
import com.RutasMoteras.rutasmoterasapi.UtilJSONParser;
import com.RutasMoteras.rutasmoterasapi.UtilREST;
import com.bumptech.glide.Glide;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
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
    List<RutasModel> rutasList;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedURL;
    String apiUrl;
    TextView RutasMoteras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        RutasMoteras = findViewById(R.id.toolbarTitle2);
        RutasMoteras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(User.this, RutasList.class);
                startActivity(intent);
            }
        });

        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        SharedPreferences sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        Long id = sharedPref.getLong("Id", -1);

        nombre = findViewById(R.id.nombreUsuario);
        nombre.setText(sharedPref.getString("Name", ""));

        apellidos = findViewById(R.id.Apellidos);
        apellidos.setText(sharedPref.getString("Surname", ""));

        correo = findViewById(R.id.Correo);
        correo.setText(sharedPref.getString("Email", ""));

        String foto = sharedPref.getString("Foto", null);

        imgUsu = findViewById(R.id.userImageView);
        String imageUrl;

        Log.d("Foto de usuario:", foto);

        if(sharedPref.contains("Foto")){
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


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        miListaRutas = findViewById(R.id.miListaRutas);
        miListaRutas.setOnItemClickListener(this);

        registerForContextMenu(miListaRutas);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LLamarApi(apiUrl + "api/rutas");
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        Log.d("Ruta de la api:", apiUrl + "api/rutasU/" + id);

        LLamarApi(apiUrl + "api/rutasU/" + id);
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
            Intent intent = new Intent(User.this, EditInfoUser.class);
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



        return super.onOptionsItemSelected(item);
    }

    public void LLamarApi(String url){

        CheckLogin.checkLastLoginDay(getApplicationContext());

        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPref.getString("LoginResponse", null);
        tokenTime = sharedPref.getLong("TokenTimestamp", 0);

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                rutasList = UtilJSONParser.parseArrayRutasPosts(jsonContent);
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

        CheckLogin.checkLastLoginDay(getApplicationContext());

        RutasModel rutaSeleccionada = (RutasModel) parent.getItemAtPosition(position);

        guardarRutaEnArchivo(rutaSeleccionada);

        Intent intent = new Intent(User.this, DetalleRuta2.class);
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
                guardarRutaEnArchivo(r);
                Intent intent = new Intent(User.this, EditRuta.class);
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
                int index = rutasList.indexOf(ruta);
                if (index != -1) {
                    rutasList.remove(index);
                    mAdaptadorRutas.notifyDataSetChanged();

                    UtilREST.runQueryWithHeaders(UtilREST.QueryType.DELETE, (apiUrl + "api/ruta/" + ruta.getId()), token, new UtilREST.OnResponseListener() {
                        @Override
                        public void onSuccess(UtilREST.Response r) {
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, RutasList.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}