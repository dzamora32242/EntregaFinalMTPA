package com.mycompany.servidordifusion;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Salon {
  private String nombre;
  private ArrayList<MensajeUsuario> mensajes;
  private ArrayList<Socket> clientes;

  
  public Salon(String nombre) {
    this.nombre = nombre;
    mensajes = new ArrayList<>();
    clientes = new ArrayList<>();
  }

  public String getNombre() {
    return nombre;
  }

  public ArrayList<MensajeUsuario> getMensajes() {
    return mensajes;
  }

  public List<MensajeUsuario> getMensajes(int limite) {
    return mensajes.subList(0, limite);
  }

  public ArrayList<Socket> getClientes() {
    return clientes;
  }

  public void meterCliente(Socket cliente) {
    clientes.add(cliente);
  }

  public void sacarCliente(Socket cliente) {
    clientes.remove(cliente);
  }

  public String obtenerHistorialDesde (long fecha_solicitada){
    String historialStr = "";

    for (MensajeUsuario mensaje : mensajes){
      if (mensaje.getTiempo() >= fecha_solicitada){
        historialStr += mensaje.getEmisor() + ": " + mensaje.getContenido() + "\n";
      }
      if (historialStr.isEmpty()){
        return "No hay mensajes";
      }

    }
    return historialStr;
  }
}
