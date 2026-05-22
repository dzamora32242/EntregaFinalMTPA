package com.mycompany.common;

public class HeartbeatACK extends Mensaje {

    public HeartbeatACK() {
        super(Primitiva.HEARTBEAT_ACK);
    }

    @Override
    public String toEncodedString() {
        return "HEARTBEAT_ACK";
    }
}
