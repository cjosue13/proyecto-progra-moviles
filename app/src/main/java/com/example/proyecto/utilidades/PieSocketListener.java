package com.example.proyecto.utilidades;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.proyecto.controladores.GameActivity;
import com.example.proyecto.controladores.WaitingRoomActivity;
import com.example.proyecto.modelos.Carta;
import com.example.proyecto.modelos.Partida;
import com.example.proyecto.modelos.Usuario;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.stream.Collectors;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

import static android.widget.Toast.*;
import static com.example.proyecto.controladores.GameActivity.txv_turno;
import static com.example.proyecto.controladores.WaitingRoomActivity.*;

public class PieSocketListener extends WebSocketListener {

    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private String text;
    private Context context;
    private Partida partida;
    private String administrador;
    private LinearLayout parentLayout2;
    private Usuario usuario;
    private ImageView img1, img2, img3;
    private ArrayList<Usuario> usuarios;
    private TextView turno;

    public PieSocketListener(String text, Context context, Partida partida, String administrador, LinearLayout parentLayout2, Usuario usuario) {
        this.text = text;
        this.partida = partida;
        this.administrador = administrador;
        this.usuario = usuario;
        this.parentLayout2 = parentLayout2;
        this.context = context;
    }

    public ImageView getImg3() {
        return img3;
    }

