package com.mycompany.common;

public abstract class Mensaje {

    private Primitiva tipo;

    public Mensaje(Primitiva tipo) {
        this.tipo = tipo;
    }

    public Primitiva getTipo() {
        return tipo;
    }

    /**
     * Transforma la instancia actual del mensaje en una cadena de texto (String) formateada.
     * Será implementada por cada una de las primitivas segun sus datos pertinentes
     * 
     * @return Una cadena de texto que representa el mensaje serializado.
     */
    public abstract String trasnformacionString();

    /**
     * Reconstruye y devuelve un objeto de tipo Mensaje (o una de sus subclases) a partir de 
     * una cadena de texto recibida por la red. Este método actúa como deserializador, 
     * analizando la primera parte de la cadena para identificar el tipo de primitiva y 
     * el resto como partes para los atributos de la clase de la primitiva como tal
     * 
     * @param mensaje La cadena de texto recibida que contiene la información del mensaje y sus parámetros separados por '|'.
     * @return Una instancia concreta correspondiente a una de las clases derivadas de Mensaje.
     * @throws IllegalArgumentException Si la primitiva extraída de la cadena no es reconocida por el sistema.
     */
    public static Mensaje transformacionInstancia(String mensaje) {
        String[] partes = mensaje.split("\\|", -1);
        String primitiva = partes[0];

        switch (primitiva) {
            case "HEARTBEAT":
                return new Heartbeat();

            case "HEARTBEAT_ACK":
                return new HeartbeatACK();

            case "REGISTER_REQ":
                return new RegisterReq(partes[1]);

            case "REGISTER_RES":
                return new RegisterRes(Boolean.parseBoolean(partes[1]), partes[2]);

            case "LOGIN_REQ":
                return new LoginReq(partes[1], partes[2]);

            case "LOGIN_RES":
                return new LoginRes(Boolean.parseBoolean(partes[1]), partes[2]);

            case "LOGOUT_REQ":
                return new LogoutReq(partes[1]);

            case "LOGOUT_RES":
                return new LogoutRes(partes[1]);

            case "GET_CHANNELS_REQ":
                return new GetChannelsReq();

            case "GET_CHANNELS_RES":
                return new GetChannelsRes(Boolean.parseBoolean(partes[1]), partes[2]);

            case "JOIN_CHANNEL_REQ":
                return new JoinChannelReq(partes[1]);

            case "JOIN_CHANNEL_RES":
                return new JoinChannelRes(Boolean.parseBoolean(partes[1]), partes[2]);

            case "LEAVE_CHANNEL_REQ":
                return new LeaveChannelReq(partes[1], partes[2]);

            case "LEAVE_CHANNEL_RES":
                return new LeaveChannelRes(Boolean.parseBoolean(partes[1]), partes[2]);

            case "NOTIFY_JOIN":
                return new NotifyJoin(partes[1], partes[2]);

            case "SEND_CHANNEL_MSG_REQ":
                return new SendChannelMsgReq(partes[1], partes[2], Boolean.parseBoolean(partes[3]));

            case "SEND_CHANNEL_NOTIFICATION":
                return new SendChannelNotification(partes[1], partes[2], partes[3]);

            case "SEND_PRIV_NOTIFICATION":
                return new SendPrivNotification(partes[1], partes[2], partes[3]);

            case "ERROR_RES":
                return new ErrorRes(partes[1]);

            case "HISTORY_REQ":
                return new HistoryReq(partes[1], partes[2], Long.parseLong(partes[3]));

            case "HISTORY_RES":
                return new HistoryRes(partes[1], partes.length > 2 ? partes[2] : "");

            default:
                throw new IllegalArgumentException("Tipo de mensaje desconocido: " + primitiva);
        }
    }
}
