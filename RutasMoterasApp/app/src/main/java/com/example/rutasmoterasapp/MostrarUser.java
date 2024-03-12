package com.example.rutasmoterasapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.rutasmoterasapi.RutasModel;
import com.example.rutasmoterasapi.UserModel;
import com.example.rutasmoterasapi.UtilJSONParser;
import com.example.rutasmoterasapi.UtilREST;

import java.util.List;

public class MostrarUser extends AppCompatActivity implements AdapterView.OnItemClickListener {

    String token;
    Long tokenTime;
    RutasAdapter mAdaptadorRutas;
    ListView miListaRutas;
    ImageView imagen;
    TextView nombre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_user);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");

        miListaRutas = findViewById(R.id.miListaRutas);
        miListaRutas.setOnItemClickListener(this);

        LLamarApi("http://192.168.1.131:5000/api/rutasU/" + userId);
        ObtenerUsuario("http://192.168.1.131:5000/api/usuarioI/" + userId);
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
                    Toast.makeText(MostrarUser.this, getResources().getString(R.string.SinRutas), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(UtilREST.Response r) {
                Toast.makeText(MostrarUser.this, getResources().getString(R.string.ErrorServidor),Toast.LENGTH_LONG).show();

                Intent intent = new Intent(MostrarUser.this, Login.class);
                startActivity(intent);
            }
        });
    }

    public void ObtenerUsuario(String url){
        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedPref.getString("LoginResponse", null);
        tokenTime = sharedPref.getLong("TokenTimestamp", 0);
        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                UserModel user = UtilJSONParser.parseUserPosts(jsonContent);
                Log.d("Usuario: ", r.content);

                nombre = findViewById(R.id.NombreUsu);
                imagen = findViewById(R.id.ImgUsuario);

                if (user != null) {
                    nombre.setText(user.getName());
                    Glide.with(MostrarUser.this)
                            .load(user.getImage())
                            .into(imagen);
                } else {
                    Log.e("MostrarUser", "El objeto user es nulo");
                }
            }

            @Override
            public void onError(UtilREST.Response r) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}