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
import java.io.InputStream;

public class EditInfoUser extends AppCompatActivity {
    EditText nombre;
    EditText apellidos;
    EditText codigoPostal;
    private Button buttonDeletePhoto;
    EditText ciudad;
    EditText email;
    Button boton;
    Button guardar;
    String apiUrl;
    ImageView imagen;
    SharedPreferences sharedURL;
    private Uri uri = null;
    private String FotoString;
    Long id;
    String Password;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info_user);

        sharedURL = getSharedPreferences("AppURL", Context.MODE_PRIVATE);
        apiUrl = sharedURL.getString("URL", "");

        sharedURL = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        nombre = findViewById(R.id.nombre);
        apellidos = findViewById(R.id.apellido);
        codigoPostal = findViewById(R.id.codigoPostal);
        ciudad = findViewById(R.id.ciudad);
        email = findViewById(R.id.email);
        imagen = findViewById(R.id.imagen);
        boton = findViewById(R.id.boton);
        guardar = findViewById(R.id.botonGuardar);

        buttonDeletePhoto = findViewById(R.id.borrarFoto);
        buttonDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagen.setImageDrawable(null);
                FotoString = "";
                uri = null;
            }
        });

        id = sharedURL.getLong("Id", -1);
        nombre.setText(sharedURL.getString("Name", ""));
        apellidos.setText(sharedURL.getString("Surname", ""));

        email.setText(sharedURL.getString("Email", ""));
        String img = sharedURL.getString("Foto", "");
        FotoString = img;
        Password = sharedURL.getString("Password", "");

        String postalCode = sharedURL.getString("postalCode", null);
        if (postalCode != null && !postalCode.isEmpty()) {
            codigoPostal.setText(postalCode);
        } else {
            codigoPostal.setText("");
        }

        String city = sharedURL.getString("City", null);
        if (city != null && !city.isEmpty() || city != "null" || city != " " || city != "null" || city != "NULL") {
            ciudad.setText(city);
        } else {
            ciudad.setText("");
        }

        try {
            byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
            Glide.with(this).asBitmap().load(decodedString).into(imagen);
        } catch (IllegalArgumentException e) {
            Log.e("Base64 Error", "Failed to decode Base64 string", e);
            Glide.with(this).load(R.drawable.userwhothoutphoto).into(imagen);
        }


        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoSeleccion();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuardarUser();
                Intent intent = new Intent(EditInfoUser.this, User.class);
                startActivity(intent);
            }
        });
    }

    private void mostrarDialogoSeleccion() {
        final CharSequence[] opciones = {getResources().getString(R.string.hacerFoto), getResources().getString(R.string.seleccionarDeGaleria), getResources().getString(R.string.cancelar)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    imagen.setImageURI(uri);
                    FotoString = convertirImagenABase64(uri);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.errorImagen), Toast.LENGTH_SHORT).show();
                }
            });

    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    imagen.setImageURI(selectedImage);
                    uri = selectedImage;
                    FotoString = convertirImagenABase64(selectedImage);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.imagenNoSeleccionada), Toast.LENGTH_SHORT).show();
                }
            });

    public void GuardarUser() {

        CheckLogin.checkLastLoginDay(getApplicationContext());

        JSONObject UserModified = new JSONObject();
        try {
            UserModified.put("name", nombre.getText().toString());
            UserModified.put("password", Password);
            UserModified.put("surname", apellidos.getText().toString());
            UserModified.put("email", email.getText().toString());
            UserModified.put("city", ciudad.getText().toString());
            UserModified.put("postalCode", codigoPostal.getText().toString());
            UserModified.put("image", FotoString);
            UserModified.put("username", email.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
        sharedPref.getString("LoginResponse", "");

        API.postPutRutas(UserModified, apiUrl + "auth/user/" + id, sharedPref.getString("LoginResponse", ""), new UtilREST.OnResponseListener() {
            @Override
            public void onSuccess(UtilREST.Response response) {
                SharedPreferences sharedPref = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Name", nombre.getText().toString());
                editor.putString("Surname", apellidos.getText().toString());
                editor.putString("Email", email.getText().toString());
                editor.putString("City", ciudad.getText().toString());
                editor.putString("postalCode", codigoPostal.getText().toString());
                editor.putString("Foto", FotoString);
                editor.apply();

                Intent intent = new Intent(EditInfoUser.this, User.class);
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