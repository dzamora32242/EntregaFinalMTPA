package com.mycompany.servidordifusion;

public class MensajeUsuario {
  private String emisor;
  private String contenido;
  private long tiempo;

  public MensajeUsuario(String emisor, String contenido) {
    this.emisor = emisor;
    this.contenido = contenido;
    tiempo = System.currentTimeMillis();
  }

  public MensajeUsuario(String emisor, String contenido, long tiempo) {
    this.emisor = emisor;
    this.contenido = contenido;
    this.tiempo = tiempo;
  }

  public String getEmisor() {
    return emisor;
  }

  public String getContenido() {
    return contenido;
  }

  public long getTiempo() {
    return tiempo;
  }
}
