package com.mycompany.common;

public class SendChannelNotification extends Mensaje {

    private String usuario;
    private String destino;
    private String contenido;

    public SendChannelNotification(String usuario, String destino, String contenido) {
        super(Primitiva.SEND_CHANNEL_NOTIFICATION);
        this.usuario = usuario;
        this.destino = destino;
        this.contenido = contenido;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getDestino() {
        return destino;
    }

    public String getContenido() {
        return contenido;
    }

    @Override
    public String toEncodedString() {
        return "SEND_CHANNEL_NOTIFICATION|" + usuario + "|" + destino + "|" + contenido;
    }
}
