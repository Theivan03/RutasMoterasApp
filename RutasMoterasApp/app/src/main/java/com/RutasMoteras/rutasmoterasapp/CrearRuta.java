package com.RutasMoteras.rutasmoterasapp;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class CrearRuta extends AppCompatActivity {

    private ImageView imageView;
    private Button buttonSelectPhoto;
    private Button buttonDeletePhoto;
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

        setSentenceCapitalizationTextWatcher(des);

        buttonDeletePhoto = findViewById(R.id.borrarFoto);
        buttonDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(null);
                FotoString = "";
                uri = null;
            }
        });

        crear.setOnClickListener(v -> {
            CrearRuta(tit.getText().toString(), des.getText().toString(), selectedComunidad, selectedTipoMoto, FotoString);

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
                    imageView.setImageURI(uri);
                    FotoString = convertirImagenABase64(uri);
                } else {
                    Toast.makeText(CrearRuta.this, getResources().getString(R.string.errorImagen), Toast.LENGTH_SHORT).show();
                }
            });

    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    imageView.setImageURI(selectedImage);
                    uri = selectedImage;
                    FotoString = convertirImagenABase64(selectedImage);
                } else {
                    Toast.makeText(CrearRuta.this, getResources().getString(R.string.imagenNoSeleccionada), Toast.LENGTH_SHORT).show();
                }
            });

    private void mostrarDialogoSeleccion() {
        final CharSequence[] opciones = {getResources().getString(R.string.hacerFoto), getResources().getString(R.string.seleccionarDeGaleria), getResources().getString(R.string.cancelar)};
        AlertDialog.Builder builder = new AlertDialog.Builder(CrearRuta.this);
        builder.setTitle(getResources().getString(R.string.elegirOpcion));
        builder.setItems(opciones, (dialog, which) -> {
            if (opciones[which].equals(getResources().getString(R.string.nuevaFoto))) {
                abrirCamara();
            } else if (opciones[which].equals(getResources().getString(R.string.seleccionarDeGaleria))) {
                abrirGaleria();
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void abrirCamara() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, getResources().getString(R.string.nuevaFoto));
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
                runOnUiThread(() -> {
                    launchConfetti();
                    showSuccessDialog();
                });

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(CrearRuta.this, User.class);
                        startActivity(intent);
                    }
                }, 3000);
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

    private void launchConfetti() {
        KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
        konfettiView.build()
                .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.BLUE)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(1500L)
                .addShapes(Shape.CIRCLE, Shape.RECT)
                .addSizes(new Size(12, 5), new Size(16, 6))
                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, konfettiView.getHeight() + 50f)
                .streamFor(300, 2000L);
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_success, null);
        builder.setView(customLayout);


        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setSentenceCapitalizationTextWatcher(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            private boolean capitalizeNext = false; // Flag to indicate the next character should be capitalized

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

}