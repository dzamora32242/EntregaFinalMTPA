package com.mycompany.common;

public class ErrorRes extends Mensaje {

    private String mensaje;

    public ErrorRes(String mensaje) {
        super(Primitiva.ERROR_RES);
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    @Override
    public String trasnformacionString() {
        return "ERROR_RES|" + mensaje;
    }
}
