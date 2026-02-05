package com.alv.mastertools.models;

public class Usuario {

    private String username;
    private String rol;
    private String password;

    public Usuario(String username, String rol, String password) {
        this.username = username;
        this.rol = rol;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getRol() {
        return rol;
    }

    public String getPassword() {
        return password;
    }

}
