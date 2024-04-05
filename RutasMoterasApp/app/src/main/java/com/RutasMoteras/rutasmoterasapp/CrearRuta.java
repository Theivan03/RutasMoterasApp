package com.RutasMoteras.rutasmoterasapp;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.RutasMoteras.rutasmoterasapi.API;
import com.RutasMoteras.rutasmoterasapi.CheckLogin;
import com.RutasMoteras.rutasmoterasapi.RutasModel;
import com.RutasMoteras.rutasmoterasapi.UtilJSONParser;
import com.RutasMoteras.rutasmoterasapi.UtilREST;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CrearRuta extends AppCompatActivity {

    private ImageView imageView;
    private Button buttonSelectPhoto;
    private Uri uri = null;
    private String FotoString;
    private String selectedComunidad;
    private String selectedTipoMoto;
    private Button crear;
    private EditText tit;
    private EditText des;
    SharedPreferences sharedURL;
    String apiUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_ruta);

        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        imageView = findViewById(R.id.imageView);
        buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto);

        buttonSelectPhoto.setOnClickListener(v -> mostrarDialogoSeleccion());

        crear = findViewById(R.id.button);
        tit = findViewById(R.id.tit);
        des = findViewById(R.id.editTextTitle2);
        crear.setOnClickListener(v -> {
            if(uri != null) {
                String fotoBase64 = convertirImagenABase64(uri);
                if(fotoBase64 != null) {
                    CrearRuta(tit.getText().toString(), des.getText().toString(), selectedComunidad, selectedTipoMoto, fotoBase64);
                } else {
                    Toast.makeText(CrearRuta.this, "Error al convertir la imagen", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(CrearRuta.this, "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
            }
        });


        Spinner spinnerTipoMoto = findViewById(R.id.spinnerTipoMoto);
        ArrayAdapter<CharSequence> adapterTipoMoto = ArrayAdapter.createFromResource(this,
                R.array.tipo_moto_array, android.R.layout.simple_spinner_item);
        adapterTipoMoto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoMoto.setAdapter(adapterTipoMoto);


        Spinner spinnerComunidad = findViewById(R.id.spinnerComunidad);
        ArrayAdapter<CharSequence> adapterComunidad = ArrayAdapter.createFromResource(this,
                R.array.comunidad_array, android.R.layout.simple_spinner_item);
        adapterComunidad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerComunidad.setAdapter(adapterComunidad);


        spinnerTipoMoto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTipoMoto = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerComunidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedComunidad = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    imageView.setImageURI(uri); // uri ya está establecida por abrirCamara()
                    FotoString = convertirImagenABase64(uri); // Asegúrate de que este método maneje correctamente null
                } else {
                    Toast.makeText(CrearRuta.this, "Error al obtener la imagen", Toast.LENGTH_SHORT).show();
                }
            });

    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    imageView.setImageURI(selectedImage);
                    uri = selectedImage; // Actualizar la uri con la seleccionada de la galería
                    FotoString = convertirImagenABase64(selectedImage); // Convertir directamente la nueva URI a Base64
                } else {
                    Toast.makeText(CrearRuta.this, "Imagen no seleccionada", Toast.LENGTH_SHORT).show();
                }
            });

    private void mostrarDialogoSeleccion() {
        final CharSequence[] opciones = {"Tomar Foto", "Elegir de Galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CrearRuta.this);
        builder.setTitle("Elige una opción");
        builder.setItems(opciones, (dialog, which) -> {
            if (opciones[which].equals("Tomar Foto")) {
                abrirCamara();
            } else if (opciones[which].equals("Elegir de Galería")) {
                abrirGaleria();
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void abrirCamara() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nuevo Picture");
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        cameraLauncher.launch(intent);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private String convertirImagenABase64(Uri uriImagen) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(uriImagen);
            Bitmap bitmapOriginal = BitmapFactory.decodeStream(imageStream);

            // Calcula el factor de escala para mantener la relación de aspecto, o puedes ignorarlo para forzar un 480x480
            int anchoOriginal = bitmapOriginal.getWidth();
            int altoOriginal = bitmapOriginal.getHeight();
            float escala = Math.min((float) 480 / anchoOriginal, (float) 480 / altoOriginal);

            int anchoRedimensionado = Math.round(anchoOriginal * escala);
            int altoRedimensionado = Math.round(altoOriginal * escala);

            // Crea un nuevo Bitmap redimensionado
            Bitmap bitmapRedimensionado = Bitmap.createScaledBitmap(bitmapOriginal, anchoRedimensionado, altoRedimensionado, true);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("NotConstructor")
    private void CrearRuta(String titulo, String descripcion, String comunidad, String tipo, String fotoBase64) {

        CheckLogin.checkLastLoginDay(getApplicationContext());

        SharedPreferences userPrefs = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fechaHoy = dateFormat.format(cal.getTime());

        JSONObject ruta = new JSONObject();
        try {
            ruta.put("titulo", titulo);
            ruta.put("fecha_creacion", fechaHoy);
            ruta.put("descripcion", descripcion);
            ruta.put("userId", userPrefs.getLong("Id", 0));
            ruta.put("imageURL", fotoBase64);
            ruta.put("tipoMoto", tipo);
            ruta.put("comunidadAutonoma", comunidad);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        sharedPref.getString("LoginResponse", "");

        API.postPostRutas(ruta, apiUrl + "api/ruta", sharedPref.getString("LoginResponse", ""), new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response response) {
                String responseData = response.content;
                RutasModel ruta = UtilJSONParser.parsePostRuta(responseData);
                guardarRutaEnArchivo(ruta);
                Intent intent = new Intent(CrearRuta.this, RutasList.class);
                startActivity(intent);
            }

            @Override
            public void onError(UtilREST.Response response) {
                String errorData = response.content;
                if (errorData != null) {
                    Log.e("Error", errorData);
                } else {
                    Log.e("Error", "Error data is null");
                }
            }

        });
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