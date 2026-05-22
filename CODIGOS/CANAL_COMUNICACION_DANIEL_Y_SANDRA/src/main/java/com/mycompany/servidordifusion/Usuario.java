package com.mycompany.servidordifusion;

public class Usuario {
  private String usuario;
  private String contrasena;

  public Usuario(String usuario, String contrasena) {
    this.usuario = usuario;
    this.contrasena = contrasena;
  }
  

  public String getUsuario() {
    return usuario;
  }

  public String getContrasena() {
    return contrasena;
  }
}
