package com.mycompany.common;

public class SendPrivNotification extends Mensaje {

    private String origen;
    private String destino;
    private String contenido;

    public SendPrivNotification(String origen, String destino, String contenido) {
        super(Primitiva.SEND_PRIV_NOTIFICATION);
        this.origen = origen;
        this.destino = destino;
        this.contenido = contenido;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public String getContenido() {
        return contenido;
    }

    @Override
    public String trasnformacionString() {
        return "SEND_PRIV_NOTIFICATION|" + origen + "|" + destino + "|" + contenido;
    }
}
