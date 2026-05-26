package com.mycompany.servidordifusion;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.common.Mensaje;

public class Salon {
    private String nombre;
    private ArrayList<MensajeUsuario> mensajes;
    private ArrayList<ClienteDifusion> clientes;

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

    public ArrayList<ClienteDifusion> getClientes() {
        return clientes;
    }

    public void meterCliente(ClienteDifusion cliente) {
        if (!clientes.contains(cliente)) {
            clientes.add(cliente);
        }
    }

    public void sacarCliente(ClienteDifusion cliente) {
        clientes.remove(cliente);
    }

    public boolean esMiembro(ClienteDifusion cliente) {
        return clientes.contains(cliente);
    }

    public void addMensaje(MensajeUsuario msg) {
        mensajes.add(msg);
    }

    public void difundir(Mensaje mensaje) {
        for (ClienteDifusion cliente : new ArrayList<>(clientes)) {
            try {
                cliente.sendMessage(mensaje);
            } catch (Exception e) {
                System.out.println("Error difundiendo en salon " + nombre + ": " + e.getMessage());
            }
        }
    }

    public String getHistorialReciente(int limite) {
        int inicio = Math.max(0, mensajes.size() - limite);
        StringBuilder sb = new StringBuilder();
        for (int i = inicio; i < mensajes.size(); i++) {
            MensajeUsuario m = mensajes.get(i);
            sb.append(m.getEmisor()).append(": ").append(m.getContenido()).append("\n");
        }
        return sb.length() > 0 ? sb.toString() : "Sin mensajes";
    }

    public String obtenerHistorialDesde(long fecha_solicitada) {
        StringBuilder sb = new StringBuilder();
        for (MensajeUsuario mensaje : mensajes) {
            if (mensaje.getTiempo() >= fecha_solicitada) {
                sb.append(mensaje.getEmisor()).append(": ").append(mensaje.getContenido()).append("\n");
            }
        }
        return sb.length() > 0 ? sb.toString() : "No hay mensajes";
    }
}
