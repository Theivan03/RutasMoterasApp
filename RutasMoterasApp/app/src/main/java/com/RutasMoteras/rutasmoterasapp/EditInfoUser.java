package com.RutasMoteras.rutasmoterasapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditInfoUser extends AppCompatActivity {
    EditText nombre;
    EditText apellidos;
    EditText codigoPostal;
    EditText ciudad;
    EditText email;
    Button boton;
    ImageView imagen;
    SharedPreferences sharedURL;
    private Uri uri = null;
    private String FotoString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info_user);

        sharedURL = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        nombre.setText(sharedURL.getString("Name", ""));
        apellidos.setText(sharedURL.getString("Surname", ""));
        codigoPostal.setText(sharedURL.getString("postalCode", ""));
        ciudad.setText(sharedURL.getString("City", ""));
        email.setText(sharedURL.getString("Email", ""));
        String img = sharedURL.getString("Foto", "");

        if(img.equals("")){
            Glide.with(this)
                    .load("https://drive.google.com/uc?id=1veQeZEa0_E17VSfY64cVGnMlUKgboNiq")
                    .into(imagen);
        }

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoSeleccion();
            }
        });
    }

    private void mostrarDialogoSeleccion() {
        final CharSequence[] opciones = {"Tomar Foto", "Elegir de Galería", "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private void cargarImagenBase64(String base64Image) {

        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);

        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

        Glide.with(this)
                .load(decodedBitmap)
                .into(imagen);
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
                    imagen.setImageURI(uri); // uri ya está establecida por abrirCamara()
                    FotoString = convertirImagenABase64(uri); // Asegúrate de que este método maneje correctamente null
                } else {
                    Toast.makeText(this, "Error al obtener la imagen", Toast.LENGTH_SHORT).show();
                }
            });

    private ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    imagen.setImageURI(selectedImage);
                    uri = selectedImage; // Actualizar la uri con la seleccionada de la galería
                    FotoString = convertirImagenABase64(selectedImage); // Convertir directamente la nueva URI a Base64
                } else {
                    Toast.makeText(this, "Imagen no seleccionada", Toast.LENGTH_SHORT).show();
                }
            });
}