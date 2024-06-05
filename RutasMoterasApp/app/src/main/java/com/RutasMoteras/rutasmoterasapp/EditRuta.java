package com.RutasMoteras.rutasmoterasapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class EditRuta extends AppCompatActivity {

    private static final String TAG = "EditRuta";
    private static final String FILE_NAME = "ruta_seleccionada.txt";
    private static final String APP_PREFERENCES = "AppPreferences";
    private static final String USER_PREFERENCES = "UserPreferences";
    private static final String URL_KEY = "URL";
    private static final String LOGIN_RESPONSE_KEY = "LoginResponse";
    private static final String FOTO_DEFAULT = "";

    private ImageView imageView;
    private Button buttonSelectPhoto;
    private Button buttonDeletePhoto;
    private Button guardar;
    private EditText tit;
    private EditText des;
    private Spinner spinnerTipoMoto;
    private Spinner spinnerComunidad;

    private Uri uri = null;
    private String FotoString = FOTO_DEFAULT;
    private String selectedComunidad;
    private String selectedTipoMoto;
    private String apiUrl;
    private String token;
    private int idRuta;
    private RutasModel ruta;

    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<String> galleryPermissionLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ruta);

        initViews();
        loadPreferences();
        initLaunchers();

        String rutaInfo = leerRutaDesdeArchivo();
        Log.d(TAG, "Url a api: " + apiUrl + "api/ruta/" + rutaInfo);
        LLamarApi(apiUrl + "api/ruta/" + rutaInfo);

        buttonSelectPhoto.setOnClickListener(v -> mostrarDialogoSeleccion());

        guardar.setOnClickListener(v -> {
            GuardarRuta(tit.getText().toString(), des.getText().toString(), selectedComunidad, selectedTipoMoto, FotoString);
        });

        initSpinners();
        setSentenceCapitalizationTextWatcher(des);
    }

    private void initViews() {
        imageView = findViewById(R.id.imageView2);
        buttonSelectPhoto = findViewById(R.id.buttonSelectPhoto);
        buttonDeletePhoto = findViewById(R.id.borrarFoto);
        guardar = findViewById(R.id.button4);
        tit = findViewById(R.id.tit);
        des = findViewById(R.id.editTextTitle2);
        spinnerTipoMoto = findViewById(R.id.spinnerTipoMoto);
        spinnerComunidad = findViewById(R.id.spinnerComunidad);

        buttonDeletePhoto.setOnClickListener(v -> {
            imageView.setImageDrawable(null);
            FotoString = FOTO_DEFAULT;
            uri = null;
        });
    }

    private void loadPreferences() {
        SharedPreferences sharedURL = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        token = sharedURL.getString(LOGIN_RESPONSE_KEY, null);

        SharedPreferences sharedURL2 = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL2.getString(URL_KEY, "");
    }

    private void initLaunchers() {
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                abrirCamara();
            } else {
                mostrarExplicacionPermiso();
            }
        });

        galleryPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            abrirGaleria();
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                if (uri != null) {
                    imageView.setImageURI(uri);
                    FotoString = convertirImagenABase64(uri);
                }
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri selectedImage = result.getData().getData();
                imageView.setImageURI(selectedImage);
                uri = selectedImage;
                FotoString = convertirImagenABase64(selectedImage);
            }
        });
    }

    private void mostrarExplicacionPermiso() {
        new AlertDialog.Builder(this)
                .setTitle("Permiso necesario")
                .setMessage("Necesitamos acceso a tu cámara para poder tomar fotos.")
                .setPositiveButton("Ok", (dialog, which) -> permissionLauncher.launch(Manifest.permission.CAMERA))
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void mostrarDialogoSeleccion() {
        final CharSequence[] opciones = {
                getResources().getString(R.string.hacerFoto),
                getResources().getString(R.string.seleccionarDeGaleria),
                getResources().getString(R.string.cancelar)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.elegirOpcion));
        builder.setItems(opciones, (dialog, which) -> {
            switch (which) {
                case 0: // Hacer foto
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        permissionLauncher.launch(Manifest.permission.CAMERA);
                    } else {
                        abrirCamara();
                    }
                    break;
                case 1: // Seleccionar de galería
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    } else {
                        abrirGaleria();
                    }
                    break;
                case 2: // Cancelar
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    private void abrirCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, getResources().getString(R.string.nuevaFoto));
            uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            cameraLauncher.launch(intent);
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private String convertirImagenABase64(Uri uriImagen) {
        try (InputStream imageStream = getContentResolver().openInputStream(uriImagen)) {
            Bitmap bitmapOriginal = BitmapFactory.decodeStream(imageStream);

            int maxWidth = 480;
            int maxHeight = 480;
            float scaleWidth = maxWidth / (float) bitmapOriginal.getWidth();
            float scaleHeight = maxHeight / (float) bitmapOriginal.getHeight();
            float scale = Math.min(scaleWidth, scaleHeight);

            int width = Math.round(scale * bitmapOriginal.getWidth());
            int height = Math.round(scale * bitmapOriginal.getHeight());

            Bitmap bitmapRedimensionado = Bitmap.createScaledBitmap(bitmapOriginal, width, height, true);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Image file not found", e);
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Error reading image file", e);
            return null;
        }
    }

    private void cargarImagenBase64(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        Glide.with(this)
                .load(decodedBitmap)
                .into(imageView);
    }

    private void actualizarVistasConDatosDeRuta() {
        if (ruta != null) {
            tit.setText(ruta.getTitle());
            des.setText(ruta.getDescription());

            int tipoMotoIndex = obtenerIndiceEnArray(ruta.getTipoMoto(), getResources().getStringArray(R.array.tipo_moto_array));
            int comunidadIndex = obtenerIndiceEnArray(ruta.getComunidad(), getResources().getStringArray(R.array.comunidad_array));

            idRuta = ruta.getId();

            spinnerTipoMoto.setSelection(tipoMotoIndex);
            spinnerComunidad.setSelection(comunidadIndex);

            String base64Image = ruta.getImage();
            cargarImagenBase64(base64Image);

            FotoString = base64Image;
        }
    }

    private int obtenerIndiceEnArray(String valor, String[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(valor)) {
                return i;
            }
        }
        return 0;
    }

    private String leerRutaDesdeArchivo() {
        StringBuilder rutaInfo = new StringBuilder();
        try (FileInputStream fis = openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                rutaInfo.append(linea);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error leyendo el archivo", e);
        }
        Log.d(TAG, "Leído del archivo: " + rutaInfo.toString());
        return rutaInfo.toString();
    }

    private void LLamarApi(String url) {
        CheckLogin.checkLastLoginDay(getApplicationContext());

        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response r) {
                ruta = UtilJSONParser.parsePostRuta(r.content);
                actualizarVistasConDatosDeRuta();
            }

            @Override
            public void onError(UtilREST.Response r) {
                String errorMsg = (r.content != null) ? r.content : "El contenido de la respuesta es nulo";
                Log.d(TAG, "ERROR: " + errorMsg);
                Toast.makeText(EditRuta.this, getResources().getString(R.string.ErrorServidor), Toast.LENGTH_LONG).show();
                startActivity(new Intent(EditRuta.this, PantallaInicial.class));
            }
        });
    }

    private void GuardarRuta(String titulo, String descripcion, String comunidad, String tipoMoto, String fotoBase64) {
        CheckLogin.checkLastLoginDay(getApplicationContext());
        SharedPreferences userPrefs = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);

        JSONObject nuevaRuta = new JSONObject();
        try {
            nuevaRuta.put("titulo", titulo);
            nuevaRuta.put("fecha_creacion", ruta.getDate());
            nuevaRuta.put("descripcion", descripcion);
            nuevaRuta.put("userId", userPrefs.getLong("Id", 0));
            nuevaRuta.put("imageURL", fotoBase64);
            nuevaRuta.put("tipoMoto", tipoMoto);
            nuevaRuta.put("comunidadAutonoma", comunidad);
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception", e);
        }

        SharedPreferences sharedPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        String loginResponse = sharedPref.getString(LOGIN_RESPONSE_KEY, "");

        API.postPutRutas(nuevaRuta, apiUrl + "api/ruta/" + idRuta, loginResponse, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response response) {
                Long role = userPrefs.getLong("Role", 1);
                Intent intent = new Intent(EditRuta.this, role == 2 ? SuperUser.class : User.class);
                startActivity(intent);
            }

            @Override
            public void onError(UtilREST.Response response) {
                String errorMsg = (response.content != null) ? response.content : "Error data is null";
                Log.e(TAG, errorMsg);
            }
        });
    }

    private void setSentenceCapitalizationTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean capitalizeNext = true; // Flag to indicate the next character should be capitalized

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No implementation needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Check if the last character entered is a period or if a space is entered after a period
                if (count > 0) {
                    int endIndex = start + count;
                    char lastChar = s.charAt(endIndex - 1);
                    if (lastChar == '.' || (capitalizeNext && lastChar == ' ')) {
                        capitalizeNext = true; // Set flag to capitalize the next character
                    } else {
                        capitalizeNext = false;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Capitalize the character if needed
                if (capitalizeNext && s.length() > 0) {
                    for (int i = 0; i < s.length(); i++) {
                        if (capitalizeNext && Character.isLetter(s.charAt(i)) && Character.isLowerCase(s.charAt(i))) {
                            s.replace(i, i + 1, String.valueOf(Character.toUpperCase(s.charAt(i))));
                            capitalizeNext = false; // Reset the flag after capitalization
                        } else if (s.charAt(i) == '.') {
                            capitalizeNext = true; // Set the flag if there's a period
                        } else if (!Character.isWhitespace(s.charAt(i))) {
                            capitalizeNext = false; // Reset the flag if the next character is not whitespace
                        }
                    }
                }

                // Ensure the first character is always capitalized if it's a letter
                if (s.length() > 0 && Character.isLetter(s.charAt(0)) && Character.isLowerCase(s.charAt(0))) {
                    s.replace(0, 1, String.valueOf(Character.toUpperCase(s.charAt(0))));
                }
            }
        });
    }

    private void initSpinners() {
        ArrayAdapter<CharSequence> adapterTipoMoto = ArrayAdapter.createFromResource(this,
                R.array.tipo_moto_array, android.R.layout.simple_spinner_item);
        adapterTipoMoto.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoMoto.setAdapter(adapterTipoMoto);

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
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerComunidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedComunidad = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}
