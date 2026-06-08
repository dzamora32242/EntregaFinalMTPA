package com.mycompany.servidordifusion;

import java.util.ArrayList;

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

    public void agregarMensaje(MensajeUsuario mensaje) {
        mensajes.add(mensaje);
    }

    public String getNombre() {
        return nombre;
    }

    public int getNumeroUsuarios() {
        return clientes.size();
    }

    public int getNumeroMensajes() {
        return mensajes.size();
    }

    public ArrayList<MensajeUsuario> getMensajes() {
        return mensajes;
    }

    public ArrayList<ClienteDifusion> getClientes() {
        return clientes;
    }

    /**
     * Añade un cliente a la lista de miembros del salón si no está ya presente.
     * Sirve para registrar la entrada de un usuario a la sala de chat.
     * 
     * @param cliente El cliente que se va a añadir al salón.
     */
    public void meterCliente(ClienteDifusion cliente) {
        if (!clientes.contains(cliente)) {
            clientes.add(cliente);
        }
    }

    /**
     * Elimina a un cliente de la lista de miembros del salón.
     * Se utiliza cuando un usuario abandona el canal o se desconecta del servidor.
     * 
     * @param cliente El cliente que se va a retirar del salón.
     */
    public void sacarCliente(ClienteDifusion cliente) {
        clientes.remove(cliente);
    }

    /**
     * Comprueba si un cliente específico es miembro actual del salón.
     * 
     * @param cliente El cliente que se desea verificar.
     * @return true si el cliente está actualmente en el salón, false en caso contrario.
     */
    public boolean esMiembro(ClienteDifusion cliente) {
        return clientes.contains(cliente);
    }

    /**
     * Añade un nuevo mensaje al historial general de mensajes de este salón.
     * 
     * @param msg El mensaje de usuario que se desea almacenar.
     */
    public void addMensaje(MensajeUsuario msg) {
        mensajes.add(msg);
    }

    /**
     * Envía un mensaje a todos los clientes que están actualmente en el salón.
     * Sirve para propagar los mensajes de chat o notificaciones (ej. un usuario nuevo) a todos los miembros.
     * 
     * @param mensaje El objeto Mensaje que se va a difundir.
     */
    public void difundir(Mensaje mensaje) {
        for (ClienteDifusion cliente : new ArrayList<>(clientes)) {
            try {
                cliente.sendMessage(mensaje);
            } catch (Exception e) {
                System.out.println("Error difundiendo en salon " + nombre + ": " + e.getMessage());
            }
        }
    }

    /**
     * Obtiene los últimos mensajes enviados en el salón hasta alcanzar el límite especificado.
     * Sirve para proporcionar contexto inicial a los usuarios que acaban de unirse a la sala.
     * 
     * @param limite El número máximo de mensajes recientes que se desea recuperar.
     * @return Una cadena de texto con el historial reciente o "Sin mensajes" si está vacío.
     */
    public String getHistorialReciente(int limite) {
        int inicio = Math.max(0, mensajes.size() - limite);
        StringBuilder sb = new StringBuilder();
        for (int i = inicio; i < mensajes.size(); i++) {
            MensajeUsuario m = mensajes.get(i);
            sb.append(m.getEmisor()).append(": ").append(m.getContenido()).append("\n");
        }
        return sb.length() > 0 ? sb.toString() : "Sin mensajes";
    }

    /**
     * Obtiene todos los mensajes del salón que hayan sido enviados a partir de una fecha determinada.
     * 
     * @param fecha_solicitada El limite de tiempo desde el cual se quieren solicitar los mensajes.
     * @return Una cadena de texto con los mensajes o "No hay mensajes" si no hay resultados.
     */
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