    public void setImg3(ImageView img3) {
        this.img3 = img3;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ImageView getImg1() {
        return img1;
    }

    public void setImg1(ImageView img1) {
        this.img1 = img1;
    }

    public ImageView getImg2() {
        return img2;
    }

    public void setImg2(ImageView img2) {
        this.img2 = img2;
    }

    public PieSocketListener(String text) {
        this.text = text;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        webSocket.send(this.text);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Carta cartaAux2;
        if (text.equals("nuevoUsuario-" + partida.getP_id())) {
            HiloWaiting hilo = new HiloWaiting(this.context, this.parentLayout2, this.usuario, this.partida);
            hilo.execute();
        } else if (text.equals("inicio partida")) {
            Intent intent = new Intent(this.context, GameActivity.class);
            intent.putExtra("administrador", administrador);
            intent.putExtra("usuario", usuario);
            context.startActivity(intent);
        } else {
            String[] arrSplit_2 = text.split(",", 5);
            switch (arrSplit_2[0]) {
                case "enviarCartas":
                    String carta1 = arrSplit_2[1];
                    int valor = Integer.valueOf(arrSplit_2[2]);
                    String id = arrSplit_2[3];
                    Carta cartaAux = new Carta(carta1, valor);
                    if (id.equals(String.valueOf(usuario.getU_id()))) {
                        if (arrSplit_2[4].equals("mazoCentral")) {
                            usuario.getMazoCentral().add(cartaAux);
                        } else if(arrSplit_2[4].equals("mazoOpcional")){
                            usuario.getMazoOpcional().add(cartaAux);
                        }
                        else {
                            usuario.getMazo().add(cartaAux);
                            usuario.getMazo().add(null);
                            usuario.getMazo().add(null);
                            HiloImagenes hilo = new HiloImagenes(this.getContext(), this.getImg1(), this.getImg2(), this.getImg3(), carta1, null, null);
                            hilo.execute();
                        }
                    }
                    Usuario usuarioAux = (Usuario) WaitingRoomActivity.usuarios.stream().filter(x -> x.getU_id() == Integer.parseInt(id)).findAny().get();
                    if (arrSplit_2[4].equals("mazo")) {
                        usuarioAux.getMazo().add(cartaAux);
                        usuarioAux.getMazo().add(null);
                        usuarioAux.getMazo().add(null);
                    } else if(arrSplit_2[4].equals("mazoOpcional")){
                        usuarioAux.getMazoOpcional().add(cartaAux);
                    }
                    break;
                case "agregarCarta":
                    String id2 = arrSplit_2[1];
                    Carta carta = new Carta();
                    Usuario usuarioAux2 = (Usuario) WaitingRoomActivity.usuarios.stream().filter(x -> x.getU_id() == Integer.parseInt(id2)).findAny().get();
                    carta = usuario.getMazoCentral().get(usuario.getMazoCentral().size() - 1);
                    usuario.getMazoCentral().remove(carta);
                    cartaAux2 = new Carta();
                    usuarioAux2.getMazo().set((usuarioAux2.getMazo().get(0) != null ? 1 : 0), carta);
                    if (arrSplit_2.length == 3 && (arrSplit_2[2].equals("cancillerMode") && usuario.getMazoCentral().size() > 1)) {
                        cartaAux2 = usuario.getMazoCentral().get(usuario.getMazoCentral().size() - 1);
                        usuario.getMazoCentral().remove(cartaAux2);
                        usuarioAux2.getMazo().set(2, cartaAux2);
                    }
                    if (id2.equals(String.valueOf(usuario.getU_id()))) {
                        usuario.getMazo().set((usuario.getMazo().get(0) != null ? 1 : 0), carta);
                        if (arrSplit_2.length == 3 && (arrSplit_2[2].equals("cancillerMode") && usuario.getMazoCentral().size() > 1)) {
                            usuario.getMazo().set(2, cartaAux2);
                        }
                        HiloImagenes hilo = new HiloImagenes(this.getContext(),
                                this.getImg1(),
                                this.getImg2(),
                                this.getImg3(),
                                (this.getImg1().getDrawable() != null) ? null : carta.getNombre(),
                                (this.getImg2().getDrawable() != null) ? null : carta.getNombre(),
                                ((cartaAux2 != null) && (usuario.getMazoCentral().size() > 1)) ? cartaAux2.getNombre() : null);
                        hilo.execute();
                    }
                    break;
                case "cambioTurno":
                    System.out.println(usuario.getMazoOpcional() + " mazo opcional");
                    GameActivity.jugadorActual++;
                    System.out.println(GameActivity.jugadorActual);
                    if (WaitingRoomActivity.usuarios.get(GameActivity.jugadorActual).isEliminado()) {
                        System.out.println("entrando a condición #1");
                        GameActivity.jugadorActual++;
                        System.out.println(GameActivity.jugadorActual);
                        if (WaitingRoomActivity.usuarios.size() == GameActivity.jugadorActual) {
                            System.out.println("entrando a condición #2");
                            GameActivity.jugadorActual = 0;
                            System.out.println(GameActivity.jugadorActual);
                        }
                    }
                    System.out.println(WaitingRoomActivity.usuarios.size() + " == " + GameActivity.jugadorActual);
                    if (WaitingRoomActivity.usuarios.size() - 1 <= GameActivity.jugadorActual) {
                        System.out.println("entrando a condición #3");
                        cambioTurnoText();
                        System.out.println(GameActivity.jugadorActual);
                    }
                    if (GameActivity.jugadorActual != -1) {
                        System.out.println("entrando a condición #4");
                        cambioTurnoText();
                        System.out.println(GameActivity.jugadorActual);
                    }
                    break;
                case "princesaJugada":
                    aplicarPrincesa(arrSplit_2[1], "");
                    break;
                case "cancillerJugada":
                    String id3 = arrSplit_2[1];
                    String carta1Mazo = arrSplit_2[3];
                    String carta2Mazo = arrSplit_2[4];
                    Carta carta1MazoObject = creacionObjectoCarta(carta1Mazo);
                    Carta carta2MazoObject = creacionObjectoCarta(carta2Mazo);

                    Usuario usuarioAux3 = (Usuario) WaitingRoomActivity.usuarios.stream().filter(x -> x.getU_id() == Integer.parseInt(id3)).findAny().get();
                    agregarCartaMazoCentral(carta1MazoObject, carta2MazoObject);
                    if (arrSplit_2[2].equals("1")) {
                        usuarioAux3.getMazo().set(0, usuarioAux3.getMazo().get(0));
                        usuarioAux3.getMazo().set(1, null);
                        usuarioAux3.getMazo().set(2, null);
                    } else if (arrSplit_2[2].equals("2")) {
                        usuarioAux3.getMazo().set(0, usuarioAux3.getMazo().get(1));
                        usuarioAux3.getMazo().set(1, null);
                        usuarioAux3.getMazo().set(2, null);
                    } else if (arrSplit_2[2].equals("3")) {
                        usuarioAux3.getMazo().set(0, usuarioAux3.getMazo().get(2));
                        usuarioAux3.getMazo().set(1, null);
                        usuarioAux3.getMazo().set(2, null);
                    }
                    if (id3.equals(String.valueOf(usuario.getU_id()))) {
                        if (arrSplit_2[2].equals("1")) {
                            usuario.getMazo().set(0, usuario.getMazo().get(0));
                            usuario.getMazo().set(1, null);
                            usuario.getMazo().set(2, null);
                        } else if (arrSplit_2[2].equals("2")) {
                            usuario.getMazo().set(0, usuario.getMazo().get(1));
                            usuario.getMazo().set(1, null);
                            usuario.getMazo().set(2, null);
                        } else if (arrSplit_2[2].equals("3")) {
                            usuario.getMazo().set(0, usuario.getMazo().get(2));
                            usuario.getMazo().set(1, null);
                            usuario.getMazo().set(2, null);
                        }
                    }
                    break;
                case "reyJugado":
                    String idJugador = arrSplit_2[1];
                    String idPropio = arrSplit_2[2];
                    Carta cartaJugador = new Carta();
                    Carta cartaPropia = new Carta();

                    //Filtramos los dos usuarios que van a intercambiar cartas
                    Usuario usuario1 = (Usuario) WaitingRoomActivity.usuarios.stream().filter(x -> x.getU_id() == Integer.valueOf(idJugador)).findAny().get();
                    Usuario usuario2 = (Usuario) WaitingRoomActivity.usuarios.stream().filter(x -> x.getU_id() == Integer.valueOf(idPropio)).findAny().get();

                    // Se obtienen las cartas
                    cartaJugador = usuario1.getMazo().get(0) != null ? usuario1.getMazo().get(0) : usuario1.getMazo().get(1);
                    cartaPropia = usuario2.getMazo().get(0) != null ? usuario2.getMazo().get(0) : usuario2.getMazo().get(1);

                    // En el caso de los dos jugadores entonces entra
                    if (usuario.getU_id() == Integer.valueOf(idJugador) || usuario.getU_id() == Integer.valueOf(idPropio)) {
                        if (usuario1.getU_id() == usuario.getU_id()) {
                            usuario.getMazo().set(0, usuario2.getMazo().get(0) != null ? usuario2.getMazo().get(0) : usuario2.getMazo().get(1));
                        } else { // cuando es el otro
                            usuario.getMazo().set(0, usuario1.getMazo().get(0) != null ? usuario1.getMazo().get(0) : usuario1.getMazo().get(1));
                        }
                        usuario.getMazo().set(1, null);
                        IntercambioCartas();
                    }

                    //Se intercambian las cartas
                    usuario1.getMazo().set(0, cartaPropia); // usuario1 es al que se le cambian las cartas
                    usuario2.getMazo().set(0, cartaJugador); // usuario2 soy yo
                    usuario1.getMazo().set(1, null);
                    usuario2.getMazo().set(1, null);

                    break;
                case "SacarCarta":
                    String ID = arrSplit_2[1];
                    String index = arrSplit_2[2];
                    Usuario Usu = (Usuario) WaitingRoomActivity.usuarios.stream().filter(x -> x.getU_id() == Integer.valueOf(ID)).findAny().get();
                    Usu.getMazo().set(Integer.parseInt(index), null);
                    break;
                case "principeJugado":
                    String idJug = arrSplit_2[1];
                    Carta cartaJug = new Carta();
                    Usuario usuarioSelect = (Usuario) WaitingRoomActivity.usuarios.stream().filter(x -> x.getU_id() == Integer.valueOf(idJug)).findAny().get();
                    cartaJug = (usuarioSelect.getMazo().get(0) != null) ? usuarioSelect.getMazo().get(0) : usuarioSelect.getMazo().get(1);
                    usuarioSelect.getMazo().set(usuarioSelect.getMazo().get(0) != null ? 0 : 1, null);
                    if (!cartaJug.getNombre().equals("princesa")) {
                        if(usuario.getMazoCentral().size() != 0){
                            Carta cartaJug2 = usuario.getMazoCentral().get(usuario.getMazoCentral().size() - 1);
                            usuario.getMazoCentral().remove(cartaJug2);
                            usuarioSelect.getMazo().set(0, cartaJug2);
                            if (Integer.valueOf(idJug) == usuario.getU_id()) {
                                usuario.getMazo().set((usuario.getMazo().get(0) != null) ? 0 : 1, null);
                                usuario.getMazo().set(0, cartaJug2);
                                quitarCartaPrincipe(cartaJug, "mazoCentral");
                            }
                        }else{
                            Carta cartaJug2 = usuario.getMazoOpcional().get(usuario.getMazoOpcional().size() - 1);
                            usuario.getMazoOpcional().remove(cartaJug2);
                            usuarioSelect.getMazo().set(0, cartaJug2);
                            if (Integer.valueOf(idJug) == usuario.getU_id()) {
                                usuario.getMazo().set((usuario.getMazo().get(0) != null) ? 0 : 1, null);
                                usuario.getMazo().set(0, cartaJug2);
                                quitarCartaPrincipe(cartaJug, "mazoOpcional");
                            }
                        }

                    }
                    if (cartaJug.getNombre().equals("princesa")) {
                        aplicarPrincesa(idJug, "principe");
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + arrSplit_2[0]);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void aplicarPrincesa(String idJug, String tipo) {
        Usuario user = (Usuario) WaitingRoomActivity.usuarios.stream().filter(x -> x.getU_id() == Integer.parseInt(idJug)).findAny().get();
        user.setEliminado(true);
        if(tipo.equals("principe")){
            quitarCartaPrincipe2(user.getU_alias());
        }else{
            princesaJugada(this.context, user);
        }
    }

    public void quitarCartaPrincipe2(String nombre) {
        new AsyncTask<String, Float, Integer>() {
            @Override
            protected Integer doInBackground(String... strings) {
                publishProgress();
                return null;
            }

            @Override
            protected void onProgressUpdate(Float... variable) {
                makeText(context, "Han aplicado un principe sobre " + nombre + " y ha perdido el juego por jugar la princesa", LENGTH_LONG).show();
            }
        }.execute();
    }

    public void quitarCartaPrincipe(Carta carta, String mazo) {
        new AsyncTask<String, Float, Integer>() {
            @Override
            protected Integer doInBackground(String... strings) {
                publishProgress();
                return null;
            }

            @Override
            protected void onProgressUpdate(Float... variable) {
                int code = context.getResources().getIdentifier(carta.getNombre(), "drawable", context.getPackageName());
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(code);
                imageView.setLayoutParams(new LinearLayout.LayoutParams(calcularPixeles(63), calcularPixeles(100)));
                img2.setImageDrawable(null);
                GameActivity.cartasContainer.addView(imageView);

                int code2 = context.getResources().getIdentifier(usuario.getMazo().get(0).getNombre(), "drawable", context.getPackageName());
                img1.setImageResource(code2);
                if(mazo.equals("mazoOpcional")){
                    makeText(context, "Han aplicado un principe sobre ti usando el mazo opcional, por lo tanto has botado tu carta y recogido otra", LENGTH_LONG).show();
                }else{
                    makeText(context, "Han aplicado un principe sobre ti, por lo tanto has botado tu carta y recogido otra", LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    public void agregarCartaMazoCentral(Carta carta1MazoObject, Carta carta2MazoObject) {
        if (usuario.getMazoCentral().size() > 0) {
            usuario.getMazoCentral().add(carta1MazoObject);
        }
        if (usuario.getMazoCentral().size() > 1) {
            usuario.getMazoCentral().add(carta2MazoObject);
        }
    }

    public Carta creacionObjectoCarta(String carta) {
        Carta cartaretorno = new Carta();
        switch (carta) {
            case "espia":
                cartaretorno.setNombre(carta);
                cartaretorno.setValor(0);
                break;
            case "guardia":
                cartaretorno.setNombre(carta);
                cartaretorno.setValor(1);
                break;
            case "sacerdote":
                cartaretorno.setNombre(carta);
                cartaretorno.setValor(2);
                break;
            case "baron":
                cartaretorno.setNombre(carta);
                cartaretorno.setValor(3);
                break;
            case "doncella":
                cartaretorno.setNombre(carta);
                cartaretorno.setValor(4);
                break;
            case "principe":
                cartaretorno.setNombre(carta);
                cartaretorno.setValor(5);
                break;
            case "canciller":
                cartaretorno.setNombre(carta);
                cartaretorno.setValor(6);
                break;
            case "rey":
                cartaretorno.setNombre(carta);
                cartaretorno.setValor(7);
                break;
            case "condesa":
                cartaretorno.setNombre(carta);
                cartaretorno.setValor(8);
                break;
            case "princesa":
                cartaretorno.setNombre(carta);
                cartaretorno.setValor(9);
                break;
        }
        return cartaretorno;
    }

    public void princesaJugada(Context context, Usuario usuario) {
        new AsyncTask<String, Float, Integer>() {
            @Override
            protected Integer doInBackground(String... strings) {
                publishProgress();
                return null;
            }

            @Override
            protected void onProgressUpdate(Float... variable) {
                makeText(context, usuario.getU_alias() + " ha perdido la ronda por haber jugado la princesa", LENGTH_LONG).show();
            }
        }.execute();
    }

    public void cambioTurnoText() {
        new AsyncTask<String, Float, Integer>() {
            @Override
            protected Integer doInBackground(String... strings) {
                publishProgress();
                return null;
            }

            @Override
            protected void onProgressUpdate(Float... variable) {
                System.out.println("entrando a hilo de cambio de turno");
                if (GameActivity.jugadorActual >= 0) {
                    System.out.println("entrando a condición");
                    txv_turno.setText(WaitingRoomActivity.usuarios.get(GameActivity.jugadorActual).getU_alias());
                    if (WaitingRoomActivity.usuarios.size() - 1 <= GameActivity.jugadorActual) {
                        System.out.println("entrando a condición #5");
                        GameActivity.jugadorActual = -1;
                    }
                }

            }
        }.execute();
    }

    public void IntercambioCartas() {
        new AsyncTask<String, Float, Integer>() {
            @Override
            protected Integer doInBackground(String... strings) {
                publishProgress();
                return null;
            }

            @Override
            protected void onProgressUpdate(Float... variable) {
                getImg2().setImageDrawable(null);
                int code = context.getResources().getIdentifier(usuario.getMazo().get(0).getNombre(), "drawable",
                        context.getPackageName());
                getImg1().setImageResource(code);
            }
        }.execute();
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        System.out.println("Error : " + t.getMessage());
    }

    public void enviarMensaje(WebSocket webSocket, String text) {
        webSocket.send(text);
    }

    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(ArrayList<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public TextView getTurno() {
        return turno;
    }

    public void setTurno(TextView turno) {
        this.turno = turno;
    }

    public void output(String text) {
        Log.d("PieSocket", text);
    }

    public int calcularPixeles(int dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }
}
