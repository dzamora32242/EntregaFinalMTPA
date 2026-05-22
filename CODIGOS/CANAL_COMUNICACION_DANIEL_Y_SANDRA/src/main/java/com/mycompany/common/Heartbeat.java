package com.mycompany.common;

public class Heartbeat extends Mensaje {

    public Heartbeat() {
        super(Primitiva.HEARTBEAT);
    }

    @Override
    public String trasnformacionString() {
        return "HEARTBEAT";
    }
}
