package com.example.proyecto.modelos;

import java.io.Serializable;
import java.util.ArrayList;

public class Usuario implements Serializable {
    private int u_id, u_cantidadPartidasJugadas = 0, u_cantidadPartidasGanadas = 0, u_cantidadAmigos, u_nivel, u_experiencia;
    private String u_alias, u_password, u_rol, u_picture;
    private String u_fechaNacimiento;
    public static ArrayList<Usuario> usuarios = new ArrayList();
    public static Usuario usuarioLogueado = new Usuario();
    private ArrayList <Carta> mazo = new ArrayList<>();
    private ArrayList <Carta> mazoCentral = new ArrayList<>();
    private ArrayList <Carta> mazoOpcional = new ArrayList<>();
    private boolean turno = false;
    private boolean eliminado = false;
    private int edad;
    private boolean doncella = false;
    private boolean espia = false;
    private int ficha = 0;
    private boolean ganado = false;

    public Usuario(int u_id, int u_cantidadPartidasJugadas, int u_cantidadPartidasGanadas, int u_cantidadAmigos, int u_nivel, int u_experiencia, String u_alias, String u_password, String u_rol, String u_picture, String u_fechaNacimiento) {
        this.u_id = u_id;
        this.u_cantidadPartidasJugadas = u_cantidadPartidasJugadas;
        this.u_cantidadPartidasGanadas = u_cantidadPartidasGanadas;
        this.u_cantidadAmigos = u_cantidadAmigos;
        this.u_nivel = u_nivel;
        this.u_experiencia = u_experiencia;
        this.u_alias = u_alias;
        this.u_password = u_password;
        this.u_rol = u_rol;
        this.u_picture = u_picture;
        this.u_fechaNacimiento = u_fechaNacimiento;
    }
    public Usuario() {
    }
    public static class ChildClass implements Serializable {

        public ChildClass() {}
    }

    public boolean isGanado() {
        return ganado;
    }

    public void setGanado(boolean ganado) {
        this.ganado = ganado;
    }

    public int getFicha() {
        return ficha;
    }

    public void setFicha(int ficha) {
        this.ficha = ficha;
    }

    public boolean isEspia() {
        return espia;
    }

    public void setEspia(boolean espia) {
        this.espia = espia;
    }

    public boolean isDoncella() {
        return doncella;
    }

    public void setDoncella(boolean doncella) {
        this.doncella = doncella;
    }

    public ArrayList<Carta> getMazoOpcional() {
        return mazoOpcional;
    }

    public void setMazoOpcional(ArrayList<Carta> mazoOpcional) {
        this.mazoOpcional = mazoOpcional;
    }

    public ArrayList<Carta> getMazoCentral() {
        return mazoCentral;
    }

    public void setMazoCentral(ArrayList<Carta> mazoCentral) {
        this.mazoCentral = mazoCentral;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public int getU_id() {
        return u_id;
    }

    public void setU_id(int u_id) {
        this.u_id = u_id;
    }

    public int getU_cantidadPartidasJugadas() {
        return u_cantidadPartidasJugadas;
    }

    public void setU_cantidadPartidasJugadas(int u_cantidadPartidasJugadas) {
        this.u_cantidadPartidasJugadas = u_cantidadPartidasJugadas;
    }

    public boolean isTurno() {
        return turno;
    }

    public void setTurno(boolean turno) {
        this.turno = turno;
    }

    public int getU_cantidadPartidasGanadas() {
        return u_cantidadPartidasGanadas;
    }

    public void setU_cantidadPartidasGanadas(int u_cantidadPartidasGanadas) {
        this.u_cantidadPartidasGanadas = u_cantidadPartidasGanadas;
    }

    public int getU_cantidadAmigos() {
        return u_cantidadAmigos;
    }

    public void setU_cantidadAmigos(int u_cantidadAmigos) {
        this.u_cantidadAmigos = u_cantidadAmigos;
    }

    public int getU_nivel() {
        return u_nivel;
    }

    public void setU_nivel(int u_nivel) {
        this.u_nivel = u_nivel;
    }

    public int getU_experiencia() {
        return u_experiencia;
    }

    public void setU_experiencia(int u_experiencia) {
        this.u_experiencia = u_experiencia;
    }

    public String getU_alias() {
        return u_alias;
    }

    public void setU_alias(String u_alias) {
        this.u_alias = u_alias;
    }

    public String getU_password() {
        return u_password;
    }

    public void setU_password(String u_password) {
        this.u_password = u_password;
    }

    public String getU_rol() {
        return u_rol;
    }

    public void setU_rol(String u_rol) {
        this.u_rol = u_rol;
    }

    public String getU_picture() {
        return u_picture;
    }

    public void setU_picture(String u_picture) {
        this.u_picture = u_picture;
    }

    public String getU_fechaNacimiento() {
        return u_fechaNacimiento;
    }

    public void setU_fechaNacimiento(String u_fechaNacimiento) {
        this.u_fechaNacimiento = u_fechaNacimiento;
    }

    public ArrayList<Carta> getMazo() {
        return mazo;
    }

    public void setMazo(ArrayList<Carta> mazo) {
        this.mazo = mazo;
    }

    public static void setUsuarios(ArrayList usuarios) {
        Usuario.usuarios = usuarios;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "u_id=" + u_id +
                ", u_cantidadPartidasJugadas=" + u_cantidadPartidasJugadas +
                ", u_cantidadPartidasGanadas=" + u_cantidadPartidasGanadas +
                ", u_cantidadAmigos=" + u_cantidadAmigos +
                ", u_nivel=" + u_nivel +
                ", u_experiencia=" + u_experiencia +
                ", u_alias='" + u_alias + '\'' +
                ", u_password='" + u_password + '\'' +
                ", u_rol='" + u_rol + '\'' +
                ", u_picture='" + u_picture + '\'' +
                ", u_fechaNacimiento='" + u_fechaNacimiento + '\'' +
                '}';
    }
}
