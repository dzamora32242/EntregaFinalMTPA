package com.mycompany.common;

public class LoginRes extends Mensaje {

    private boolean exito;
    private String respuesta;

    public LoginRes(boolean exito, String respuesta) {
        super(Primitiva.LOGIN_RES);
        this.exito = exito;
        this.respuesta = respuesta;
    }

    public boolean getExito() {
        return exito;
    }

    public String getRespuesta() {
        return respuesta;
    }

    @Override
    public String trasnformacionString() {
        return "LOGIN_RES|" + exito + "|" + respuesta;
    }
}
