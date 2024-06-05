package com.RutasMoteras.rutasmoterasapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;

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
import com.RutasMoteras.rutasmoterasapi.UtilREST;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class CrearRuta extends AppCompatActivity {
    private static final int CODIGO_PERMISOS_CAMARA = 1;
    private static final int CODIGO_PERMISOS_GALERIA = 2;

    private static final String SENDER_EMAIL = "rutasmoterasoficial@gmail.com";
    private static final String RECEIVER_EMAIL = "rutasmoterasoficial@gmail.com";
    private static final String PASSWORD_SENDER_EMAIL = "tioilxblzdgivveh";
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int SMTP_PORT = 587;

    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<String> galleryPermissionLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    private ImageView imageView;
    private Button buttonSelectPhoto;
    private Button buttonDeletePhoto;
    private Uri uri = null;
    private String fotoString = "";
    private String selectedComunidad;
    private String selectedTipoMoto;
    private Button crear;
    private EditText tit;
    private EditText des;
    private SharedPreferences sharedURL;
    private String apiUrl;

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
        buttonDeletePhoto.setOnClickListener(v -> {
            imageView.setImageDrawable(null);
            fotoString = "";
            uri = null;
        });

        crear.setOnClickListener(v -> {
            if (validarEntradas(tit.getText().toString(), des.getText().toString())) {
                crearRuta(tit.getText().toString(), des.getText().toString(), selectedComunidad, selectedTipoMoto, fotoString);
            } else {
                Toast.makeText(CrearRuta.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
            }
        });

        inicializarSpinners();
        inicializarLaunchers();
    }

    private void inicializarSpinners() {
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

    private void inicializarLaunchers() {
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
                    fotoString = convertirImagenABase64(uri);
                }
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri selectedImage = result.getData().getData();
                imageView.setImageURI(selectedImage);
                uri = selectedImage;
                fotoString = convertirImagenABase64(selectedImage);
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
        final CharSequence[] opciones = {getResources().getString(R.string.hacerFoto), getResources().getString(R.string.seleccionarDeGaleria), getResources().getString(R.string.cancelar)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elegir opción");
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
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nueva Foto");
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        cameraLauncher.launch(intent);
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    String convertirImagenABase64(Uri uriImagen) {
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

    private boolean validarEntradas(String titulo, String descripcion) {
        return !titulo.isEmpty() && !descripcion.isEmpty();
    }

    @SuppressLint("NotConstructor")
    private void crearRuta(String titulo, String descripcion, String comunidad, String tipo, String fotoBase64) {
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
        String loginResponse = sharedPref.getString("LoginResponse", "");

        API.postPostRutas(ruta, apiUrl + "api/ruta", loginResponse, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response response) {
                runOnUiThread(() -> {
                    launchConfetti();
                    showSuccessDialog();
                    if (checkNotificationPermission()) {
                        sendNotification();
                    }
                });

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Intent intent = new Intent(CrearRuta.this, User.class);
                    startActivity(intent);
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

    private void sendNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "my_channel_id";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Canal para notificaciones importantes");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(this, RutasList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.logo_round)
                .setContentTitle("Ruta creada!!!🎉")
                .setContentText("Gracias por usar mi aplicación. Disfrutala!.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText("Gracias por aceptar recibir notificaciones."));

        notificationManager.notify(1, builder.build());
    }

    private boolean checkNotificationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
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
}
