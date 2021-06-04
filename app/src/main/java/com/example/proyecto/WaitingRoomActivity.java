package com.example.proyecto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto.connection.PieSocketListener;
import com.example.proyecto.login.LoginActivity;
import com.example.proyecto.partida.CrearPartidaActivity;
import com.example.proyecto.partida.Partida;
import com.example.proyecto.partida.PartidaActivity;
import com.example.proyecto.partida.PartidaRequest;
import com.example.proyecto.registro.RegisterActivity;
import com.example.proyecto.usuarios.Usuario;
import com.example.proyecto.usuarios.UsuariosActivity;
import com.example.proyecto.usuarios.UsuariosRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class WaitingRoomActivity extends AppCompatActivity {

    private TextView mTextView;
    private LinearLayout parentLayout;
    private LinearLayout parentLayout2;
    public static ArrayList<Usuario> usuarios = new ArrayList<>();
    private Partida partida;
    private String administrador;
    private ImageView imageViewStart;
    private ConstraintLayout parentLayout3;
    private int cantidadUsuarios = 0;
    private PieSocketListener listener;
    private WebSocket ws;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waitingroom);
        parentLayout2 = (LinearLayout) findViewById(R.id.parentLayout2);
        parentLayout3 = (ConstraintLayout) findViewById(R.id.parentLayout3);
        mTextView = (TextView) findViewById(R.id.text);
        imageViewStart = (ImageView) findViewById(R.id.imageViewStart);
        Intent i = this.getIntent();
        partida = (Partida) i.getSerializableExtra("partida");
        administrador = i.getStringExtra("administrador");
        parentLayout3.removeView(administrador.equals("true") ? null : imageViewStart);
        //CargarUsuarios("cargarUsuarios");

        if (i.getStringExtra("listenerPieSocket").equals("true")) {
            //web socket
            OkHttpClient client = new OkHttpClient();
            Log.d("PieSocket", "Connecting");
            String apiKey = "dwRO3yR7VvymQk1HfYHqJBK22coq0TnEW90aqcN4"; //Demo key, get yours at https://piesocket.com
            int channelId = 1;
            Request request = new Request.Builder()
                    .url("wss://us-nyc-1.websocket.me/v3/1?api_key=dwRO3yR7VvymQk1HfYHqJBK22coq0TnEW90aqcN4&notify_self")
                    .build();
            listener = new PieSocketListener("nuevoUsuario-" + partida.getP_id(),
                    this, partida, administrador, parentLayout2, Usuario.usuarioLogueado);
            ws = client.newWebSocket(request, listener);
        }
    }



    public void agregarWaitingRooms(Usuario usuario) {
        // Agregando usuarios
        LinearLayout userLinear = new LinearLayout(this);
        userLinear.setOrientation(LinearLayout.HORIZONTAL);
        userLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        userLinear.setBackgroundColor((Color.parseColor("#6D6969")));
        userLinear.setPadding(calcularPixeles(10), calcularPixeles(10), calcularPixeles(10), calcularPixeles(10));

        ImageView imageView = new ImageView(this);

        byte[] bytes = Base64.decode(usuario.getU_picture(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView.setImageBitmap(bitmap);

        imageView.setLayoutParams(new LinearLayout.LayoutParams(calcularPixeles(90),
                calcularPixeles(90)));
        userLinear.addView(imageView);

        LinearLayout dataLinear = new LinearLayout(this);
        dataLinear.setOrientation(LinearLayout.VERTICAL);
        dataLinear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
        dataLinear.setPadding(calcularPixeles(10), calcularPixeles(10), calcularPixeles(10), calcularPixeles(10));

        TextView txt_alias = new TextView(this);
        txt_alias.setText("Alias: " + usuario.getU_alias());
        txt_alias.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                50));
        txt_alias.setTextColor(Color.WHITE);
        TextView txt_nivel = new TextView(this);
        txt_nivel.setText("Nivel: " + usuario.getU_nivel());
        txt_nivel.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                50));
        txt_nivel.setTextColor(Color.WHITE);
        TextView txt_fechaNacimiento = new TextView(this);
        txt_fechaNacimiento.setText("Fec.Nac:" + usuario.getU_fechaNacimiento());
        txt_fechaNacimiento.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                50));
        txt_fechaNacimiento.setTextColor(Color.WHITE);
        dataLinear.addView(txt_alias);
        dataLinear.addView(txt_nivel);
        dataLinear.addView(txt_fechaNacimiento);

        parentLayout2.addView(userLinear);
        userLinear.addView(dataLinear);
    }

    public int calcularPixeles(int dps) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    private void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public void empezarPartida(View view) {
        //Redirecciona a la otra vista
        if (usuarios.size() >= partida.getP_cantidadJugadores()  ) {
            /*Intent nextActivity = new Intent(WaitingRoomActivity.this, GameActivity.class);
            WaitingRoomActivity.this.startActivity(nextActivity);*/
            listener.enviarMensaje(ws,"inicio partida");
        } else {
            AlertDialog.Builder alerta = new AlertDialog.Builder(WaitingRoomActivity.this);
            alerta.setMessage("Cantidad de jugadores insuficiente").setNegativeButton("Reintentar", null).create().show();
        }
    }

    public void volverLobby(View view) {
        Response.Listener<String> respuesta = new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(String response) {
                try {
                    response = response.replaceFirst("<font>.*?</font>", "");
                    int jsonStart = response.indexOf("{");
                    int jsonEnd = response.lastIndexOf("}");
                    if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart) {
                        response = response.substring(jsonStart, jsonEnd + 1);
                    }

                    JSONObject jsonRespuesta = new JSONObject(response);
                    boolean ok = jsonRespuesta.getBoolean("success");

                    if (ok) {
                        Intent nextActivity = new Intent(WaitingRoomActivity.this, PartidaActivity.class);
                        WaitingRoomActivity.this.startActivity(nextActivity);
                        WaitingRoomActivity.this.finish();
                    } else {
                        AlertDialog.Builder alerta = new AlertDialog.Builder(WaitingRoomActivity.this);
                        alerta.setMessage("Fallo al salir de partida").setNegativeButton("Reintentar", null).create().show();
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            }
        };

        PartidaRequest r = new PartidaRequest(String.valueOf(Usuario.usuarioLogueado.getU_id()), String.valueOf(partida.getP_id()), respuesta);
        RequestQueue cola = Volley.newRequestQueue(WaitingRoomActivity.this);
        cola.add(r);
    }
}