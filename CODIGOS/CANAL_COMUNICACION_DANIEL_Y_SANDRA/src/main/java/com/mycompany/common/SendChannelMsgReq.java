package com.mycompany.common;

public class SendChannelMsgReq extends Mensaje {

    private String destino;
    private String contenido;
    private boolean esUnSalon;

    public SendChannelMsgReq(String destino, String contenido, boolean esUnSalon) {
        super(Primitiva.SEND_CHANNEL_MSG_REQ);
        this.destino = destino;
        this.contenido = contenido;
        this.esUnSalon = esUnSalon;
    }

    public String getDestino() {
        return destino;
    }

    public String getContenido() {
        return contenido;
    }

    public boolean getEsUnSalon() {
        return esUnSalon;
    }

    @Override
    public String toEncodedString() {
        return "SEND_CHANNEL_MSG_REQ|" + destino + "|" + contenido + "|" + esUnSalon;
    }
}
