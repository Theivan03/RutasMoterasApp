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

        if(misRutas.get(position).getComunidad().equals("ComunidadValenciana")){
            misRutas.get(position).setComunidad("Comunidad Valenciana");
        }if(misRutas.get(position).getComunidad().equals("CastillaLaMancha")){
            misRutas.get(position).setComunidad("Castilla La Mancha");
        }if(misRutas.get(position).getComunidad().equals("CastillaLeon")){
            misRutas.get(position).setComunidad("Castilla León");
        }if(misRutas.get(position).getComunidad().equals("IslasBaleares")){
            misRutas.get(position).setComunidad("Islas Baleares");
        }if(misRutas.get(position).getComunidad().equals("IslasCanarias")){
            misRutas.get(position).setComunidad("Islas Canarias");
        }if(misRutas.get(position).getComunidad().equals("LaRioja")){
            misRutas.get(position).setComunidad("La Rioja");
        }if(misRutas.get(position).getComunidad().equals("PaisVasco")){
            misRutas.get(position).setComunidad("País Vasco");
        }

        ImageView imageView = mifila.findViewById(R.id.imgRuta);
        String base64Image = misRutas.get(position).getImage();

        if (base64Image != null && base64Image.startsWith("data:image")) {
            base64Image = base64Image.split(",")[1];
        }

        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);

        Glide.with(context)
                .asBitmap()
                .load(decodedString)
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
