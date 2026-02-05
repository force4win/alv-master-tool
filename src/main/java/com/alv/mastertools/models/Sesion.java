package com.alv.mastertools.models;

public class Sesion {
    private static Sesion instance;
    private Usuario usuarioLogueado;

    private Sesion() {
    }

    public static Sesion get() {
        if (instance == null) {
            instance = new Sesion();
        }
        return instance;
    }

    public Usuario getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Usuario usuario) {
        this.usuarioLogueado = usuario;
    }

    // ESTE ES EL MÉTODO QUE TE FALTA
    public void cerrarSesion() {
        this.usuarioLogueado = null;
    }
}
