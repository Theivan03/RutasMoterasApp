package com.example.rutasmoterasapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.rutasmoterasapi.RutasModel;

import java.util.List;

public class RutasAdapter extends ArrayAdapter<RutasModel> {

    private int mResource;
    private List<RutasModel> misRutas;
    Context context;
    public RutasAdapter(@NonNull Context context, int resource, @NonNull List<RutasModel> misRutas) {
        super(context, resource, misRutas);
        this.context = context;
        this.mResource = resource;
        this.misRutas = misRutas;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        View mifila = inflater.inflate(mResource, parent, false);

        ImageView imageView = mifila.findViewById(R.id.imgRuta);
        String imageUrl = misRutas.get(position).getImage();

        Glide.with(context)
                .load(imageUrl)
                .into(imageView);

        TextView Titulo = mifila.findViewById(R.id.Titulo);
        Titulo.setText(misRutas.get(position).getTitle());

        TextView Comunidad = mifila.findViewById(R.id.Comunidad);
        Comunidad.setText(misRutas.get(position).getComunidad());

        TextView TipoMoto = mifila.findViewById(R.id.TipoMoto);
        TipoMoto.setText(misRutas.get(position).getTipoMoto());

        return mifila;
    }
}
