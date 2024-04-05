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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
    String token;
    RutasModel ruta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ruta);

        imageView = findViewById(R.id.imageView2);
        buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto);

        sharedURL = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        token = sharedURL.getString("LoginResponse", null);

        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        String rutaInfo = leerRutaDesdeArchivo();

        Log.d("Url a api:", apiUrl + "api/ruta/" + rutaInfo);

        LLamarApi(apiUrl + "api/ruta/" + rutaInfo);

        buttonSelectPhoto.setOnClickListener(v -> mostrarDialogoSeleccion());

        crear = findViewById(R.id.button4);
        tit = findViewById(R.id.tit);
        des = findViewById(R.id.editTextTitle2);
        crear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(uri != null) {
                    String fotoBase64 = convertirImagenABase64(uri);
                    if(fotoBase64 != null) {
                        GuardarRuta(tit.getText().toString(), des.getText().toString(), selectedComunidad, selectedTipoMoto, fotoBase64);
                    } else {
                        Toast.makeText(EditRuta.this, "Error al convertir la imagen a Base64", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditRuta.this, "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
                }
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

    private void mostrarDialogoSeleccion() {
        final CharSequence[] opciones = {"Tomar Foto", "Elegir de Galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditRuta.this);
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

    private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    imageView.setImageURI(uri); // uri ya está establecida por abrirCamara()
                    FotoString = convertirImagenABase64(uri); // Asegúrate de que este método maneje correctamente null
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
                    uri = selectedImage; // Actualizar la uri con la seleccionada de la galería
                    FotoString = convertirImagenABase64(selectedImage); // Convertir directamente la nueva URI a Base64
                } else {
                    Toast.makeText(EditRuta.this, "Imagen no seleccionada", Toast.LENGTH_SHORT).show();
                }
            });

    private void cargarImagenBase64(String base64Image) {
        // Decodifica la imagen desde Base64 a byte[]
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        // Convierte byte[] a Bitmap
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        // Usa Glide para cargar el Bitmap
        Glide.with(this)
                .load(decodedBitmap)
                .into(imageView);
    }

    private void actualizarVistasConDatosDeRuta() {
        if (ruta != null) {
            Spinner spinnerTipoMoto = findViewById(R.id.spinnerTipoMoto);
            Spinner spinnerComunidad = findViewById(R.id.spinnerComunidad);

            tit.setText(ruta.getTitle());
            des.setText(ruta.getDescription());

            int tipoMotoIndex = obtenerIndiceEnArray(ruta.getTipoMoto(), getResources().getStringArray(R.array.tipo_moto_array));
            int comunidadIndex = obtenerIndiceEnArray(ruta.getComunidad(), getResources().getStringArray(R.array.comunidad_array));

            idRuta = ruta.getId();

            spinnerTipoMoto.setSelection(tipoMotoIndex);
            spinnerComunidad.setSelection(comunidadIndex);


            String base64Image = ruta.getImage();
            cargarImagenBase64(base64Image);

            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if(decodedByte != null) {
                imageView.setImageBitmap(decodedByte);
            } else {
                Log.e("DetalleRuta2", "La decodificación de la imagen falló.");
            }

            // Decodifica y carga la imagen
            Glide.with(this)
                    .asBitmap()
                    .load(decodedString)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .error(R.drawable.favicon) // Asegúrate de tener este recurso drawable.
                    .into(imageView);
        } else {
            // Maneja el caso en que `ruta` sea null
        }
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

    private String convertirImagenABase64(Uri uriImagen) {
        try {
            InputStream imageStream = getContentResolver().openInputStream(uriImagen);
            Bitmap bitmapOriginal = BitmapFactory.decodeStream(imageStream);

            // Puedes ajustar estos valores para reducir aún más el tamaño de la imagen si es necesario
            int maxWidth = 480;
            int maxHeight = 480;
            float scaleWidth = maxWidth / (float) bitmapOriginal.getWidth();
            float scaleHeight = maxHeight / (float) bitmapOriginal.getHeight();
            float scale = Math.min(scaleWidth, scaleHeight);

            int width = Math.round(scale * bitmapOriginal.getWidth());
            int height = Math.round(scale * bitmapOriginal.getHeight());

            Bitmap bitmapRedimensionado = Bitmap.createScaledBitmap(bitmapOriginal, width, height, true);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // Ajusta el segundo parámetro para cambiar la calidad de la imagen
            // Un valor más bajo reduce el tamaño del archivo pero también la calidad de la imagen
            bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }



    private String leerRutaDesdeArchivo() {
        StringBuilder rutaInfo = new StringBuilder();
        try {
            FileInputStream fis = openFileInput("ruta_seleccionada.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String linea;
            while ((linea = br.readLine()) != null) {
                rutaInfo.append(linea);
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("Leído del archivo:", rutaInfo.toString());
        return rutaInfo.toString();
    }

    public void LLamarApi(String url){

        CheckLogin.checkLastLoginDay(getApplicationContext());

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                String jsonContent = r.content;
                ruta = UtilJSONParser.parsePostRuta(jsonContent);

                actualizarVistasConDatosDeRuta();
            }

            @Override
            public void onError(UtilREST.Response r) {
                if (r.content != null) {
                    Log.d("ERROR!!!!!!!!!", r.content);
                } else {
                    Log.d("ERROR!!!!!!!!!", "El contenido de la respuesta es nulo" + r.content);
                }
                Toast.makeText(EditRuta.this, getResources().getString(R.string.ErrorServidor), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditRuta.this, PantallaInicial.class);
                startActivity(intent);
            }
        });
    }

    public void GuardarRuta(String tit, String des, String com, String tipo, String fotoBase64) {

        CheckLogin.checkLastLoginDay(getApplicationContext());
        SharedPreferences userPrefs = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        JSONObject nuevaRuta = new JSONObject();
        try {
            nuevaRuta.put("titulo", tit);
            nuevaRuta.put("fecha_creacion", ruta.getDate());
            nuevaRuta.put("descripcion", des);
            nuevaRuta.put("userId", userPrefs.getLong("Id", 0));
            nuevaRuta.put("imageURL", fotoBase64);
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
