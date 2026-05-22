package com.mycompany.common;

public class LogoutRes extends Mensaje {

    private String respuesta;

    public LogoutRes(String respuesta) {
        super(Primitiva.LOGOUT_RES);
        this.respuesta = respuesta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    @Override
    public String toEncodedString() {
        return "LOGOUT_RES|" + respuesta;
    }
}
