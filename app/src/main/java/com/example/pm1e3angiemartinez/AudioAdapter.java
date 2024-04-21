package com.example.pm1e3angiemartinez;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pm1e3angiemartinez.Configuracion.Entrevista;
import com.example.pm1e3angiemartinez.Configuracion.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder>{

    private Context context;
    private List<Entrevista> listaEntrevistas;
    private List<Boolean> seleccionados;

    public AudioAdapter(Context context, List<Entrevista> listaEntrevistas) {
        this.context = context;
        this.listaEntrevistas = listaEntrevistas;
        seleccionados = new ArrayList<>(Collections.nCopies(listaEntrevistas.size(), false));
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_audio, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position)  {
        Entrevista entrevista = listaEntrevistas.get(position);

        // Asignar datos a las vistas en el ViewHolder
        holder.txtId.setText(entrevista.getId());
        holder.txtDescripcion.setText(entrevista.getDescripcion());
        holder.textViewPeriodista.setText(entrevista.getPeriodista());
        holder.textViewFecha.setText(entrevista.getFecha());
        Bitmap imagenBitmap = Utilities.base64ToBitmap(entrevista.getImagenBase64());
        holder.imageViewEntrevista.setImageBitmap(imagenBitmap);

        // Manejo del clic del botón de reproducción
        holder.btnReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para reproducir el audio
                String audioBase64 = entrevista.getAudioBase64();
                reproducirAudioBase64(audioBase64);
            }
        });

        // Guardar el ID de Firestore junto con los datos de la entrevista
        holder.itemView.setTag(entrevista.getIdFirestore());

        // Manejar clics en los elementos de la lista
        holder.itemView.setOnClickListener(v -> {
            seleccionados.set(position, !seleccionados.get(position));
            holder.itemView.setActivated(seleccionados.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return listaEntrevistas.size();
    }

    private void reproducirAudioBase64(String audioBase64) {
        try {
            byte[] audioData = Base64.decode(audioBase64, Base64.DEFAULT);
            File outputFile = File.createTempFile("audio_temp", ".mp3", context.getCacheDir());
            FileOutputStream fos = new FileOutputStream(outputFile);
            fos.write(audioData);
            fos.close();

            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(outputFile.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class AudioViewHolder extends RecyclerView.ViewHolder {
        TextView txtId, txtDescripcion, textViewFecha, textViewPeriodista;
        ImageView imageViewEntrevista;
        Button btnReproducir;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar vistas
            txtId = itemView.findViewById(R.id.text_id);
            txtDescripcion = itemView.findViewById(R.id.textViewDescripcion);
            textViewFecha = itemView.findViewById(R.id.textViewFecha);
            textViewPeriodista = itemView.findViewById(R.id.textViewPeriodista);
            imageViewEntrevista = itemView.findViewById(R.id.imageViewEntrevista);
            btnReproducir = itemView.findViewById(R.id.btnReproducir);
        }
    }

}

