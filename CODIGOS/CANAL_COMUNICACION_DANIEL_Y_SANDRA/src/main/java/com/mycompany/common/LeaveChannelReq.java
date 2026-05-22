package com.mycompany.common;

public class LeaveChannelReq extends Mensaje {
    private String nombreUsuario;
    private String salon;

    public LeaveChannelReq(String nombreUsuario, String salon) {
        super(Primitiva.LEAVE_CHANNEL_REQ);
        this.nombreUsuario = nombreUsuario;
        this.salon = salon;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getSalon() {
        return salon;
    }

    @Override
    public String toEncodedString() { 
        return "LOGIN_RES|" + nombreUsuario + "|" + salon;
    }
}
