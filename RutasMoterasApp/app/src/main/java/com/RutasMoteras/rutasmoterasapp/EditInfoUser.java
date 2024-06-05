package com.RutasMoteras.rutasmoterasapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.RutasMoteras.rutasmoterasapi.API;
import com.RutasMoteras.rutasmoterasapi.CheckLogin;
import com.RutasMoteras.rutasmoterasapi.UtilREST;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditInfoUser extends AppCompatActivity {
    private static final String TAG = "EditInfoUser";
    private static final String APP_URL = "AppURL";
    private static final String USER_PREFERENCES = "UserPreferences";
    private static final String LOGIN_RESPONSE_KEY = "LoginResponse";
    private static final String URL_KEY = "URL";

    private EditText nombre, apellidos, codigoPostal, ciudad, email;
    private ImageView imagen;
    private Button boton, guardar, buttonDeletePhoto;
    private String apiUrl;
    private Uri uri = null;
    private String FotoString;
    private Long id;
    private String Password;

    private ActivityResultLauncher<String> permissionLauncher;
    private ActivityResultLauncher<String> galleryPermissionLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info_user);

        initViews();
        loadPreferences();
        initLaunchers();

        buttonDeletePhoto.setOnClickListener(v -> {
            imagen.setImageDrawable(null);
            FotoString = "";
            uri = null;
        });

        boton.setOnClickListener(v -> mostrarDialogoSeleccion());

        guardar.setOnClickListener(v -> {
            GuardarUser();
            startActivity(new Intent(EditInfoUser.this, User.class));
        });
    }

    private void initViews() {
        nombre = findViewById(R.id.nombre);
        apellidos = findViewById(R.id.apellido);
        codigoPostal = findViewById(R.id.codigoPostal);
        ciudad = findViewById(R.id.ciudad);
        email = findViewById(R.id.email);
        imagen = findViewById(R.id.imagen);
        boton = findViewById(R.id.boton);
        guardar = findViewById(R.id.botonGuardar);
        buttonDeletePhoto = findViewById(R.id.borrarFoto);
    }

    private void loadPreferences() {
        SharedPreferences sharedURL = getSharedPreferences(APP_URL, Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString(URL_KEY, "");

        SharedPreferences userPreferences = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
        id = userPreferences.getLong("Id", -1);
        nombre.setText(userPreferences.getString("Name", ""));
        apellidos.setText(userPreferences.getString("Surname", ""));
        email.setText(userPreferences.getString("Email", ""));
        FotoString = userPreferences.getString("Foto", "");
        Password = userPreferences.getString("Password", "");

        String postalCode = userPreferences.getString("postalCode", "");
        codigoPostal.setText(postalCode);

        String city = userPreferences.getString("City", "");
        ciudad.setText(city);

        loadProfileImage(FotoString);
    }

    private void loadProfileImage(String base64Image) {
        try {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Glide.with(this).asBitmap().load(decodedString).into(imagen);
        } catch (IllegalArgumentException e) {
            Log.e("Base64 Error", "Failed to decode Base64 string", e);
            Glide.with(this).load(R.drawable.userwhothoutphoto).into(imagen);
        }
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
                    imagen.setImageURI(uri);
                    FotoString = convertirImagenABase64(uri);
                }
            }
        });

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Uri selectedImage = result.getData().getData();
                imagen.setImageURI(selectedImage);
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

    public void GuardarUser() {
        CheckLogin.checkLastLoginDay(getApplicationContext());

        JSONObject userModified = new JSONObject();
        try {
            userModified.put("name", nombre.getText().toString());
            userModified.put("password", Password);
            userModified.put("surname", apellidos.getText().toString());
            userModified.put("email", email.getText().toString());
            userModified.put("city", ciudad.getText().toString());
            userModified.put("postalCode", codigoPostal.getText().toString());
            userModified.put("image", FotoString);
            userModified.put("username", email.getText().toString());
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception", e);
        }

        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        String loginResponse = sharedPref.getString(LOGIN_RESPONSE_KEY, "");

        API.postPutRutas(userModified, apiUrl + "auth/user/" + id, loginResponse, new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response response) {
                SharedPreferences sharedPref = getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Name", nombre.getText().toString());
                editor.putString("Surname", apellidos.getText().toString());
                editor.putString("Email", email.getText().toString());
                editor.putString("City", ciudad.getText().toString());
                editor.putString("postalCode", codigoPostal.getText().toString());
                editor.putString("Foto", FotoString);
                editor.apply();

                startActivity(new Intent(EditInfoUser.this, User.class));
            }

            @Override
            public void onError(UtilREST.Response response) {
                String errorData = response.content;
                if (errorData != null) {
                    Log.e(TAG, errorData);
                } else {
                    Log.e(TAG, "Error data is null");
                }
            }
        });
    }
}
