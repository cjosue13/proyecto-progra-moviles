package com.example.proyecto.utilidades;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.Serializable;

public class HiloImagenes extends AsyncTask<String, Float, Integer> implements Serializable {
    private View v;
    private static class View implements Serializable {
        Context contexto;
        ImageView img1, img2, img3;
        String carta1, carta2, carta3;
    }
    public HiloImagenes(Context contexto, ImageView img1, ImageView img2, ImageView img3, String carta1, String carta2, String carta3) {
        v = new View();
        v.contexto = contexto;
        v.img1 = img1;
        v.img2 = img2;
        v.img3 = img3;
        v.carta1 = carta1;
        v.carta2 = carta2;
        v.carta3 = carta3;
    }

    @Override
    protected void onPreExecute() {
    }
    @Override
    protected Integer doInBackground(String... variable_no_usada) {
        publishProgress();
        return 1;
    }

    @Override
    protected void onProgressUpdate(Float... ejes) {
        int code;
        if(this.v.carta1 != null) {
            code = this.v.contexto.getResources().getIdentifier(this.v.carta1, "drawable",
                    this.v.contexto.getPackageName());
            this.v.img1.setImageResource(code);
        }
        else if(this.v.carta2 != null){
            code = this.v.contexto.getResources().getIdentifier(this.v.carta2, "drawable",
                    this.v.contexto.getPackageName());
            this.v.img2.setImageResource(code);
        }
        if(this.v.carta3 != null) {
            code = this.v.contexto.getResources().getIdentifier(this.v.carta3, "drawable",
                    this.v.contexto.getPackageName());
            this.v.img3.setImageResource(code);
        }
    }
    @Override
    protected void onPostExecute(Integer variable_no_usada) {
    }
    @Override
    protected void onCancelled (Integer variable_no_usada) {
    }
}
