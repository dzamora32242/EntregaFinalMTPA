package com.mycompany.common;

public class LoginReq extends Mensaje {

    private String nombreUsuario;
    private String contrasena;

    public LoginReq(String nombreUsuario, String contrasena) {
        super(Primitiva.LOGIN_REQ);
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    @Override
    public String toEncodedString() {
        return "LOGIN_REQ|" + nombreUsuario + "|" + contrasena;
    }
}
