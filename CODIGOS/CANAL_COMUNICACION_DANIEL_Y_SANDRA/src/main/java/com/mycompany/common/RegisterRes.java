package com.mycompany.common;

public class RegisterRes extends Mensaje {

    private boolean exito;
    private String respuesta;

    public RegisterRes(boolean exito, String respuesta) {
        super(Primitiva.REGISTER_RES);
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
        return "REGISTER_RES|" + exito + "|" + respuesta;
    }
}
