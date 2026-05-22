package com.mycompany.common;

public abstract class Mensaje {

    private Primitiva tipo;

    public Mensaje(Primitiva tipo) {
        this.tipo = tipo;
    }

    public Primitiva getTipo() {
        return tipo;
    }

    public abstract String toEncodedString();

    public static Mensaje fromEncodedString(String mensaje) {
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

            case "SEND_CHANNEL_MSG_REQ":
                return new SendChannelMsgReq(partes[1], partes[2], Boolean.parseBoolean(partes[3]));

            case "SEND_CHANNEL_NOTIFICATION":
                return new SendChannelNotification(partes[1], partes[2], partes[3]);

            case "SEND_PRIV_NOTIFICATION":
                return new SendPrivNotification(partes[1], partes[2], partes[3]);

            case "ERROR_RES":
                return new ErrorRes(partes[1]);

            default:
                throw new IllegalArgumentException("Tipo de mensaje desconocido: " + primitiva);
        }
    }
}
