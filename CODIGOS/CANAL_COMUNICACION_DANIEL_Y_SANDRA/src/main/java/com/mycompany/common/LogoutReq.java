package com.mycompany.common;

public class LogoutReq extends Mensaje {

    private String nombreUsuario;

    public LogoutReq(String nombreUsuario) {
        super(Primitiva.LOGOUT_REQ);
        this.nombreUsuario = nombreUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    @Override
    public String trasnformacionString() {
        return "LOGOUT_REQ|" + nombreUsuario;
    }
}
