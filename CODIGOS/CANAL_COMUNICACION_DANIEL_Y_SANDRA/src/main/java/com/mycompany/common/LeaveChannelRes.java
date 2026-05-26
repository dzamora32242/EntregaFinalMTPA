package com.mycompany.common;

public class LeaveChannelRes extends Mensaje {
    private boolean ok;
    private String mensaje;

    public LeaveChannelRes(boolean ok, String mensaje) {
        super(Primitiva.LEAVE_CHANNEL_RES);
        this.ok = ok;
        this.mensaje = mensaje;
    }

    public boolean isOk() {
        return ok;
    }

    public String getMensaje() {
        return mensaje;
    }

    @Override
    public String trasnformacionString() {
        return "LEAVE_CHANNEL_RES|" + ok + "|" + mensaje;
    }
}
