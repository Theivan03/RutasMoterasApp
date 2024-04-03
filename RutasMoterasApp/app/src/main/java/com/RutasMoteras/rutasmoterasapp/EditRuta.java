package com.RutasMoteras.rutasmoterasapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

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
import android.util.Base64;
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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class EditRuta extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_IMAGE_PICK = 102;
    private static final int PERMISSION_REQUEST_CODE = 100;

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
    int idRuta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ruta);

        imageView = findViewById(R.id.imageView2);
        buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto);

        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        buttonSelectPhoto.setOnClickListener(v -> mostrarDialogoSeleccion());

        crear = findViewById(R.id.button4);
        tit = findViewById(R.id.tit);
        des = findViewById(R.id.editTextTitle2);
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuardarRuta(tit.getText().toString(), des.getText().toString(), selectedComunidad, selectedTipoMoto);
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

        // Insertar datos en los Spinners
        insertarDatos();
    }

    private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    imageView.setImageURI(uri);
                    FotoString = uri.toString();
                } else {
                    Toast.makeText(EditRuta.this, "Error al obtener la imagen", Toast.LENGTH_SHORT).show();
                }
            });


    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    imageView.setImageURI(selectedImage);
                    FotoString = selectedImage.toString();
                } else {
                    Toast.makeText(EditRuta.this, "Imagen no seleccionada", Toast.LENGTH_SHORT).show();
                }
            });

    private void mostrarDialogoSeleccion() {
        final CharSequence[] opciones = {"Tomar Foto", "Elegir de Galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditRuta.this);
        builder.setTitle("Elige una opción");
        builder.setItems(opciones, (dialog, which) -> {
            if (opciones[which].equals("Tomar Foto")) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                ContentValues values = new ContentValues(1);
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
                uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                cameraLauncher.launch(intent);
            } else if (opciones[which].equals("Elegir de Galería")) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(intent);
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // Método para insertar datos en los Spinners
    private void insertarDatos() {
        Spinner spinnerTipoMoto = findViewById(R.id.spinnerTipoMoto);
        Spinner spinnerComunidad = findViewById(R.id.spinnerComunidad);

        String rutaInfo = leerRutaDesdeArchivo();
        String[] datosRuta = rutaInfo.split("\n");
        Log.d("Ruta entera", rutaInfo);

        tit.setText(datosRuta[1]);
        des.setText(datosRuta[4]);

        // Obtener el índice de las opciones correspondientes
        int tipoMotoIndex = obtenerIndiceEnArray(datosRuta[0], getResources().getStringArray(R.array.tipo_moto_array));
        int comunidadIndex = obtenerIndiceEnArray(datosRuta[3], getResources().getStringArray(R.array.comunidad_array));
        idRuta = Integer.parseInt(datosRuta[7]);
        Log.d("Id de la ruta", String.valueOf(idRuta));

        // Establecer las selecciones en los Spinners
        spinnerTipoMoto.setSelection(tipoMotoIndex);
        spinnerComunidad.setSelection(comunidadIndex);
    }

    // Método auxiliar para obtener el índice de una cadena en un array
    private int obtenerIndiceEnArray(String valor, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(valor)) {
                return i;
            }
        }
        return 0; // Valor predeterminado si no se encuentra la opción
    }

    private String leerRutaDesdeArchivo() {
        StringBuilder rutaInfo = new StringBuilder();
        try {
            FileInputStream fis = openFileInput("ruta_seleccionada.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String linea;
            while ((linea = br.readLine()) != null) {
                rutaInfo.append(linea).append("\n");
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rutaInfo.toString();
    }

    public void GuardarRuta(String tit, String des, String com, String tipo) {

        CheckLogin.checkLastLoginDay(getApplicationContext());
        SharedPreferences userPrefs = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        JSONObject nuevaRuta = new JSONObject();
        try {
            nuevaRuta.put("titulo", tit);
            nuevaRuta.put("fecha_creacion", "");
            nuevaRuta.put("descripcion", des);
            nuevaRuta.put("userId", userPrefs.getLong("Id", 0));
            nuevaRuta.put("imageURL", " ");
            nuevaRuta.put("tipoMoto", tipo);
            nuevaRuta.put("comunidadAutonoma", com);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        sharedPref.getString("LoginResponse", "");

        API.postPutRutas(nuevaRuta, apiUrl + "api/ruta/" + idRuta, sharedPref.getString("LoginResponse", ""), new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response response) {
                String responseData = response.content;
                RutasModel ruta = UtilJSONParser.parsePostRuta(responseData);
                Intent intent = new Intent(EditRuta.this, RutasList.class);
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
}
