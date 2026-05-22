package com.mycompany.common;

public class JoinChannelReq extends Mensaje {

  private String nombreSalon;

  public JoinChannelReq(String nombreSalon) {
    super(Primitiva.JOIN_CHANNEL_REQ);
    this.nombreSalon = nombreSalon;
  }

  public String getNombreSalon() {
    return nombreSalon;
  }

  @Override
  public String toEncodedString() {
    return "JOIN_CHANNEL_REQ|" + nombreSalon;
  }
}
