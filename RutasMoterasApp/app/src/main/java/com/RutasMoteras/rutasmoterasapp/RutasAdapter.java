package com.RutasMoteras.rutasmoterasapp;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.RutasMoteras.rutasmoterasapi.RutasModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class RutasAdapter extends ArrayAdapter<RutasModel> {

    private final int mResource;
    private final List<RutasModel> misRutas;
    private final Context context;

    public RutasAdapter(@NonNull Context context, int resource, @NonNull List<RutasModel> misRutas) {
        super(context, resource, misRutas);
        this.context = context;
        this.mResource = resource;
        this.misRutas = misRutas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.imageView = convertView.findViewById(R.id.imgRuta);
            holder.titulo = convertView.findViewById(R.id.Titulo);
            holder.comunidad = convertView.findViewById(R.id.Comunidad);
            holder.tipoMoto = convertView.findViewById(R.id.TipoMoto);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        RutasModel ruta = misRutas.get(position);

        String base64Image = ruta.getImage();
        if (base64Image != null && base64Image.startsWith("data:image")) {
            base64Image = base64Image.split(",")[1];
        }

        try {
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Glide.with(context)
                    .asBitmap()
                    .load(decodedString)
                    .into(holder.imageView);
        } catch (IllegalArgumentException e) {
            Glide.with(context)
                    .load(R.drawable.favicon)  // Usa una imagen de respaldo en caso de error
                    .into(holder.imageView);
        }

        holder.titulo.setText(ruta.getTitle());
        holder.comunidad.setText(ruta.getComunidad());
        holder.tipoMoto.setText(ruta.getTipoMoto());

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView titulo;
        TextView comunidad;
        TextView tipoMoto;
    }
}
