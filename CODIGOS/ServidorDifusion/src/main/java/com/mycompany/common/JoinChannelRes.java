package com.mycompany.common;

public class JoinChannelRes extends Mensaje {

  private boolean exito;
  private String respuesta;

  public JoinChannelRes(boolean exito, String respuesta) {
    super(Primitiva.JOIN_CHANNEL_RES);
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
  public String toEncodedString() {
    return "JOIN_CHANNEL_RES|" + exito + "|" + respuesta;
  }
}
