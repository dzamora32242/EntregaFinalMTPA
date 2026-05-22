package com.mycompany.common;

public class GetChannelsReq extends Mensaje {
  
  public GetChannelsReq() {
    super(Primitiva.GET_CHANNELS_REQ);
  }

  @Override
  public String toEncodedString() {
    return "GET_CHANNELS_REQ";
  }
}
