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

import com.RutasMoteras.rutasmoterasapi.RutasModel;
import com.RutasMoteras.rutasmoterasapi.UtilJSONParser;
import com.RutasMoteras.rutasmoterasapi.UtilREST;
import com.bumptech.glide.Glide;

import java.io.FileOutputStream;
import java.io.IOException;
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

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        miListaRutas = findViewById(R.id.miListaRutas);
        miListaRutas.setOnItemClickListener(this);

        registerForContextMenu(miListaRutas);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LLamarApi("http://192.168.1.131:5000/api/rutas");
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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
            Intent intent = new Intent(User.this, ContactWithUs.class);
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
        RutasModel rutaSeleccionada = (RutasModel) parent.getItemAtPosition(position);

        // Guardar la ruta en un archivo de texto
        guardarRutaEnArchivo(rutaSeleccionada);

        // Ir a otra pantalla
        Intent intent = new Intent(User.this, DetalleRuta2.class);
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

                mAdaptadorRutas.notifyDataSetChanged();
                break;
            case R.id.editar:
                Intent intent = new Intent(User.this, EditRuta.class);
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
        builder.setTitle("Título del Diálogo");
        builder.setMessage("¿Estás seguro de borrar la película?");

        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Busca la posición de la ruta en la lista
                int index = rutasList.indexOf(ruta);
                if (index != -1) {
                    // Si se encuentra la ruta, elimínala de la lista y notifica al adaptador
                    rutasList.remove(index);
                    mAdaptadorRutas.notifyDataSetChanged();

                    // Llama a la API para eliminar la ruta
                    UtilREST.runQueryWithHeaders(UtilREST.QueryType.DELETE, ("http://44.207.234.210/api/ruta/" + ruta.getId()), token, new UtilREST.OnResponseListener() {
                        @Override
                        public void onSuccess(UtilREST.Response r) {
                            // Maneja el éxito de la eliminación de la ruta
                        }

                        @Override
                        public void onError(UtilREST.Response r) {
                            // Maneja el error al eliminar la ruta
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

}