package com.mycompany.common;

public class GetChannelsRes extends Mensaje {

  private boolean exito;
  private String respuesta;

  public GetChannelsRes(boolean exito, String respuesta) {
    super(Primitiva.GET_CHANNELS_RES);
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
    return "GET_CHANNELS_RES|" + exito + "|" + respuesta;
  }
}
